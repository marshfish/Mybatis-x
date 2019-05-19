package com.mcode.mybatisx.dal.util.tree.inherit.type;

import lombok.Getter;

@Getter
public enum TreeParentNodeEnum {
    PARENT(1,"父节点"),NOT_PARENT(0,"非父节点");
    private int code;
    private String desc;

    TreeParentNodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
