package com.yizhigou.cart.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.yizhigou.mapper.TbItemMapper;
import com.yizhigou.pojo.TbItem;
import com.yizhigou.pojo.TbOrderItem;
import com.yizhigou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements  CartService{
    @Autowired
    private TbItemMapper itemMapper;

    /**
     *
     * @param cartList 购物车中信息
     * @param itemId 商品skuid
     * @param num 购买数量
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //根据商品id查询出商品详情
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断
        if(item==null){
            throw new RuntimeException("商品不存在！");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }
        //2获取到商家id
        String sellerId = item.getSellerId();
        Cart cartLists=null;
        //3 根据商家id判断购物车当中是否存在该商家的购物车
        Cart cart = searchCartBySellerId(cartList, sellerId);
        if(cart==null){
        //4 如果购物车列表中不存在该商家的购物车
            cart=new Cart();
          //4.1新建购物车
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem=createOrderItem(item,num);//创建购物车明细
            System.out.println("购物车列表中不存在该商家的购物车");
         //4.2 将新建的购物车信息，添加到购物车列表中
            List orderItemList=new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //4.2将购物车对象添加到购物车列表
            cartList.add(cart);

        }else{

        //5 购物车已经存在该商家的购物车
        TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), item.getId());
        //如何进行比较 新传递进来的购物车，和已经存在的购物车车信息进行比较
        if(orderItem==null){
            //5.2如果没有购物车商品，新增购物车明细
            orderItem=createOrderItem(item,num);
            //添加到购物车明细
            cart.getOrderItemList().add(orderItem);
        }else{
            //5.1查询购物车中是否有该商品，直接改变数量
            orderItem.setNum(orderItem.getNum()+num);
            //更改价格
            orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
            //如果购物车数量操作后小于0，移除
            if(orderItem.getNum()<=0){
                cart.getOrderItemList().remove(orderItem);
            }
            //如果移除后，cart数据为0，把该商家购物车移除掉
            if(cart.getOrderItemList().size()==0){
            cartList.remove(cart);
            }
        }
    }


        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        System.out.println("从redis中提取购物车数据....."+userName);
        List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
        if(cartList==null){
            return new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(List<Cart> cartList, String userName) {
            System.out.println("购物车存入到redis中....");
            redisTemplate.boundHashOps("cartList").put(userName,cartList);

    }
    //合并购物车

    @Override
    public List<Cart> margeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车。。。。");
        for (Cart cart:cartList2){
            //获取购物车信息
            for (TbOrderItem orderItem:cart.getOrderItemList()){
                //集合1 和集合2 进行 合并 如果集合里已经有的数据在集合2中吧数量进行修改
                cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }


        return cartList1;
    }

    /**
     *
     * @param orderItemList 商品明细
     * @param itemId    商品skuid
     * @return  判断购物车是否有该商品
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for(TbOrderItem orderItem :orderItemList){
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }


    //创建订单明细
    private TbOrderItem  createOrderItem(TbItem item,Integer num){
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }


    /**
     *
     * @param cartList
     * @param sellerId
     * @return  根据商家id判断购物车列表中是否有该商家购物车，有返回购物车，没有返回null
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart:cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

}
