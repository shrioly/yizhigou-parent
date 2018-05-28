package com.yizhigou.cart.service;

import com.yizhigou.pojogroup.Cart;

import java.util.List;

public interface CartService {
    //添加到购物车中
    //需要的信息，商家id 商品id 商品数量 购物车明细
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     *
     * @param userName
     * @return 读取redis购物车
     */
    public List<Cart> findCartListFromRedis(String userName);

    /**
     *
     * @param cartList
     * @param userName 购物车信息存入redis中
     */
    public void saveCartListToRedis(List<Cart> cartList,String userName);

    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> margeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
