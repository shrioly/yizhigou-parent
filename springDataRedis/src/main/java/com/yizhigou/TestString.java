package com.yizhigou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext-redis.xml")
public class TestString {
@Autowired
    private RedisTemplate redisTemplate;

@Test
    public void setValue(){
    redisTemplate.boundValueOps("name").set("chtjava");

}
@Test
    public void getValue(){
    String str=(String) redisTemplate.boundValueOps("name").get();
    System.out.println(str);
}
@Test
    public void deleteValue(){
    redisTemplate.delete("name");
}
}
