package com.yizhigou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yizhigou.pojo.TbBrand;
import com.yizhigou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")
    public List<TbBrand>  getBrandList(){
        return brandService.getBrandList();
    }

    //品牌查询分页
    @RequestMapping("/search")
    public PageResult  findPage(@RequestBody TbBrand tbBrand, int page,int rows){
        return  brandService.findPage(tbBrand,page,rows);
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
             brandService.save(tbBrand);
             return new Result (true,"添加成功!");
        }catch (Exception e){
            e.printStackTrace();
            return   new Result(false,"添加失败");
        }
    }

    //查询单条
    @RequestMapping("/findOne")
    public TbBrand findOne(long id){
        return brandService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
                brandService.update(tbBrand);
                return  new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"修改失败");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return  new Result( true, "删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return  new Result(false,"删除失败");
        }
    }

    //查询品牌下拉列表
    @RequestMapping("/selectOptionList")
    public List<Map>  selectOptionList(){
      return   brandService.selectOptionList();
    }

}
