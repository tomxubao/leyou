package com.leyou.item.api;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.vo.SkuVo;
import com.leyou.item.vo.SpuVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /**
     * 分页查询
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuVo> findPapge(
            @RequestParam(value ="key",required= false) String key,
            @RequestParam (value ="saleable",required = false)Boolean saleable,
            @RequestParam(value ="page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    );
    /**
     * 根据spu的id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    public List<SkuVo> querySkuBySpuId(@RequestParam("id") Long id);
    /**
     * 根据spuid查询商品详情
     * @param id
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public SpuDetail querySpuDetailById(@PathVariable("id") Long id);
}
