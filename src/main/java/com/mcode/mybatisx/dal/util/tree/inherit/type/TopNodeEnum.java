package com.mcode.mybatisx.dal.util.tree.inherit.type;

import lombok.Getter;

@Getter
public enum TopNodeEnum {
    TOP(0L,"顶级节点"),NOT_TOP(1L,"非顶级节点");
    private Long code;
    private String desc;

    TopNodeEnum(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
