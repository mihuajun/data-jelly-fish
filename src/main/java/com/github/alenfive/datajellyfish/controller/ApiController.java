package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.service.ConsumerService;
import com.github.alenfive.datajellyfish.service.ProducerService;
import com.github.alenfive.datajellyfish.service.DataJellyFishService;
import com.github.alenfive.datajellyfish.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/9/9 15:20
 * @UpdateDate: 2020/9/9 15:20
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private DataJellyFishService dataJellyFishService;

    /**
     * 任务加载
     */
    @GetMapping("/task-reload")
    public void taskReload() {
        dataJellyFishService.taskReload();
    }

}
