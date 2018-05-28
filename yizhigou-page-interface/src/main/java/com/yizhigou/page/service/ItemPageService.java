package com.yizhigou.page.service;

public interface ItemPageService {
    //生产静态页面
    public boolean genItemHtml(Long goodsId);
    //删除页面
    public  boolean deleteItemHtml(Long[] goodsId);
}
