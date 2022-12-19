package com.github.alenfive.datajellyfish.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alenfive.datajellyfish.entity.*;
import com.github.alenfive.datajellyfish.utils.MD5Utils;
import com.github.alenfive.datajellyfish.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/9 15:11
 * @UpdateDate: 2020/9/9 15:11
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Service
@Slf4j
public class ProducerService {

    //生产者任务待处理队列
    private final String producerPendingKey = "rocket-ssc:producer:queue:pending";

    private final String producerLockKey = "rocket-ssc:producer:lock";

    @Autowired
    private DataOperateService dataOperateService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisService redisService;


    @Autowired
    private WarningService warningService;

    public void taskReload() {
        //入生产者任务队列
        List<Topic> topicList = dataOperateService.listTopicAll();
        for (Topic topic : topicList){
            if (topic.getStatus() != null && topic.getStatus() == 0){
                continue;
            }
            for (ShardingTopic sharding : topic.getShardings()){
                String producerKey = topic.getStrId()+"_"+sharding.getKey();
                redisTemplate.opsForZSet().add(producerPendingKey,producerKey,System.currentTimeMillis());
            }
        }
    }

    public void execute(String producerKey){
        String topicId = producerKey.split("_")[0];
        String shardingTopicKey = producerKey.split("_")[1];
        Topic topic = dataOperateService.getTopic(topicId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        //清理无效任务
        if (topic == null || topic.getStatus() == 0){
            redisTemplate.opsForZSet().remove(producerPendingKey,producerKey);
            return;
        }

        ShardingTopic shardingTopic = topic.getShardings().stream().filter(item->item.getKey().equals(shardingTopicKey)).findFirst().orElse(null);
        //清理无效任务
        if (shardingTopic == null){
            log.warn("topic:{},sharding is empty",producerKey);
            redisTemplate.opsForZSet().remove(producerPendingKey,producerKey);

            //推送告警
            warningService.dingTalkText(topic.getWarningUrl(),"producerKey:"+producerKey,topic.getUrl(),null,null,"producer sharding is empty");

            return;
        }

        List<Consumer> consumerList = dataOperateService.findConsumerByTopic(topicId);
        if (consumerList.isEmpty()){
            log.warn("topic:{},consumerList is empty",producerKey);
            redisTemplate.opsForZSet().add(producerPendingKey,producerKey,System.currentTimeMillis()+60*1000);

            //推送告警
            warningService.dingTalkText(topic.getWarningUrl(),"producerKey:"+producerKey,topic.getUrl(),null,null,"consumerList is empty");

            return;
        }

        Map<String,Object> params = new HashMap<>();
        SSCProducerResult result = null;
        String resultStr = null;
        try {

            params.put("producerKey",producerKey);
            params.put("offset",shardingTopic.getOffset());
            params.put("id",shardingTopic.getuId());
            if (!StringUtils.isEmpty(shardingTopic.getFilter())){
                params.putAll(objectMapper.readValue(shardingTopic.getFilter(),Map.class));
            }

            resultStr = restTemplate.postForObject(topic.getUrl(),params,String.class);
            result = objectMapper.readValue(resultStr, SSCProducerResult.class);
            List<Map<String,Object>> sscData = result.getData();
            if (!"0".equals(result.getCode())){
                throw new RuntimeException("三方返回格式异常");
            }
            log.debug("producerKey:{},request url:{} params:{},result size:{}",producerKey,topic.getUrl(),params, CollectionUtils.isEmpty(sscData)?0:sscData.size());

            if (CollectionUtils.isEmpty(sscData)){
                redisTemplate.opsForZSet().add(producerPendingKey,producerKey,System.currentTimeMillis()+topic.getDelay()*1000);
                return;
            }

            List<TopicRecord> topicRecordList = sscData.stream().map(item-> TopicRecord.builder()
                    .id(ObjectId.get())
                    .uId(null)
                    .dt(Integer.valueOf(sdf.format(new Date())))
                    .createTime(new Date())
                    .updateTime(new Date())
                    .isSync(0)
                    .times(0)
                    .content(item)
                    .lastResult("init")
                    .build()).collect(Collectors.toList());

            //数据分发
            for (Consumer consumer : consumerList){
                Date nextTime = buildNextTime(consumer);
                for (TopicRecord item : topicRecordList){

                    //consumer 订阅过滤
                    boolean isTrue = subscribeFilter(item.getContent(),consumer.getSubscribeFilter());
                    if (!isTrue){
                        continue;
                    }

                    item.setNextTime(nextTime);
                    item.setUId(MD5Utils.md5(buildUniqueField(item.getContent(),consumer.getUniqueFields())));
                    boolean exists = dataOperateService.existsTopicRecord(item.getUId(),consumer.getTable());
                    if (!exists){
                        dataOperateService.saveTopicRecord(item,consumer.getTable());
                    }
                }
            }

            TopicRecord lastTopicRecord = topicRecordList.get(topicRecordList.size()-1);

            Object newOffset = lastTopicRecord.getContent().get(topic.getOffsetFields().getOffset());
            if (StringUtils.isEmpty(newOffset)){
                throw new IllegalArgumentException("offset field `"+topic.getOffsetFields().getOffset()+"` value is empty");
            }
            Object newUId = lastTopicRecord.getContent().get(topic.getOffsetFields().getuId());

            if (StringUtils.isEmpty(newUId)){
                throw new IllegalArgumentException("uId field `"+topic.getOffsetFields().getuId()+"` value is empty");
            }

            //偏移量更新
            dataOperateService.updateOffset(topicId,shardingTopicKey,shardingTopic.getOffset(),newOffset.toString(),newUId.toString());

            redisTemplate.opsForZSet().add(producerPendingKey,producerKey,System.currentTimeMillis()+topic.getDelay()*1000);
        }catch (Throwable e){
            //发生异常，暂定1分钟处理
            redisTemplate.opsForZSet().add(producerPendingKey,producerKey,System.currentTimeMillis()+60*1000);
            log.error("producerKey:{},request url:{} params:{},result {}",producerKey,topic.getUrl(),params,e.getMessage());

            //推送告警
            warningService.dingTalkText(topic.getWarningUrl(),"producerKey:"+producerKey,topic.getUrl(),params,null,e.getMessage());

            e.printStackTrace();
        }
    }

    private boolean subscribeFilter(Map<String, Object> content, String subscribeFilter) throws IOException {
        if (StringUtils.isEmpty(subscribeFilter) || "{}".equals(subscribeFilter.trim())){
            return true;
        }
        Map<String,Object> filter = objectMapper.readValue(subscribeFilter,Map.class);
        for (String key : filter.keySet()){
            if (!filter.get(key).equals(content.get(key))){
                return false;
            }
        }
        return true;
    }

    /**
     * 计算唯一去重字段
     * @param content
     * @param uniqueFields
     * @return
     * @throws JsonProcessingException
     */
    private String buildUniqueField(Map<String,Object> content,String uniqueFields) throws IOException {
        uniqueFields = uniqueFields.trim();
        List<String> fields = null;
        if ("*".equals(uniqueFields)){
            fields = content.keySet().stream().collect(Collectors.toList());
        }else{
            fields = Arrays.asList(uniqueFields.split(","));
        }


        Map<String,Object> result = new HashMap<>();
        for (String field : fields){
            Object value = content.get(field.trim());
            if (!"*".equals(uniqueFields) && StringUtils.isEmpty(value)){
                throw new IllegalArgumentException("unique field `"+field+"` value is empty");
            }
            result.put(field,value);
        }

        return objectMapper.writeValueAsString(result);
    }


    private Date buildNextTime(Consumer consumer) {
        if (consumer.getIsFirstDelay() == 0){
            return new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,consumer.getDelay());
        return calendar.getTime();
    }

    public void run() {
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuNumber*2);
        executor.setMaxPoolSize(cpuNumber*2);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("producer-");
        executor.initialize();

        while (true){
            try {
                executor.execute(() -> {
                    String producerKey = null;
                    Integer times = 0;
                    long step = 10000L;
                    long maxTimes = (60*1000)/step;
                    while (true){

                        //互斥 kill -9 || redis 阻塞时
                        Long lockValue = System.currentTimeMillis() + 60*1000;
                        boolean isLock = redisService.lock(producerLockKey,lockValue.toString());
                        if (isLock) {
                            try {
                                Set<String> topList = redisTemplate.opsForZSet().rangeByScore(producerPendingKey,0,System.currentTimeMillis(),0,1);
                                if (topList.iterator().hasNext()){
                                    producerKey = topList.iterator().next();
                                    //允许任务最大处理时长为10分钟
                                    redisTemplate.opsForZSet().add(producerPendingKey,producerKey,System.currentTimeMillis() + 10*60*1000);
                                    break;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                redisService.unlock(producerLockKey,lockValue.toString());
                            }
                        }

                        ThreadUtils.sleep(step);
                        times ++;
                        if (times >= maxTimes){
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(producerKey)){
                        log.warn("idle time 1 minute");
                        return;
                    }
                    this.execute(producerKey);
                });
            }catch (RejectedExecutionException e){
                ThreadUtils.sleep(1000L);
            }catch (Throwable e){
                e.printStackTrace();
                ThreadUtils.sleep(1000L);
            }
        }
    }


}
