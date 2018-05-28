package com.yizhigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.*;
import com.yizhigou.pojo.*;
import com.yizhigou.pojogroup.Goods;
import com.yizhigou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper  itemMapper;

	@Autowired
	private TbItemCatMapper  itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//0未审核  1.审核通过, 2.审核未通过
		goodsMapper.insert(goods.getGoods());
		//int a = 1/0; //模拟异常
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		saveItemList(goods);
	}

	//抽离保存商品的方法
	private  void  saveItemList(Goods goods){
		if(goods.getGoods().getIsEnableSpec().equals("1")){//启动规格参数
//商品SKU列表
			for(TbItem item:goods.getItemList()){
				//sku标题
				String title = goods.getGoods().getGoodsName();
				Map<String,Object> specmap= JSON.parseObject(item.getSpec());
				for(String key :specmap.keySet()){
					title+=" "+specmap.get(key);
				}
				item.setTitle(title);//拼接后的标题
				setItemValue(item,goods);
				itemMapper.insert(item);
			}
		}else{
			TbItem item = new TbItem();
			//sku标题
			String title = goods.getGoods().getGoodsName();
			item.setPrice(goods.getGoods().getPrice());
			item.setSpec("{}");
			item.setNum(9999);
			item.setStatus("1");
			item.setTitle(title);//拼接后的标题
			setItemValue(item,goods);
			itemMapper.insert(item);
		}
	}

//抽出保存商品sku信息
	private void setItemValue(TbItem item,Goods goods){
		//查询商品所属类目
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategoryid(goods.getGoods().getCategory3Id());
		item.setCategory(tbItemCat.getName());//商品分类名称
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		item.setGoodsId(goods.getGoods().getId()); //补全goodsID
		item.setSellerId(goods.getGoods().getSellerId());//商家id
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(tbBrand.getName());//品牌名称
		//商家店铺名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());
		//图片保存
		List<Map> imageList=  JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);
		if(imageList.size()>0){
			item.setImage((String) imageList.get(0).get("url"));
		}
	}
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//修改后把状态修改成未审核
		goods.getGoods().setAuditStatus("0");//未审核
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//修改sku列表//先删除后添加
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		//tbgoods   tbgoodsDesc表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		Goods  goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		//读取sku列表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItemList(tbItems);

		return goods;


	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");//1代表商品已经删除
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		TbGoodsExample.Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	//商品审核
	@Override
	public void updateStatus(Long[] id, String status) {
			for(Long  ids:id){
				TbGoods tbGoods = goodsMapper.selectByPrimaryKey(ids);
				tbGoods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(tbGoods);
			}
	}
	//根据id和状态查询商品
	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] id, String status) {
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(id));
		criteria.andStatusEqualTo(status);
		return itemMapper.selectByExample(example);
	}

}
