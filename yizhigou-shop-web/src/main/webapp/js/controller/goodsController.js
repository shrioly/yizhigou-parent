 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService,$location){
	$controller('baseController',{$scope:$scope});//继承
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var  id = $location.search()['id']
		if(id==null){
			return ;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//富文本编辑器添加内容
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//显示规格属性
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				
				for (var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
				
			}
		);				
	}


    //保存
    $scope.save=function(){
        //获取富文本编辑器里的信息
        $scope.entity.goodsDesc.introduction=editor.html();
        var serviceObject;//服务层对象
        if($scope.entity.goods.id!=null){//如果有ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            $scope.entity={};
            editor.html('');//清空富文本编辑器内容
            serviceObject=goodsService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                   // $scope.reloadList();//重新加载
					//alert("保存成功");
					location.href="goods.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }

	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//上传图片
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function(response){
				if (response.success){  //图片上传成功
					$scope.image_entity.url=response.message;
				}else{
					alert(response.message);
				}
			}
		).error(function(){
			alert("上传图片发生错误!");
		})
    }

    //保存图片
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};
    $scope.add_image_entity=function(){
	    $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //移除图片
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //查询一级分类
	$scope.selectItemCat1List=function(){
         itemCatService.findByParentId(0).success(
         	function(response){
         		$scope.itemCat1List = response;
			}
		 )
	}

	//查询二级分类

	$scope.$watch("entity.goods.category1Id",function(newValue,oldValue){
        //alert(newValue+"====="+oldValue);
        //根据选中的id调用findByParentId进行查询
		itemCatService.findByParentId(newValue).success(
			function(response){
				$scope.itemCat2List=response;
			}
		)
	});
    //查询显示三级分类
    $scope.$watch("entity.goods.category2Id",function(newValue,oldValue){
        //alert(newValue+"====="+oldValue);
        //根据选中的id调用findByParentId进行查询
        itemCatService.findByParentId(newValue).success(
            function(response){
                $scope.itemCat3List=response;
            }
        )
    });

    //查询完三级页面操作
    $scope.$watch("entity.goods.category3Id",function(newValue,oldValue){

        //根据选中的id调用findOne 当前类目,模板id
        itemCatService.findOne(newValue).success(
            function(response){
                $scope.entity.goods.typeTemplateId=response.typeId;
            }
        )



    });
    //弹出模板ID后做的操作,查询品牌列表
    $scope.$watch("entity.goods.typeTemplateId",function(newValue,oldValue){

        //根据选中的id调用findOne 当前类目,模板id
		typeTemplateService.findOne(newValue).success(
			function(response){
				$scope.typeTemplate=response;
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
				if($location.search()["id"]==null){
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
				}

			}
		)
		//根据模板id查询规格信息
		typeTemplateService.findSpecList(newValue).success(
			function(response){
				$scope.specList=response;
			}
		)
	});

    $scope.updateSpecAttribute=function($event,name,value){
          var object =$scope.searchObjectByKey(
          	  $scope.entity.goodsDesc.specificationItems,'attributeName',name
		  )
		if(object!=null){
            if($event.target.checked){
                object.attributeValue.push(value);
            }else{
            	object.attributeValue.splice(object.attributeValue.indexOf(value),1);//移除选中
				//选项取消勾选
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}
	//创建SKU列表
   $scope.createItemList=function(){
    	//定义一个对象
	   $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];//初始化
	   //
	   var items = $scope.entity.goodsDesc.specificationItems;//获取到规格项列表
	   for(var i=0;i<items.length;i++){
	   	 $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
	   }
   }

   addColumn=function(list,columnName,conlumnValues){
    	var newList=[];//新集合
      for(var i=0;i<list.length;i++){
      	var oldRow=list[i];
      	for(var j=0;j<conlumnValues.length;j++){
      		var newRow = JSON.parse(JSON.stringify(oldRow));
      		newRow.spec[columnName]=conlumnValues[j];
      		newList.push(newRow);
		}
	  }
	  return newList;
   }
   //显示状态
    $scope.status=['未审核','审核通过','审核未通过','关闭'];

    $scope.itemCatList=[];
   //显示分类
	$scope.findItemCatList=function(){
		itemCatService.findAll().success(
			function(response){
				//$scope.list=response;
				//把json数据遍历出来,放到一个对象中
				for (var i=0;i<response.length;i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
			}
		)
	}

	//规格项复选框勾选状态
	$scope.checkAttributeValue=function(specName,optionName){
		//获取到数据库中当前商品的已经有的规格选项
		var items = $scope.entity.goodsDesc.specificationItems;
		//判断比较
		var  object = $scope.searchObjectByKey(items,'attributeName',specName);
		if(object==null){
			return  false;
		}else{
			if(object.attributeValue.indexOf(optionName)>=0){
                return true;
			}else{
				return false;
			}
		}
	}


});	
