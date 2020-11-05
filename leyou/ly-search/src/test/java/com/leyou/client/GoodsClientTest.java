package com.leyou.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.vo.SpuVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsClientTest {

    @Autowired
    private GoodsClient goodsClient;
    @Test
    public void createDeployment() {
        PageResult<SpuVo> papge = goodsClient.findPapge(null, true, 1, 2);
        papge.getItems().forEach(System.out::println);
    }

}
