package com.yizhigou.content.service.impl;
import java.util.List;

import com.yizhigou.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbContentMapper;
import com.yizhigou.pojo.TbContent;

import com.yizhigou.content.service.ContentService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//清空缓存
		//redisTemplate.delete("content");
		//删除当前类别下的缓存数据
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//清空缓存(缓存的同步)
		//redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		//如果分类id发送了修改，清除修改后分类id的缓存
		Long categoryId=contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(categoryId);
		if(categoryId.longValue()!=content.getCategoryId().longValue()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//清除缓存
			Long categoryId=contentMapper.selectByPrimaryKey(id).getCategoryId();//广告分类id
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		TbContentExample.Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	//按照分类id查询广告内容
	@Override
	public List<TbContent> findByCategoryId(long categoryId) {
		//1从redis中取数据
		List<TbContent> contentList=(List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);
		//2没有取到的话从数据库里查
		if(contentList==null){
			TbContentExample example=new TbContentExample();
			TbContentExample.Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			//查询状态为1的
			criteria.andStatusEqualTo("1");
			//排序
			example.setOrderByClause("sort_order");
			contentList = contentMapper.selectByExample(example);
			//3把数据库里查到的数据存到redis中
			redisTemplate.boundHashOps("content").put(categoryId,contentList);
			//4第二个人再进行访问的时候redis中就有数据了

		}else{
			System.out.println("从缓存读取");
		}
		return contentList;
	}

}
