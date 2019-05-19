package com.mcode.mybatisx.dal.util.tree.inherit;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 继承该类，自定义树的整体结构
 * S即自定义的树节点，可扩展其他属性
 * R即树结构，继承该类自定义的属性不会属于S
 *
 * @param <S>
 * @param <R>
 */

public abstract class AbstractTree<S extends TreeNode, R extends AbstractTree<S, R>> implements Tree<S, R> {
    private S node;
    private List<R> childNode = Lists.newArrayList();
    private transient Supplier<R> supplier;
    private transient BiFunction<S, Supplier<R>, R> function;

    public S getNode() {
        return node;
    }

    @SuppressWarnings("unchecked")
     R setNode(S node) {
        this.node = node;
        return (R) this;
    }

    public List<R> getChildNode() {
        return childNode;
    }

    @SuppressWarnings("unchecked")
    R setChildNode(List<R> childNode) {
        this.childNode = childNode;
        return (R) this;
    }

    protected AbstractTree() {
    }

    @SuppressWarnings("unchecked")
     R setMapper(BiFunction<S, Supplier<R>, R> function) {
        this.function = function;
        return (R) this;
    }


    @SuppressWarnings("unchecked")
    R setAbstractTreeSupplier(Supplier<R> supplier) {
        this.supplier = supplier;
        return (R) this;
    }


    @Override
    public List<S> flatTreeNode() {
        if (CollectionUtils.isNotEmpty(childNode)) {
            List<S> collect = childNode.stream().
                    map(AbstractTree::flatTreeNode).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
            collect.add(node);
            return collect;
        }
        return Lists.newArrayList(node);
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<R> flatTree() {
        if (CollectionUtils.isNotEmpty(childNode)) {
            List<R> collect = childNode.stream().
                    map(Tree::flatTree).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
            collect.add((R)this);
            return collect;
        }
        return Lists.newArrayList((R)this);
    }


    /**
     * 添加新节点
     *
     * @param node               节点
     * @param persistentConsumer 持久化操作
     */
    public void addNode(S node, Consumer<S> persistentConsumer) {
        persistentConsumer.accept(node);
    }

    /**
     * 删除节点
     *
     * @param node               节点
     * @param persistentConsumer 删除策略
     */
    public void removeNode(S node, Consumer<S> persistentConsumer) {
        persistentConsumer.accept(node);
    }


    @Override
    public R selectTreeNode(S node, Function<Long, List<S>> operation) {
        node.assertNull();
        if (node.judgeParent()) {
            List<R> result = operation.apply(node.getId())
                    .parallelStream()
                    .map(e -> selectTreeNode(e, operation))
                    .collect(Collectors.toList());
            return function.apply(node, supplier).setChildNode(result);
        }
        return function.apply(node, supplier);
    }


    @Override
    public List<R> selectMultiTreeNode(List<S> nodes, Function<Long, List<S>> operation) {
        return nodes.stream().
                map(e -> selectTreeNode(e, operation)).
                collect(Collectors.toList());

    }


    @Override
    public List<S> selectFlatMultiTreeNode(List<S> nodes, Function<Long, List<S>> operation) {
        return this.selectMultiTreeNode(nodes, operation).
                stream().
                map(Tree::flatTreeNode).
                flatMap(Collection::stream).
                collect(Collectors.toList());
    }


    @Override
    public List<S> selectFlatTreeNode(S node, Function<Long, List<S>> operation) {
        return this.selectTreeNode(node, operation).flatTreeNode();
    }


}
