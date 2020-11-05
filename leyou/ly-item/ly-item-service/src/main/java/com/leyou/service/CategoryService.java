package com.leyou.service;

import com.leyou.item.pojo.Category;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {
    List<Category> queryByIds(List<Long> ids);
    List<Category> findByParentId(Long pid);

    List<Category> findByBrandtId(Long bid);

    void addCategory(Category category);

    Category selectCategoryLast();

    void updateCategory(Category category);


    void deleteCategory(Long cid);


    List<Category> queryCategoryByIds(List<Long> ids);
}
