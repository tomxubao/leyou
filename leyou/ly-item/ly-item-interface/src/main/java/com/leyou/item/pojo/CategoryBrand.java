package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Table;

@Table(name="tb_category_brand")
@Data
public class CategoryBrand {
    private Long CategoryId;
    private Long BrandId;
}
