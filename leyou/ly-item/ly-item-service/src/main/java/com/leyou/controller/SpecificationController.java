package com.leyou.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("spec")
@CrossOrigin
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        return ResponseEntity.ok(this.specificationService.queryGroupByCid(cid));
    }
    @PostMapping("group")
    public ResponseEntity<Void> addGroup(@RequestBody(required=false) SpecGroup specGroup){
        specificationService.addSpecification(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestBody(required=false)SpecGroup specGroup){
        specificationService.updateSpecification(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id){
        specificationService.deleteSpecification(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="gid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching
    ){
        return ResponseEntity.ok(this.specificationService.querySpecParams(gid,cid,searching));
    }

    @PostMapping("param")
    public ResponseEntity<Void> addParam(@RequestBody(required=false) SpecParam specParam){
        specificationService.addParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("param")
    public ResponseEntity<Void> updateParam(@RequestBody(required=false)SpecParam specParam){
        specificationService.updateParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable Long id){
        specificationService.deleteParam(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}