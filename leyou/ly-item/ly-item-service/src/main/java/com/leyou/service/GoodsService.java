package com.leyou.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.vo.SkuVo;
import com.leyou.item.vo.SpuVo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GoodsService {
    SpuDetail qById(Long spuId);
    PageResult<SpuVo> findPapge(String key, Boolean saleable, Integer page, Integer rows);
    void addGoods(SpuVo spuVo);
    List<SkuVo> querySkuListBySpuId(Long id);

    void updateGoods(SpuVo spuVo);

    void saleableGoodsUpdate(Long id,Boolean saleable);

    void deleteBySpuId(Long id);

}
