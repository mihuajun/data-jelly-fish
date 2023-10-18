package com.github.alenfive.datajellyfish.controller;

import com.github.alenfive.datajellyfish.security.ProfileService;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.ldap.profile.service.LdapProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private ProfileService profileService;

    @Autowired
    private Config config;

    @GetMapping("/view/{page}")
    public ModelAndView viewAdminIndex(@PathVariable String page) {

        Map<String,Object> model = new HashMap<>();
        model.put("profile", profileService.getSessionProfile());
        return new ModelAndView("/data-jelly-fish/"+page,model);
    }

    @GetMapping("/{page}")
    public String viewIndex(@PathVariable String page){
        return page;
    }

    @GetMapping("/login")
    public String loginForm(Model model){
        final FormClient formClient = (FormClient) config.getClients().findClient("FormClient").get();
        model.addAttribute("callbackUrl", formClient.getCallbackUrl()+"?client_name=FormClient");
        return "login";
    }

    @GetMapping("/")
    public String viewAdminIndex(){
        return "redirect:/view/admin";
    }
}
