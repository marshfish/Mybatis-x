package com.mcode.mybatisx.dal.util.tree.inherit;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 基于继承关系的树操作工具
 * DB IO次数较多，适用于表数据较小、简单的树结构层级管理
 * 若树较为复杂、对性能有较高要求，可选择基于左右值编码的树结构工具包 {@link com.mcode.mybatisx.dal.util.tree.coding}
 * @param <S>
 * @param <R>
 */
public interface Tree<S extends TreeNode, R extends Tree<S, R>> {
    /**
     * 扁平化子节点的树节点（即仅包含node属性）
     *
     * @return 树节点列表
     */
    List<S> flatChildTreeNode();

    /**
     * 扁平化子节点的树结构（即既包含node属性，也包含子节点列表）
     *
     * @return 树结构列表
     */
    List<R> flatChildTree();

    /**
     * 根据父节点查询子节点树
     *
     * @param node      父节点
     * @param operation 自定义的查询下一级子节点操作
     * @return 节点树（包含父节点）
     */
    R selectTreeNode(S node, Function<Long, List<S>> operation);

    /**
     * 根据父节点列表查询子节点树
     *
     * @param nodes     父节点列表
     * @param operation 自定义的查询下一级子节点操作
     * @return 节点树（包含父节点）
     */
    List<R> selectMultiTreeNode(List<S> nodes, Function<Long, List<S>> operation);

    /**
     * 根据父节点获取扁平子节点列表
     *
     * @param node      节点
     * @param operation 自定义的查询下一级子节点操作
     */
    List<S> selectFlatTreeNode(S node, Function<Long, List<S>> operation);

    /**
     * 根据父节点列表获取所有扁平子节点列表
     *
     * @param nodes     节点列表
     * @param operation 自定义的查询下一级子节点操作
     */
    List<S> selectFlatMultiTreeNode(List<S> nodes, Function<Long, List<S>> operation);

    /**
     * 树结构工厂，返回一个空的树结构对象
     *
     * @param <S>      自定义树节点
     * @param <R>      自定义树结构
     * @param supplier 树结构生成supplier,需要返回一个继承AbstractTree的自定义树结构类
     * @return Tree<S,R>
     */
    static <S extends TreeNode, R extends AbstractTree<S, R>> Tree<S, R> build(Supplier<R> supplier) {
        final R r = supplier.get().setAbstractTreeSupplier(supplier);
        return r.setMapper( rSupplier -> rSupplier.get().setAbstractTreeSupplier(supplier));
    }

}
