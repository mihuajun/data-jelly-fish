package com.github.alenfive.datajellyfish.config;

import com.github.alenfive.datajellyfish.service.DataOperateService;
import com.github.alenfive.datajellyfish.service.data.MongoDataOperateService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/10/16 21:32
 * @UpdateDate: 2020/10/16 21:32
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Configuration
public class BeanConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    public DataOperateService getDataOperateService(){
        return new MongoDataOperateService();
    }
}
