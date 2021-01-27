package com.github.alenfive.datajellyfish.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/9 14:12
 * @UpdateDate: 2020/9/9 14:12
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@Service
public class DataJellyFishService {

    @Autowired
    private ProducerService producerService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private TopicService topicService;

    /**
     * 任务初始化
     * @todo 执行中任务初始化问题
     */
    public void taskReload() {
        producerService.taskReload();
        consumerService.taskReload();
    }

    public void run() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(0);
        executor.initialize();
        executor.execute(()->{
            producerService.run();
        });
        executor.execute(()->{
            consumerService.run();
        });

    }
}
