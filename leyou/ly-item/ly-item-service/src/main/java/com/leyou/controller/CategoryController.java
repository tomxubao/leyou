package com.leyou.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.service.CategoryService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点查询子类
     * @param pid 默认值 0
     * @RequestParam(？pid)
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> findById(  @RequestParam(value = "pid", defaultValue = "0") Long pid){
        if(pid==-1){
           Category category= categoryService.selectCategoryLast();
           List<Category> categories=new ArrayList<Category>();
           categories.add(category);
           if(categories==null){
               throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
           }
            return ResponseEntity.ok(categories);
        }else{
            List<Category> list =categoryService.findByParentId(pid);
            return ResponseEntity.ok(list);
        }
    }
    /**
     * 根据
     * @PathVariable（/bid）
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>>findByBrandId(@PathVariable("bid") Long bid){
        List<Category> list =categoryService.findByBrandtId(bid);
        return ResponseEntity.ok(list);
    }
    /**
     *添加
     * @return
     */
    @PostMapping
    public ResponseEntity<Category> addCategory(Category category){
            categoryService.addCategory(category);
            return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 修改
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCategory(Category category){
        categoryService.updateCategory(category);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 删除
     * @return
     */
    @DeleteMapping("cid/{cid}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long cid) {
        categoryService.deleteCategory(cid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据商品id集合查询category
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids")List<Long> ids) {
        List<Category> list = categoryService.queryCategoryByIds(ids);
        return ResponseEntity.ok(list);
    }
}
