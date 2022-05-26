package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.service.SSCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private SSCService sscService;

    /**
     * 任务加载
     */
    @GetMapping("/reload")
    public void reload() {
        sscService.taskReload();
    }


}
