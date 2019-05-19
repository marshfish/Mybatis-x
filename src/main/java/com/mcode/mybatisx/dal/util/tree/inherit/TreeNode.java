package com.mcode.mybatisx.dal.util.tree.inherit;

import com.mcode.mybatisx.dal.util.tree.inherit.type.TreeParentNodeEnum;
import lombok.Data;

/**
 * 继承该类，自定义树节点的结构
 */
@Data
public abstract class TreeNode {
    private Long id;
    private Integer isParent;
    private transient Long parentId;

    void assertNull() {
        if (id == null || isParent == null || parentId == null) {
            throw new RuntimeException("Illegal tree structure");
        }
    }

    boolean judgeParent() {
        return isParent == TreeParentNodeEnum.PARENT.getCode();
    }

}
