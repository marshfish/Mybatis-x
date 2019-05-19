package com.mcode.mybatisx.dal.entity;

import lombok.Data;

import java.util.Collections;
import java.util.List;


@Data
public class PagingDO<T> {
    /***
     * 总条数x
     */
    private Integer total;

    /***
     * 总页数
     */
    private Integer pageTotal;

    /***
     * 当前页
     */
    private Integer currentPage;

    /***
     * 内容
     */
    private List<T> list;

    /***
     * 获取总页数
     * @param total 总条数
     * @param pageSize 每页大小
     * @return int
     */
    public static int getPageTotal(int total, int pageSize){
        return (total % pageSize) > 0 ? total / pageSize + 1 : total / pageSize;
    }

    public static <T> PagingDO<T> emptyVO(){
        PagingDO<T> vo = new PagingDO<>();
        vo.setList(Collections.<T>emptyList());
        vo.setTotal(0);
        vo.setCurrentPage(1);
        vo.setPageTotal(1);
        return vo;
    }
}
