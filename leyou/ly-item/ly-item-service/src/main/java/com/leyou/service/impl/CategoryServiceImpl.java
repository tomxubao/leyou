package com.leyou.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.CategoryBrand;
import com.leyou.mapper.CategoryMapper;
import com.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.CollectionUtils.*;
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    public List<Category> queryByIds(List<Long> ids){
        List<Category> list = categoryMapper.selectByIdList(ids);
        // 判断集合是否为空
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return list;
    }

    @Override
    public List<Category> findByParentId(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        List list=categoryMapper.select(category);
        if(isEmpty(list)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return list;
    }

    @Override
    public List<Category> findByBrandtId(Long bid) {
        List<Category> list= categoryMapper.findByBrandId(bid);
        if(list==null){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        return list;
    }

    @Override
    @Transactional
    public void addCategory(Category category) {
        category.setId(null);
        int insert = categoryMapper.insert(category);
        if(insert  == 0){
            throw new LyException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
        //需要查询其父类IsParent==false如果是就改成true
        Category parent = new Category();
        parent.setId(category.getParentId());
        parent.setIsParent(true);
        categoryMapper.updateByPrimaryKeySelective(parent);
    }

    @Override
    public Category selectCategoryLast() {
        return categoryMapper.selectCategoryLast();
    }

    @Override
    public void updateCategory(Category category) {
        //**Selective允许为空
        categoryMapper.updateByPrimaryKeySelective(category);
    }

    /**
     * 判断是否是父类 -》是就删除所有子节点且维护中间表，如果不是直接删除维护中间表
     * @param cid
     */
    @Override
    @Transactional
    public void deleteCategory(Long cid) {
        //根据id查询到该对象selectByPrimaryKey
        Category category = categoryMapper.selectByPrimaryKey(cid);
        //如果是父类
        if(category.getIsParent()){
            List<Category> list = new ArrayList<>();
            categoryList(category,list);
            List<Category> list2 = new ArrayList<>();
            categoryAllList(category,list2);




            for(Category c: list2){
                categoryMapper.delete(c);
            }

            for(Category c: list){
                categoryMapper.deleteByCategoryIdInCategoryBrand(c.getId());
            }
            List<Category> byParentId = findByParentId(category.getParentId());
            if(byParentId.size()==0){
                Category category1 = new Category();
                category1.setId(category.getParentId());
                category1.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(category1 );
            }
        }else {
            //1.查询此节点的父亲节点的孩子个数 ===> 查询还有几个兄弟
            List<Category> byParentId = findByParentId(category.getParentId());
            if(byParentId.size()>1){
                //有兄弟,直接删除自己
                categoryMapper.delete(category);
                //维护中间表
                categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }
            else {
                //已经没有兄弟了
                categoryMapper.delete(category);
                Category category1 = new Category();
                category1.setId(category.getParentId());
                category1.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(category1);
                //维护中间表
                categoryMapper.deleteByCategoryIdInCategoryBrand(category.getId());
            }
        }

    }

    @Override
    public List<Category> queryCategoryByIds(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        return categories;
    }

    /**
     * 本身及子类全部查出
     * @param category
     * @param list
     */
    private void categoryList(Category category,List<Category> list) {
//        if(!category.getIsParent()){
//            CategoryBrand byCategoryBrand = categoryMapper.findByCategoryBrand(category.getId());
//            if(byCategoryBrand==null){
//                list.add(category);
//            }
//        }
        if(!category.getIsParent()){
            list.add(category);
        }
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("parentId",category.getId());
            List<Category> categories = categoryMapper.selectByExample(example);
            for (Category category2 : categories){
                categoryList(category2,list);
            }
    }
    private void categoryAllList(Category category,List<Category> list) {
        list.add(category);
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId",category.getId());
        List<Category> categories = categoryMapper.selectByExample(example);
        for (Category category2 : categories){
            categoryList(category2,list);
        }
    }


}
