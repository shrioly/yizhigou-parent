package com.yizhigou;

import com.yizhigou.demo.QueueProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-jms-producer.xml ")
public class QueueTestProducer {
    @Autowired
    private QueueProducer queueProducer;
    @Test
    public void testProducer(){
        queueProducer.sendTestMessage("欢迎使用spring整合JMS!!");
    }
}
