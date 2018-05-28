package com.yizhigou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext-redis.xml")
public class TestHash {
    @Autowired
    private RedisTemplate redisTemplate;
   @Test
    public void setHash(){
       redisTemplate.boundHashOps("h1").put("a1","孙悟空");
       redisTemplate.boundHashOps("h1").put("a2","二师兄");
       redisTemplate.boundHashOps("h1").put("a3","沙师弟");
   }
   @Test
    public void getValue(){
       Set h1 = redisTemplate.boundHashOps("h1").keys();
       List list = redisTemplate.boundHashOps("h1").values();
       System.out.println(list);
   }

   @Test
    public void getValueOne(){
       String str = (String)redisTemplate.boundHashOps("h1").get("a1");
       System.out.println(str);
   }
   @Test
    public void deleteValue(){
       redisTemplate.boundHashOps("h1").delete("a1");
   }
   @Test
    public void deleteAll(){
       redisTemplate.delete("h1");
   }
}
