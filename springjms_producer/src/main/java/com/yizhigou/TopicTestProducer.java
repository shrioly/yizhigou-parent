package com.yizhigou;

import com.yizhigou.demo.QueueProducer;
import com.yizhigou.demo.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-jms-producer.xml ")
public class TopicTestProducer {
    @Autowired
    private TopicProducer topicProducer;
    @Test
    public void testProducer(){
        topicProducer.sendTestMessage("欢迎使用spring整合JMS!!---订阅式");
    }
}
