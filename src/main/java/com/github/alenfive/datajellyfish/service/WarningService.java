package com.github.alenfive.datajellyfish.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.alenfive.datajellyfish.entity.DingTalkEntity;
import com.github.alenfive.datajellyfish.entity.DingTalkTextEntity;
import com.github.alenfive.datajellyfish.entity.WarningEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/12/28 20:24
 * @UpdateDate: 2020/12/28 20:24
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Service
@Slf4j
public class WarningService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void dingTalkText(String warningUrl, String taskId, String url, Object params, Object result,String message){
        if (StringUtils.isEmpty(warningUrl)){
            return;
        }

        WarningEntity warningEntity = WarningEntity.builder()
                .taskId(taskId)
                .url(url)
                .params(params)
                .result(result)
                .message("告警"+message)
                .build();
        try {
            String content = objectMapper.writeValueAsString(warningEntity);
            DingTalkEntity dingTalkEntity = DingTalkEntity.builder()
                    .msgtype("text")
                    .text(DingTalkTextEntity.builder()
                            .content(content)
                            .build())
                    .build();
            ResponseEntity<Object> warningResult = restTemplate.postForEntity(warningUrl,dingTalkEntity,Object.class);
            log.error("warning send result:{}",warningResult.getBody());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
