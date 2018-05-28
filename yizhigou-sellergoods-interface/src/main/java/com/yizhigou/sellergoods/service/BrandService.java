package com.yizhigou.sellergoods.service;

import com.yizhigou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService {

    public List<TbBrand>  getBrandList();

    PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);

    void  save(TbBrand brand);

    TbBrand findOne(long  id);

    void  update(TbBrand tbBrand);

    void delete(Long[] id);

    List<Map>  selectOptionList();

}
