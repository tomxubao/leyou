package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@RequestMapping("category")
public interface CategoryApi {
    /**
     * 根据商品id集合查询category
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public List<Category> queryCategoryByIds(@RequestParam("ids")List<Long> ids) ;
}
