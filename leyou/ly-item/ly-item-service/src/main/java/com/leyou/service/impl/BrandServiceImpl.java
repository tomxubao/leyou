package com.leyou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.mapper.BrandMapper;
import com.leyou.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    /**
     *
     * @Param page：c，int
     * @Param rows：每页大小，int
     * @Param sortBy：排序字段，String
     * @Param desc：是否为降序，boolean
     * @Param key：搜索关键词，String
     * @return 分页条件查询
     */
    @Override
    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String key, String sortBy, Boolean desc) {
        //创建分页对象
        PageHelper.startPage(page,rows);
        //过滤分页条件
        Example example = new Example(Brand.class);
        //StringUtils lang3
        if(StringUtils.isNoneEmpty(key)){
            //创建条件
            example.createCriteria().orLike("name","%"+key+"%")
                    .orEqualTo("letter",key.toUpperCase());
        }
        // 排序
        if(StringUtils.isNoneBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);// id desc
        }
        // 查询
        List<Brand> brands = brandMapper.selectByExample(example);
        // 判断是否为空
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.BRANDS_NOT_FOND);
        }
        // 解析分页结果
        PageInfo<Brand> result = new PageInfo<>(brands);
        return new PageResult<>(result.getTotal(), result.getList());
    }

    /**
     * 保存方法
     * @param brand
     * @param ids
     */
    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> ids) {
        //新增品牌
        //设置id为空
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if(count==0){
            throw new LyException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
        for (Long id:ids){
            int i = brandMapper.addBrandAndCategory(id,brand.getId() );
            if(i==0){
                throw new LyException(ExceptionEnum.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 修改
     * @param brand
     * @param ids
     */
    @Override
    @Transactional
    public void updateBrand(Brand brand, List<Long> ids) {
        //修改brand表
        Example example = new Example(Brand.class);
        example.createCriteria().andEqualTo("id",brand.getId());
        brandMapper.updateByExampleSelective(brand,example);
        //修改中间表先删除在添加
        int i = brandMapper.deleteCategoryBrandByBrandId(brand.getId());
        if(i==0){
            throw new LyException(ExceptionEnum.UPDTAE_SERVER_ERROR);
        }
        for (Long cid:ids){
            int j = brandMapper.addBrandAndCategory(brand.getId(), cid);
            if(i==0){
                throw new LyException(ExceptionEnum.UPDTAE_SERVER_ERROR);
            }
        }
    }

    @Override
    public Brand queryBrandById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand == null){
            throw new LyException(ExceptionEnum.BRANDS_NOT_FOND);
        }
        return brand ;
    }

    @Override
    public List<Brand> brandByCid(Long cid) {
        List<Brand> list = brandMapper.brandByCid(cid);
        return list;
    }

    @Override
    public void deleteByBrandId(Long id) {
        //删除本身
        int i = brandMapper.deleteByPrimaryKey(id);
        if(i==0){
            throw  new LyException(ExceptionEnum.DELETE_SERVER_ERROR);
        }
        //删除中间表
        int i1 = brandMapper.deleteCategoryBrandByBrandId(id);
        if(i1==0){
            throw  new LyException(ExceptionEnum.DELETE_SERVER_ERROR);
        }
    }

    @Override
    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        return brand;

    }
}
