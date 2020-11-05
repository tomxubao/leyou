package com.leyou.mapper;

import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.CategoryBrand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
//IdListMapper<实体类,主键类型>
public interface CategoryMapper extends Mapper<Category> , IdListMapper<Category,Long> {
    /**
     * 根据brandId查询Category
     * @param bid
     * @return
     */
    @Select("select b.* from tb_category as b INNER JOIN (select * from tb_category_brand where brand_id=#{bid})as a on  b.id=a.category_id")
    List<Category> findByBrandId(Long bid);

    /**
     * 根据parentId查父类
     * @param pid
     * @return
     */
    @Select("select * FROM tb_category where id=#{pid}")
    Category findBySonId(Long pid);

    /**
     * 查询最后一条语句
     * @return
     */
    @Select("select * from tb_category where  id=(SELECT max(id) FROM tb_category)")
    Category selectCategoryLast();

    /**
     * 根据category id删除中间表相关数据
     * @param cid
     */
    @Delete("DELETE FROM tb_category_brand WHERE category_id = #{cid}")
    void deleteByCategoryIdInCategoryBrand(@Param("cid") Long cid);
    /**
     * 查看中间表是否有此数据
     */
    @Select("select *from tb_category_brand where category_id=#{cid}")
    CategoryBrand findByCategoryBrand(Long cid);
}