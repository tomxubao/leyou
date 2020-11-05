package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Data;

@Data
public class ExceptionResult {
    private int status;
    private String msg;
    private long timestamp;

    public ExceptionResult(ExceptionEnum em){
        this.status = em.value();
        this.msg = em.msg();
        this.timestamp = System.currentTimeMillis();
    }
}