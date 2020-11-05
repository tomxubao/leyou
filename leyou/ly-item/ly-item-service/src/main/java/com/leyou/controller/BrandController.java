package com.leyou.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
@CrossOrigin
public class BrandController {

    @Autowired
    private BrandService brandService;
    //brand/bid/id真删除
    @DeleteMapping("bid/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long id){
        brandService.deleteByBrandId(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> brandByCid(@PathVariable("cid") Long cid){
       List<Brand>brands =  brandService.brandByCid(cid);
        return ResponseEntity.ok(brands);

    }
    @PutMapping
    public ResponseEntity<Void> update(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.updateBrand(brand,cids);
        //没有返回结果的用这个
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据cids添加
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> add(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand,cids);
        //没有返回结果的用这个
        return  ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 条件查询分页
     *
     * @param page
     * @param rows
     * @param key
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc
    ) {
        return ResponseEntity
                .ok(brandService.queryBrandByPage(page, rows, key, sortBy, desc));
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id){
        Brand brand = brandService.queryById(id);
        return ResponseEntity.ok(brand);
    }
}