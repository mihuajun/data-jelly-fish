package com.github.alenfive.datajellyfish.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alenfive.datajellyfish.entity.Consumer;
import com.github.alenfive.datajellyfish.entity.SSCResult;
import com.github.alenfive.datajellyfish.entity.ShardingConsumer;
import com.github.alenfive.datajellyfish.entity.TopicRecord;
import com.github.alenfive.datajellyfish.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;

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
@Slf4j
@Service
public class ConsumerService {

    //消费者任务待处理队列
    private final String consumerPendingKey = "rocket-ssc:consumer:queue:pending";

    private final String consumerLockKey = "rocket-ssc:consumer:lock";

    @Autowired
    private MongoTemplate mongoTemplate;

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

    public List<Consumer> listAll() {
        return mongoTemplate.findAll(Consumer.class);
    }

    public void save(Consumer consumer) {
        mongoTemplate.save(consumer);
    }
    public Consumer getById(String id){
        return mongoTemplate.findById(new ObjectId(id),Consumer.class);
    }
    public void taskReload() {
        //入消费者任务队列
        List<Consumer> consumerList = this.listAll();
        for (Consumer consumer : consumerList){
            if (consumer.getStatus() != null && consumer.getStatus() == 0){
                continue;
            }
            for (ShardingConsumer sharding : consumer.getShardings()){
                String consumerKey = consumer.getStrId() +"_"+sharding.getKey();
                redisTemplate.opsForZSet().add(consumerPendingKey,consumerKey,System.currentTimeMillis());
            }
        }

    }

    public List<Consumer> findByTopic(String topicId) {
        return mongoTemplate.find(Query.query(Criteria.where("topicId").is(new ObjectId(topicId))),Consumer.class);
    }

    public void execute(String consumerKey){
        String consumerId = consumerKey.split("_")[0];
        String shardingConsumerKey = consumerKey.split("_")[1];

        Consumer consumer = this.getById(consumerId);
        //清理无效任务
        if (consumer == null || consumer.getStatus() == 0){
            redisTemplate.opsForZSet().remove(consumerPendingKey,consumerKey);
            return;
        }

        ShardingConsumer shardingConsumer = consumer.getShardings().stream().filter(item->item.getKey().equals(shardingConsumerKey)).findFirst().orElse(null);
        //清理无效任务
        if (shardingConsumer == null){
            redisTemplate.opsForZSet().remove(consumerPendingKey,consumerKey);
            //推送告警
            warningService.dingTalkText(consumer.getWarningUrl(),"producerKey:"+consumerKey,consumer.getUrl(),null,null,"consumer sharding is empty");
            return;
        }

        //构建分片数据过滤条件
        Criteria criteria = Criteria.where("nextTime").lte(new Date()).and("isSync").is(0).and("times").lt(consumer.getMaxTimes());
        Document document = criteria.getCriteriaObject();
        buildContentFilter(document,shardingConsumer.getFilter());
        Query query = new BasicQuery(document).limit(100);

        List<TopicRecord> topicRecordList = mongoTemplate.find(query,TopicRecord.class,consumer.getTable());


        if (CollectionUtils.isEmpty(topicRecordList)){
            redisTemplate.opsForZSet().add(consumerPendingKey,consumerKey,System.currentTimeMillis()+1*1000);
            return;
        }

        String resultStr = null;
        SSCResult result = null;
        Map<String,Object> params = null;
        String id = null;

        for (TopicRecord topicRecord : topicRecordList){

            try {
                params = topicRecord.getContent();
                id = topicRecord.getId().toHexString();
                String url = consumer.getUrl();
                if (StringUtils.isEmpty(url)){
                    throw new IllegalArgumentException("URL参数为空");
                }
                log.info("request url:{},params:{}",url,params);
                resultStr = restTemplate.postForObject(url,params,String.class);
                result = objectMapper.readValue(resultStr, SSCResult.class);
                topicRecord.setLastResult(objectMapper.readValue(resultStr, Map.class));
                //清理异常信息
                redisService.setErrMsg(consumer.getTable(),"正常");
            }catch (RestClientException e){
                //发生异常，暂定1分钟处理
                redisTemplate.opsForZSet().add(consumerPendingKey,consumerKey,System.currentTimeMillis()+60*1000);
                log.error("consumerKey:{},request url:{} params:{},result:{}",consumerKey,consumer.getUrl(),params,resultStr);

                //暂存异常信息
                redisService.setErrMsg(consumer.getTable(),e.getMessage() +" 关注数据： {\"_id\":{\"$oid\":\""+id+"\"}}");

                //推送告警
                warningService.dingTalkText(consumer.getWarningUrl(),"producerKey:"+consumerKey,consumer.getUrl(),params,resultStr,e.getMessage());
                return;
            } catch (Throwable e) {
                topicRecord.setLastResult(e.getMessage());
            }

            log.debug("consumerKey:{},request url:{} params:{},result:{}",consumerKey,consumer.getUrl(),params,resultStr);

            if (result != null && "0".equals(result.getCode())){
                topicRecord.setIsSync(1);
            }
            topicRecord.setShardingKey(shardingConsumerKey);
            topicRecord.setTimes(topicRecord.getTimes() + 1);
            topicRecord.setUpdateTime(new Date());
            topicRecord.setNextTime(buildNextTime(consumer,topicRecord));

            mongoTemplate.save(topicRecord,consumer.getTable());
        }

        redisTemplate.opsForZSet().add(consumerPendingKey,consumerKey,System.currentTimeMillis());

    }

    /**
     * 构建mongodb查询数据集中content的内容
     * @param document
     * @param filter
     */
    public void buildContentFilter(Document document, String filter) {
        Document shardingConsumerFilter = Document.parse(filter);
        for(String key :shardingConsumerFilter.keySet()){
            if(key.equals("_id")){
                document.put(key,shardingConsumerFilter.get(key));
                continue;
            }
            document.put("content."+key,shardingConsumerFilter.get(key));
        }
    }

    public void run() {
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuNumber*2);
        executor.setMaxPoolSize(cpuNumber*2);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("consumer-");
        executor.initialize();

        while (true){
            try {
                executor.execute(() -> {
                    String consumerKey = null;
                    Integer times = 0;
                    long step = 10000L;
                    long maxTimes = (60*1000)/step;
                    while (true){

                        //互斥 kill -9 || redis 阻塞时
                        Long lockValue = System.currentTimeMillis() + 60*1000;
                        boolean isLock = redisService.lock(consumerLockKey,lockValue.toString());
                        if (isLock) {
                            try {
                                Set<String> topList = redisTemplate.opsForZSet().rangeByScore(consumerPendingKey,0,System.currentTimeMillis(),0,1);
                                if (topList.iterator().hasNext()){
                                    consumerKey = topList.iterator().next();
                                    //允许处理时长1小时
                                    redisTemplate.opsForZSet().add(consumerPendingKey,consumerKey,System.currentTimeMillis() + 10*60*1000);
                                    break;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                redisService.unlock(consumerLockKey,lockValue.toString());
                            }
                        }

                        ThreadUtils.sleep(step);
                        times ++;
                        if (times >= maxTimes){
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(consumerKey)){
                        log.warn("redis query timeout 1 minutes");
                        return;
                    }
                    this.execute(consumerKey);
                });
            }catch (RejectedExecutionException e){
                ThreadUtils.sleep(1000L);
            }catch (Throwable e){
                e.printStackTrace();
                ThreadUtils.sleep(1000L);
            }
        }
    }

    private Date buildNextTime(Consumer consumer, TopicRecord topicRecord) {
        Calendar calendar = Calendar.getInstance();
        if (consumer.getIsExponent() == 0){
            calendar.add(Calendar.SECOND,consumer.getDelay());
            return calendar.getTime();
        }
        Integer delay = (1 << (topicRecord.getTimes()-1))*consumer.getDelay();
        calendar.add(Calendar.SECOND,delay);
        return calendar.getTime();
    }

}
