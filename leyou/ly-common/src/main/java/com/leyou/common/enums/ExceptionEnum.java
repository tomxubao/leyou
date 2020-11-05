package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    GOODS_UPDATE_ERROR(500,"商品修改失败"),
    GOODS_DELETE_ERROR(500,"商品删除失败"),
    GOODS_DETAIL_NOT_FOND(500,"商品详情查询失败"),
    SPEC_PARAM_NOT_FOUND(500,"规格属性查询失败"),
    GOODS_NOT_FOND(404,"商品查询失败"),
    SPEC_PARAM_DELETE_ERROR(500,"规格属性删除该失败"),
    SPEC_PARAM_UPDATE_ERROR(500,"规格属性修改该失败"),
    SPEC_PARAM_SAVE_ERROR(500,"规格属性保存失败"),
    SPEC_PARAM_SELECT_ERROR(404,"规格属性没有找到"),
    SPEC_GROUP_NOT_FOUND(404,"规格参数没有找到"),
    UPDTAE_SERVER_ERROR(500,"修改失败"),
    DELETE_SERVER_ERROR(500,"删除失败"),
    OSS_IMAGE_ADD(500,"图片上传失败"),
    INTERNAL_SERVER_ERROR(500,"添加失败"),
    BRANDS_NOT_FOND(404,"品牌没查到"),
    CATEGORY_NOT_FOND(404,"商品分类没查到"),
    PRICE_CANNOT_BE_NULL(400, "价格不能为空！"),
    SPEC_GROUP_INSERT_ERROR(500,"规格组添加失败"),
    SPEC_GROUP_UPDATE_ERROR(500,"规格组修改失败"),
    SPEC_GROUP_DELETE_ERROR(500,"规格组删除失败"),
    GOODS_INSERT_ERROR(500,"商品添加失败");

    private int value;
    private String msg;

    public int value() {
        return this.value;
    }

    public String msg() {
        return this.msg;
    }


}