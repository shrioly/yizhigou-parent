app.service("contentService",function ($http) {
    //查询大广告位
    this.findByCategoryId=function (categoryId) {
        return $http.get("/content/findByCategoryId.do?categoryId="+categoryId);
    }
})