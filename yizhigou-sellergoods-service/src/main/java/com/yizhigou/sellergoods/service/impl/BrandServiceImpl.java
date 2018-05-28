package com.yizhigou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yizhigou.mapper.TbBrandMapper;
import com.yizhigou.pojo.TbBrand;
import com.yizhigou.pojo.TbBrandExample;
import com.yizhigou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    //查询全部
    @Override
    public List<TbBrand> getBrandList() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        //查询条件
        if(tbBrand.getName()!=null && tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
        }
        if(tbBrand.getFirstChar()!=null && tbBrand.getFirstChar().length()>0){
            criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
        }
        //执行查询
        Page<TbBrand>  result = (Page<TbBrand>) brandMapper.selectByExample(example);



        //返回总条数,  和查询结果
        return new PageResult(result.getTotal(),result.getResult());
    }

    @Override
    public void save(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] id) {
        for (Long ids: id){
             brandMapper.deleteByPrimaryKey(ids);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
