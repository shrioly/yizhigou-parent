package com.yizhigou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueueController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @RequestMapping("/send")
    public void send(String text){
        jmsMessagingTemplate.convertAndSend("jd_test",text);

    }
    @RequestMapping("/sendMap")
    public void sendMap(){
        Map map=new HashMap<>();
        map.put("mobile","13581530428");
        map.put("template_code","SMS_135036951");
        map.put("sign_name","枯木扶疏");
        map.put("param","{\"name\":\"57958\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);
    }
}
