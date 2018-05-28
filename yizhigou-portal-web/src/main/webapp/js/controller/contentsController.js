app.controller("contentController",function ($scope,$http,contentService) {

    //查询广告位展示
    $scope.contentList=[];
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    }
    $scope.search=function () {//传递参数 搜索的数据传递
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})