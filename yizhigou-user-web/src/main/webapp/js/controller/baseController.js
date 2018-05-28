app.controller("baseController",function($scope){
    $scope.reloadList=function(){
        // $scope.findPage( $scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,//当前页
        totalItems: 10,//总条数
        itemsPerPage: 10,//每页显示的数量
        perPageOptions: [10, 20, 30, 40, 50],//分页选项
        onChange: function(){  //加载分页
            //传递两个参数
            $scope.reloadList();
        }
    };

    //删除功能
    //1.创建一个数组,这个数据专用于存储选中的id
    $scope.selectIds=[];
    //触发选中的事件
    $scope.updateSelection=function($event,id){
        if( $event.target.checked){
            $scope.selectIds.push(id);
        }else{
            //获取到当前选中的这个值得下标
            var  idx = $scope.selectIds.indexOf(id);//返回当前选中id的下标值
            //移除操作
            $scope.selectIds.splice(idx,1);//第一个参数 移除的下标值    第二个参数,移除的个数
        }
    }

    //提取Json串中的 属性,展示
    $scope.jsonToString=function(jsonString,key){
        var json = JSON.parse(jsonString);//转换成为json对象
        var value="";
        for (var i=0;i<json.length;i++){
            if(i>0){
                value +=","
            }
            value +=json[i][key];
        }
        return value;
    }
})