package com.github.alenfive.datajellyfish.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Copyright: Copyright (c) 2019  ALL RIGHTS RESERVED.
 * @Author: 米华军
 * @CreateDate: 2020/12/22 11:32
 * @UpdateDate: 2020/12/22 11:32
 * @UpdateRemark: init
 * @Version: 1.0
 * @menu
 */
@RequestMapping("")
@Controller
public class ViewAdminController {

    @GetMapping("/view/{page}")
    public String viewAdminIndex(@PathVariable String page){
        return "data-jelly-fish/"+page;
    }

    @GetMapping("/")
    public String viewAdminIndex(){
        return "redirect:/view/admin";
    }
}
