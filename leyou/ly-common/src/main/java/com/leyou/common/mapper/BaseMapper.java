package com.leyou.common.mapper;


import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;

@RegisterMapper//加入这个才会生效哦
public interface BaseMapper<T,PK> extends Mapper<T>, IdListMapper<T, PK>, InsertListMapper<T> {
}