package com.leyou.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    public List<SpecGroup> queryGroupByCid(Long cid);

    void addSpecification(SpecGroup specGroup);

    void updateSpecification(SpecGroup specGroup);

    void deleteSpecification(Long id);

     List<SpecParam>querySpecParams(Long gid, Long cid, Boolean searching);

    void addParam(SpecParam specParam);

    void updateParam(SpecParam specParam);

    void deleteParam(Long id);

}
