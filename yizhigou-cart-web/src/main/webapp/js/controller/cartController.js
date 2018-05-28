//购物车控制层
app.controller('cartController',function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                //调用sum计算总价格和总数量
                $scope.totalValue=cartService.sum($scope.cartList);
            }
        );
    }
    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if(response.success){
                    $scope.findCartList();//刷新购物车列表
                }else{
                    alert(response.message);
                }
            }
        )
    }

    $scope.findListByLoginUser=function () {
        cartService.findListByLoginUser().success(
            function (response) {
                $scope.addressList=response;
                //设置默认地址选中
                //1 循环addressList 找到 isDefault这个属性等于1的这条数据
                //2 把这条数据给address
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }
    //判断是否选中
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }

    //判断是否选中当前的地址
    $scope.isSelectedAddress=function (address) {
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }


    //保存订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    //页面跳转
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果是货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else {
                    alert(response.message);//也可以跳转到提示页面
                }
            }
        )
    }
});