package com.yizhigou.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;
    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){
//        String mobile 手机号,String template_code 模板编号,String sign_name 签名,String param 内容
        try {
          SendSmsResponse response= smsUtil.sendSms(
                    map.get("mobile"),
                    map.get("template_code"),
                    map.get("sign_name"),
                    map.get("param")
                    );
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
