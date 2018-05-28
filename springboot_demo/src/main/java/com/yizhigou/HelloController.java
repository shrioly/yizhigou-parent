package com.yizhigou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private Environment env;
    @RequestMapping("/info")
    public String showInfo(){
        return "hello world"+env.getProperty("url");
    }
}
