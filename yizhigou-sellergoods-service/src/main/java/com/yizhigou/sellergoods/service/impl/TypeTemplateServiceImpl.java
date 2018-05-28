package com.yizhigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbSpecificationOptionMapper;
import com.yizhigou.mapper.TbTypeTemplateMapper;
import com.yizhigou.pojo.TbSpecificationOption;
import com.yizhigou.pojo.TbSpecificationOptionExample;
import com.yizhigou.pojo.TbTypeTemplate;
import com.yizhigou.pojo.TbTypeTemplateExample;
import com.yizhigou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;


	//查询商品分类品牌，规格项 数据放入到redis中
	@Autowired
	private RedisTemplate redisTemplate;
	private  void saveToRedis(){
		//获取模板数据
        List<TbTypeTemplate> templateList = findAll();
        //循环模板
        for(TbTypeTemplate typeTemplate:templateList){
            //存品牌数据
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            //品牌列表存入到redis中
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),brandList);
            //存规格列表
            List<Map> specList = findSpecList(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
        }
		//把模板存入到品牌列表中
        System.out.println("品牌数据和规格项数据，已经存入到redis中....");
	}
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}
	}


		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
            saveToRedis();//启动时调用
	    PageHelper.startPage(pageNum, pageSize);
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		TbTypeTemplateExample.Criteria criteria = example.createCriteria();

		if(typeTemplate!=null){
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}

		}

		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> findSpecList(long id) {
		TbTypeTemplate type = typeTemplateMapper.selectByPrimaryKey(id);
		//提取 spec_id字段
		List<Map> list = JSON.parseArray(type.getSpecIds(), Map.class);
		for (Map map :list) {
			//循环遍历 list  把id为查询条件,从 规格项表中查询出 数据 List
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
			List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);
			map.put("options",tbSpecificationOptions);//options:[id:1,optionName:'3G']
		}
		return list;
	}

}
