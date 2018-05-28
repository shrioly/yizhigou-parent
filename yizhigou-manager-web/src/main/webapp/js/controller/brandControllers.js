app.controller("brandController",function($scope,$http,$controller,brandService){
    $controller("baseController",{$scope:$scope});//继承
    //读取品牌列表方法
    $scope.findAll=function(){
        //向后台发送http请求
        $http.get("../brand/findAll.do").success(
            function(response){
                $scope.list = response;
            })
    }

    //分页方法
    $scope.findPage=function(page,rows){
        $http.get('../brand/findPage.do?page='+page+"&rows="+rows).success(
            function(response){
                $scope.list = response.rows;
                //更新总条数
                $scope.paginationConf.totalItems=response.total;
            }
        );
    }
    //添加品牌
    $scope.save=function(){
        var Object = brandService.add($scope.entity);
        if($scope.entity.id!=null){
            Object=  brandService.update($scope.entity);
        }
        Object.success(
            function(response){
                if(response.success){
                    //刷新当前页面
                    $scope.reloadList();
                }else{
                    //保存失败提示
                    alert(response.message);
                }
            }
        )

    }

    //查询单条
    $scope.findOne=function(id){
        brandService.findOne(id).success(
            function(response){
                $scope.entity = response;
            }
        )
    }


    //向后台发送删除请求
    $scope.dele=function(){
        brandService.dele($scope.selectIds).success(
            function(response){
                if(response.success){
                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            }
        );
    }
    //定义一个搜索对象
    $scope.searchEntity={};
    $scope.search=function(page,rows){
        brandService.search($scope.searchEntity,page,rows).success(
            function(response){
                //更新总条数
                //更新总条数
                $scope.paginationConf.totalItems=response.total;
                $scope.list = response.rows;
            }
        )
    }



})