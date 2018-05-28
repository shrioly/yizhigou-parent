app.controller("searchController",function ($scope,$http,searchService,$location) {
//接受传过来的搜索关键字
$scope.loadkeywords=function(){
    $scope.searchMap.keywords=$location.search()['keywords'];
    $scope.search();
}

    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);// 在执行查询前，将页码转换为int类型，否则提交到后端有可能变成字符串
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;//返回搜索数据
                buildPageLabel();
            }
        )
    }
    buildPageLabel=function(){
        //循环页码
        $scope.pageLabel=[];
        //获取总页数
        var maxPageNo=$scope.resultMap.totalPages;
        //开始页
        var firstPage=1;
        var lastPage=maxPageNo;//截止页
        //显示省略号
        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后面有点

        //如果总页数大于5页
        if($scope.resultMap.totalPages>5){
            if($scope.searchMap.pageNo<=3){//如果当前页小于等于3
                lastPage=5;//显示前5页
                $scope.firstDot=false;//前面没有点
            }else if($scope.resultMap.pageNo>=$scope.resultMap.totalPages-2){//最后5页
                firstPage=$scope.resultMap.totalPages-4;
                $scope.lastDot=false;//后面没点
            }else {//显示当前为中心的5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后面无点
        }
        console.info("首页"+firstPage+"=====");
        console.info("尾页"+lastPage+"=====");
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }
    //提交查询
    $scope.queryByPage=function(pageNo){
        if(pageNo<1||pageNo>$scope.resultMap.totalPages){
            return;//如果是第一页或最后一页不让执行
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

        //判断上一页是否可点
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }
    //判断下一页是否可点
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }

    //排序
    $scope.sortSearch=function(sortFiled,sort){
        $scope.searchMap.sortFiled=sortFiled;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    //隐藏品牌列表
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }


    //创建搜索对象
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sortFiled':'','sort':''};
    $scope.addSearchItem=function (key,value) {
        if(key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    //撤销选项
    $scope.removeSearchItem=function (key) {
        if(key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]='';
        }else{
            delete $scope.searchMap.spec[key];//移除规格项
        }
        $scope.search();//执行搜索
    }
})