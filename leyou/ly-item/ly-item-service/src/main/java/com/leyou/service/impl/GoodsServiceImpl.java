package com.leyou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.item.vo.SkuVo;
import com.leyou.item.vo.SpuVo;
import com.leyou.mapper.*;
import com.leyou.service.BrandService;
import com.leyou.service.CategoryService;
import com.leyou.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.beans.BeanUtils.copyProperties;
import static org.springframework.util.CollectionUtils.contains;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;

    @Override
    public SpuDetail qById(Long spuId) {
        SpuDetail detail = spuDetailMapper.selectByPrimaryKey(spuId);
        if(detail == null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOND);
        }
        return detail;
    }

    @Override
    public PageResult<SpuVo> findPapge(String key, Boolean saleable, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //逻辑删除过滤 1true是记录
        criteria.andEqualTo("valid", true);
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        List<Spu> spus = spuMapper.selectByExample(example);
        List<SpuVo> spuVos =new ArrayList<>();
        for(Spu spu: spus){
            SpuVo spuVo = new SpuVo();
            //将对象一的值给对象二
            copyProperties(spu,spuVo);
            spuVos.add(spuVo);
        }
        if(isEmpty(spuVos)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        cidChangeName(spuVos);
        PageInfo<Spu> info = new PageInfo<>(spus);

        return new PageResult( info.getTotal() , spuVos);
    }

    /**
     * 添加
     * @param spuVo
     */
    @Override
    @Transactional
    public void addGoods(SpuVo spuVo) {
        //spu表增长
        Spu spu = new Spu();
        copyProperties(spuVo,spu);
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setLastUpdateTime(spu.getCreateTime());
        int i = spuMapper.insert(spu);
        //spuDetail详情表增长
        SpuDetail spuDetail = spuVo.getSpuDetail();
        spuDetail.setSpuId(spu.getId());
        int j = spuDetailMapper.insert(spuDetail);

        //增加sku
        saveSkuAndStock(spuVo,spu);
    }

    @Override
    public List<SkuVo> querySkuListBySpuId(Long id) {
        //获取sku列表值
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> select = skuMapper.select(sku);
        if(select==null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
      List<SkuVo>skuVos= new ArrayList<>();
        select.forEach(s->{
            SkuVo skuVo = new SkuVo();
            copyProperties(s,skuVo);
            skuVos.add(skuVo);
        });
//        for(Sku s : select){
//            SkuVo skuVo = new SkuVo();
//            copyProperties(s,skuVo);
//            skuVos.add(skuVo);
//            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
//            if(stock==null){
//                throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
//            }
//            copyProperties(stock,skuVo);
//        }
        //将集合转流获取id后再将流转成只有id的成数组
        List<Long> ids = skuVos.stream().map(s -> s.getId()).collect(Collectors.toList());
        List<Stock> stocks = stockMapper.selectByIdList(ids);
        if (stocks == null) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOND);
        }
        Map<Long, Integer> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skuVos.forEach(skuVo->skuVo.setStock(stockMap.get(skuVo.getId())));
        // stocks.forEach(System.out::println);
        return skuVos;
    }

    @Override
    @Transactional
    public void updateGoods(SpuVo spuVo) {
        if(spuVo.getId() == null){
            throw new LyException(ExceptionEnum. GOODS_UPDATE_ERROR);
        }
        Sku sku = new Sku();
        sku.setSpuId(spuVo.getId());
        //删除sku再删除之前先查询下有没有
        List<Sku> skus = skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(skus)){
            skuMapper.delete(sku);
//            List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());
//            stockMapper.deleteByIdList(ids);
            skus.forEach(s ->stockMapper.deleteByPrimaryKey(s.getId()));
        }
        //修改spu
        Spu spu = new Spu();
        copyProperties(spuVo,spu);
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spuMapper.updateByPrimaryKeySelective(spu);
        SpuDetail detail = spuVo.getSpuDetail();
        spuDetailMapper.updateByPrimaryKeySelective(detail);
        //添加sku
        saveSkuAndStock(spuVo,spu);
    }

    @Override
    public void saleableGoodsUpdate(Long id,Boolean saleable) {

        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(!saleable);
        //只想更新某一字段用 updateByPrimaryKeySelective
        int i = spuMapper.updateByPrimaryKeySelective(spu);
        if(i!=1){
            throw new LyException(ExceptionEnum. GOODS_UPDATE_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteBySpuId(Long id) {
        //逻辑删除spu
        Spu spu = new Spu();
        spu.setId(id);
        spu.setValid(false);
        int i1 = spuMapper.updateByPrimaryKeySelective(spu);
        if(i1 != 1){
            throw new LyException(ExceptionEnum.DELETE_SERVER_ERROR);
        }
        //逻辑删除sku 删除是0 false 记录是1true
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> select = skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(select)){
            select.forEach(s -> {
                s.setEnable(false);
                int i = skuMapper.updateByPrimaryKeySelective(s);
                if(i != 1){
                    throw new LyException(ExceptionEnum.DELETE_SERVER_ERROR);
                }
            });
        }
    }

    private void saveSkuAndStock(SpuVo spuVo ,Spu spu) {
        List<SkuVo> skuVos = spuVo.getSkus();
        List<Stock> stocks = new ArrayList<>();
        for(SkuVo skuVo:skuVos){
            Sku sku = new Sku();
            copyProperties(skuVo,sku);
            sku.setId(null);
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());
            int insert = skuMapper.insert(sku);
            if(insert==0){
                throw new LyException(ExceptionEnum.GOODS_INSERT_ERROR);
            }

            Stock stock =new Stock();
            copyProperties(skuVo,stock);
            stock.setSkuId(sku.getId());
            stocks.add(stock);
        }
        int l = stockMapper.insertList(stocks);
    }

    private void cidChangeName(List<SpuVo> spuVos) {
        for (SpuVo spuVo : spuVos) {
            //取消字符串的拼接会占用更小的内存
//            List<Long> cids = new ArrayList<>();
//            cids.add(spuVo.getCid1());
//            cids.add(spuVo.getCid2());
//            cids.add(spuVo.getCid3());
//            categoryService.queryByIds(cids);
            List<Category> categories = categoryService.queryByIds(Arrays.asList(spuVo.getCid1(), spuVo.getCid2(), spuVo.getCid3()));
            //数组转化成流
            Stream<Category> stream = categories.stream();
            //将对象转化成字符串
            Stream<String> stringStream = stream.map(Category::getName);
            //将流转化成集合
            List<String> collect = stringStream.collect(Collectors.toList());
            //转换拼接设置处理后的属性
            spuVo.setCname(StringUtils.join(collect,"/"));

            Brand brand = brandService.queryBrandById(spuVo.getBrandId());
            //设置处理后的属性
            spuVo.setBname(brand.getName());
        }
    }
}
