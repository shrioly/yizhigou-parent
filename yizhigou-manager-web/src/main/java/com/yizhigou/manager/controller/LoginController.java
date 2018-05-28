package com.yizhigou.manager.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/name")
    public Map name(){
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录的用户名
       Map map = new HashMap();
       map.put("loginName",loginName);
       return map;
    }
}
