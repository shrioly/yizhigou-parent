//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    //添加商品到购物车
    this.addGoodsToCartList=function (itemId,num) {
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    this.sum=function (cartList) {
        //定义下对象，用来存放购买数量还有总价格
        var totalValue={totalNum:0,totalMoney:0.00};
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cart.orderItemList.length;j++){
                var orderItem=cart.orderItemList[j];//购物车明细
                //计算总数量
                totalValue.totalNum+=orderItem.num;
                //计算总金额
                totalValue.totalMoney+=orderItem.totalFee;
            }
        }
        return totalValue;
    }

    this.findListByLoginUser=function () {
        return $http.get("address/findListByLoginUser.do");
    }

    //保存订单
    this.submitOrder=function (order) {
        return $http.post('order/add.do',order);
    }
});