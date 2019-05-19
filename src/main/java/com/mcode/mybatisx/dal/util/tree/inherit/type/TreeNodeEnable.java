package com.mcode.mybatisx.dal.util.tree.inherit.type;

import lombok.Getter;

@Getter
public enum TreeNodeEnable {
    ENABLE(1,"开启权限"),DISABLE(0,"禁用权限");
    private int enable;
    private String desc;

    TreeNodeEnable(int enable, String desc) {
        this.enable = enable;
        this.desc = desc;
    }

    public static boolean isEnable(int type){
        return type == ENABLE.enable;
    }
}
