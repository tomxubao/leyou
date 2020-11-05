package com.leyou.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    @Insert("insert into  tb_category_brand (category_id, brand_id) values (#{cid},#{bid})")
    int addBrandAndCategory(Long cid, Long bid);
    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    int deleteCategoryBrandByBrandId(Long bid);
    @Select("select * from tb_brand as b INNER JOIN (select * from tb_category_brand where category_id=#{cid})as ab on b.id=ab.brand_id")
    List<Brand> brandByCid(@Param("cid") Long cid);
}