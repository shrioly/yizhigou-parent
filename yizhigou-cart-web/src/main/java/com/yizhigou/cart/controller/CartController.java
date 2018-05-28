package com.yizhigou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yizhigou.cart.service.CartService;
import com.yizhigou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    //显示购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("userName========"+userName);
        //从cookie中取商品信息
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null||cartListString.equals("")){
            cartListString="[]";//为了不报空指针
        }
        List<Cart> cartList_cookie=JSON.parseArray(cartListString,Cart.class);
        if(userName.equals("anonymousUser")){
            System.out.println("通过cookie查询出购物车");
            //读取本地购物车
            //从cookie中获取商品信息
          return cartList_cookie;
        }else{
            List<Cart> cartList_redis=cartService.findCartListFromRedis(userName);
            if(cartList_cookie.size()>0) {
                cartList_redis = cartService.margeCartList(cartList_redis, cartList_cookie);
                //清除cookie中的信息
                CookieUtil.deleteCookie(request, response, "cartList");
                //合并后的数据保存到redis中
                cartService.saveCartListToRedis(cartList_redis, userName);
            }
            return cartList_redis;
        }

    }
    //添加购物车 itemId num
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(Long itemId,Integer num) {
/*        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");//设置打开cookie操作*/


        //获取登录名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

            try {
                //1 获取购物车列表
                List<Cart> cartList = findCartList();
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                if (userName.equals("anonymousUser")) {
                    System.out.println("通过cookie存储到购物车....");
                //存入到cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                }else{ //用户已经登录
                    cartService.saveCartListToRedis(cartList,userName);
                }
                return new Result(true, "添加购物车成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(false, "添加购物车失败");
            }
        }

}
