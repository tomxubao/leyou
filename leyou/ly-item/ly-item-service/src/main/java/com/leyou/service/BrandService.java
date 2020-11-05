package com.leyou.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

public interface BrandService {

    PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String key, String sortBy, Boolean desc);

    void saveBrand(Brand brand, List<Long> ids);

    void updateBrand(Brand brand, List<Long> ids);
    Brand queryBrandById(Long id);

    List<Brand> brandByCid(Long cid);

    void deleteByBrandId(Long id);

    Brand queryById(Long id);
}
