package com.yizhigou.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping("/showName")
    public String showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取登录名
        return name;
    }
}
