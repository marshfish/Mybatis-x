package com.mcode.mybatisx.dal.util.tree.inherit;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    private List<R> childNode;
    private transient Supplier<R> supplier;
    private transient Function<Supplier<R>, R> function;

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
     R setMapper(Function<Supplier<R>, R> function) {
        this.function = function;
        return (R) this;
    }


    @SuppressWarnings("unchecked")
    R setAbstractTreeSupplier(Supplier<R> supplier) {
        this.supplier = supplier;
        return (R) this;
    }


    @Override
    public List<S> flatChildTreeNode() {
        if (CollectionUtils.isNotEmpty(childNode)) {
            List<S> collect = childNode.stream().
                    map(AbstractTree::flatChildTreeNode).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
            collect.add(node);
            return collect;
        }
        return Lists.newArrayList(node);
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<R> flatChildTree() {
        if (CollectionUtils.isNotEmpty(childNode)) {
            List<R> collect = childNode.stream().
                    map(Tree::flatChildTree).
                    flatMap(Collection::stream).
                    collect(Collectors.toList());
            collect.add((R)this);
            return collect;
        }
        return Lists.newArrayList((R)this);
    }


    @Override
    public R selectTreeNode(S node, Function<Long, List<S>> operation) {
        node.assertNull();
        R resultNode = function.apply(supplier).setNode(node);
        if (node.judgeParent()) {
            List<R> result = operation.apply(node.getId())
                    .parallelStream()
                    .map(e -> selectTreeNode(e, operation))
                    .collect(Collectors.toList());
            return resultNode.setChildNode(result);
        }
        return resultNode.setChildNode(Collections.emptyList());
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
                map(Tree::flatChildTreeNode).
                flatMap(Collection::stream).
                collect(Collectors.toList());
    }


    @Override
    public List<S> selectFlatTreeNode(S node, Function<Long, List<S>> operation) {
        return this.selectTreeNode(node, operation).flatChildTreeNode();
    }


}
