package com.yizhigou;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Consumer {
    @JmsListener(destination = "jd_test")
    public void readMessage(String text){
        System.out.println("接受到的信息"+text);
    }
    @JmsListener(destination = "jd_map")
    public  void readMap(Map map){
        System.out.println(map);
    }

}
