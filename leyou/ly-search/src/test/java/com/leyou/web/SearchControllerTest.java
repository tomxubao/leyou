package com.leyou.web;


import com.leyou.client.GoodsClient;
import com.leyou.common.vo.PageResult;
import com.leyou.item.vo.SpuVo;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import com.leyou.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchControllerTest {
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;
    @Test
    public void search() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
        int page = 1;
        int rows = 100;
        int size = 0;
        do{
            PageResult<SpuVo> papges = goodsClient.findPapge(null, null, page, rows);
            //获取所有数据
            List<SpuVo> list = papges.getItems();
            size = list.size();
            List<Goods> goods = new ArrayList<>();
            list.forEach(spuVo->{
                try {
                    Goods good = searchService.buildGoods(spuVo);
                    goods.add(good);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            repository.saveAll(goods);
            page++;
        }while(size==100);
    }
}
