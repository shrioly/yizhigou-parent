 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.brandIds=JSON.parse($scope.entity.brandIds);//转换品牌
                $scope.entity.specIds=JSON.parse($scope.entity.specIds);//转换规格
                $scope.entity.customAttributeItems=JSON.parse($scope.entity.customAttributeItems);//转换扩展属性
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//$scope.brandList={data:[{id:1,text:"联想"},{id:2,text:"长城"},{id:3,text:"长安"},{id:4,text:"吉利"}]};

	//查询品牌下拉列表
	$scope.brandList={data:[]}
	$scope.findBrandList=function(){
		brandService.selectOptionList().success(
			function(response){
                  $scope.brandList ={data:response};
			}
		)
	}

//查询品牌下拉列表
    $scope.optionList={data:[]}
    $scope.findOptionList=function(){
        specificationService.selectOptionList().success(
            function(response){
                $scope.optionList ={data:response};
            }
        )
    }

    //新增扩展属性
	$scope.addTableRow=function(){
		$scope.entity.customAttributeItems.push({});
	}

	//删除扩展属性
	$scope.deleTableRow=function(index){
		$scope.entity.customAttributeItems.splice(index,1);
	}

});	
