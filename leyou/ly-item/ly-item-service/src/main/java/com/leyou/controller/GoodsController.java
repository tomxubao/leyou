package com.leyou.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.vo.SkuVo;
import com.leyou.item.vo.SpuVo;
import com.leyou.service.CategoryService;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
   //page?key=&saleable=true&page=1&rows=5

    /**
     * 分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuVo>> findPapge(
            @RequestParam(value ="key",required= false) String key,
            @RequestParam (value ="saleable",required = false)Boolean saleable,
            @RequestParam(value ="page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuVo> list= goodsService.findPapge(key, saleable, page, rows);
        return ResponseEntity.ok(list);
    }
    @PostMapping("goods")//新增201
    public ResponseEntity<Void> addGoods(@RequestBody(required=false) SpuVo spuVo ){
        goodsService.addGoods(spuVo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //http://localhost:10010/api/item/spu/detail/195

    /**
     * 根据spuid查询商品详情
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.qById(id));
    }
    /**
     * 根据spu的id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<SkuVo>> querySkuBySpuId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(this.goodsService.querySkuListBySpuId(id));
    }
    @PutMapping("goods")//更新204
    public ResponseEntity<Void> updateGoods(@RequestBody(required=false) SpuVo spuVo ){
        goodsService.updateGoods(spuVo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("goods/saleab")
    public ResponseEntity<Void> saleableGoods(@RequestParam("id") Long id,
                                              @RequestParam("saleable") Boolean saleable){
        goodsService.saleableGoodsUpdate(id,saleable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    //goods/spuId/id

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("goods/spuId/{id}")
    public ResponseEntity<Void> deleteSid(@PathVariable Long id){
        goodsService.deleteBySpuId(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
