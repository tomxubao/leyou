package com.leyou.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import com.leyou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;
    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        //select根据不为空字段查询
        List<SpecGroup> list = groupMapper.select(group);
        if(CollectionUtils.isEmpty(list)){
            // 没查到
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    @Override
    public void addSpecification(SpecGroup specGroup) {
        specGroup.setId(null);
        int i = groupMapper.insert(specGroup);
        if(i==0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_INSERT_ERROR);
        }
    }

    @Override
    public void updateSpecification(SpecGroup specGroup) {
        int i = groupMapper.updateByPrimaryKeySelective(specGroup);
        if(i==0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_UPDATE_ERROR);
        }
    }

    @Override
    public void deleteSpecification(Long id) {
        int i = groupMapper.deleteByPrimaryKey(id);
        if(i==0){
            throw new LyException(ExceptionEnum.SPEC_GROUP_DELETE_ERROR);
        }
    }

    @Override
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        List<SpecParam> list = paramMapper.select(specParam);
        if(list==null){
            throw new LyException(ExceptionEnum.SPEC_PARAM_SELECT_ERROR);
        }
        return list;
    }

    @Override
    public void addParam(SpecParam specParam) {
        specParam.setId(null);
        int insert = paramMapper.insert(specParam);
        if(insert == 0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_SAVE_ERROR);
        }
    }

    @Override
    public void updateParam(SpecParam specParam) {
        int i = paramMapper.updateByPrimaryKeySelective(specParam);
        if(i == 0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_UPDATE_ERROR);
        }
    }

    @Override
    public void deleteParam(Long id) {
        int i = paramMapper.deleteByPrimaryKey(id);
        if(i == 0){
            throw new LyException(ExceptionEnum.SPEC_PARAM_DELETE_ERROR);
        }
    }
}