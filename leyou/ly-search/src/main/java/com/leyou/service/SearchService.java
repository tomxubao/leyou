package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.pojo.*;
import com.leyou.item.vo.SkuVo;
import com.leyou.item.vo.SpuVo;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.repository.GoodsRepository;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specClient;
    @Autowired
    private GoodsRepository repository;

    /**
     * 条件查询all
     * @param request
     * @return
     */
    public PageResult<Goods> search(SearchRequest request) {
        if(StringUtils.isBlank(request.getKey())){
            return null;
        }

        //构建查询对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //创建查询条件operator(Operator.AND)电商默认都是and，创建条件默认是or
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()).operator(Operator.AND));
        //添加分页结果分页结果要从0开始
        queryBuilder.withPageable(PageRequest.of(request.getPage()-1,request.getSize()));
        //添加搜索过滤
       // queryBuilder.withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));
        //字段过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //排序
        String sortBy = request.getSortBy();
        Boolean descending = request.getDescending();
        if(StringUtils.isNotBlank(sortBy)){
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending?SortOrder.DESC:SortOrder.ASC));
        }
        //返回查询结果
        Page<Goods> search = repository.search(queryBuilder.build());
        //解析分页结果
        long totalElements = search.getTotalElements();//条数
        Integer totalPages = search.getTotalPages();//总页数
        List<Goods> content = search.getContent();//获取页面内容
        return new PageResult(totalElements, totalPages,content);
    }
//    public PageResult<Goods> searchs(SearchRequest request){
//        //构建查询条件
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        //创建搜索条件
//        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));
//        //创建排序规则
//        queryBuilder.withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));
//        //创建过滤字段
//        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
//        //创建分页结果
//        Page<Goods> search = repository.search(queryBuilder.build());
//        //获取并返回
//        return new PageResult(search.getTotalElements(), search.getTotalPages(),search.getContent());
//    }
    public Goods buildGoods(SpuVo spuVo) throws IOException {
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spuVo.getCid1(), spuVo.getCid1(), spuVo.getCid1()));
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryById(spuVo.getBrandId());

        //拼接搜索字段
        StringBuilder all = new StringBuilder();
        all.append(" ").append(spuVo.getTitle());
        names.forEach((name)->{
            all.append(" ").append(name);
        });
        all.append(" ").append(brand.getName());



        //根据id查询到了spu集合
        List<SkuVo> skuVos = goodsClient.querySkuBySpuId(spuVo.getId());
        // 2 处理sku
        List<Map<String, Object>> skuList = new ArrayList<>();
        HashSet<Long> price = new HashSet<Long>();//为什么用hashset
        // 3 创建价格集合
        skuVos.forEach(sku -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skuList.add(map);
            // 添加价格
            price.add(sku.getPrice());
        });
            //查询规格参数只有第三极才能选规格
        List<SpecParam> specParams = specClient.querySpecParam(null, spuVo.getCid3(), true);
        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuVo.getId());
        //通用规格
        Map<Long,String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);        // 特有规格参数
        Map<Long,List<String>> specialSpec =
                JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});

        // 4 规格参数, key是规格参数名称, 值是规格参数的值
        Map<String,Object> specs = new HashMap<>();
        // 填充
        for (SpecParam param : specParams) {
            String key = param.getName();// 规格参数key
            Object value = null;
            // 判断参数是否是通用参数
            if(param.getGeneric()){
                // 通用
                value = genericSpec.get(param.getId());
                // 判断是否是数值类型
                if(param.getNumeric()){
                    // 如果是数值类型,要进行分段
                    value = chooseSegment(value.toString(), param);
                }
            }else{
                // 特有参数
                value = specialSpec.get(param.getId());
            }

            // 健壮处理
            value = value == null || StringUtils.isEmpty(value.toString()) ? "其它" : value;
            // 存入map
            specs.put(key, value);
        }

        Goods goods = new Goods();
        goods.setId(spuVo.getId());
        goods.setAll(all.toString());// 搜索字段,包含:标题,分类,品牌,规格等
        goods.setSubTitle(spuVo.getSubTitle());
        goods.setBrandId(spuVo.getBrandId());
        goods.setCid1(spuVo.getCid1());
        goods.setCid2(spuVo.getCid2());
        goods.setCid3(spuVo.getCid3());
        goods.setCreateTime(spuVo.getCreateTime());
        goods.setPrice(price);
        goods.setSkus(JsonUtils.toString(skuList));
        goods.setSpecs(specs);

        return goods;
    }
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

}
