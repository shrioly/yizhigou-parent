package com.yizhigou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.yizhigou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
        @Value("${appid}")
         private String appid;
        @Value("${partner}")
        private String partner;
        @Value("${partnerkey}")
        private String partnerkey;

    /**
     * 生成二维码
     * @param out_trade_no 订单号
     * @param total_fee 金额(分)
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
       //1 创建参数
        Map<String,String> param=new HashMap();//创建参数
        param.put("appid",appid);//公众号
        param.put("mch_id",partner);//商户号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        param.put("body","易直购");//商品描述
        param.put("out_trade_no",out_trade_no);//商品订单号
        param.put("total_fee",total_fee);//总金额（分）
        param.put("spbill_create_ip","127.0.0.1");//IP
        param.put("notify_url","http://www.jd.com");//回调地址（随便写）
        param.put("trade_type","NATIVE ");//交易类型
        //生成要发送的xml
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("发送的数据：<--------"+xmlParam+"---------->");
            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            //3 获得结果
            String result = client.getContent();
            System.out.println("请求返回的数据<--------"+result+"---------->");
            Map<String,String> resultMap=WXPayUtil.xmlToMap(result);
            Map<String,String> map=new HashMap<>();
            map.put("code_url",resultMap.get("code_url"));//trade_type为NATIVE时有返回，用于生成二维码，展示给用户进行扫码支付
            map.put("total_fee",total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param=new HashMap<>();
        param.put("appid",appid);//公众号id
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",out_trade_no);//订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/orderquery";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println("将查询结果转换为map格式===="+map+"---->");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
