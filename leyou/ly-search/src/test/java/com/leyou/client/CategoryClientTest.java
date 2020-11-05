package com.leyou.client;


import com.leyou.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryService;
    @Test
    public void queryCategoryByIds() {
        List<Category> categories = categoryService.queryCategoryByIds(Arrays.asList(1L));
        //断言
        Assert.assertEquals(1,categories.size());
        categories.forEach(System.out::println);
    }
}
