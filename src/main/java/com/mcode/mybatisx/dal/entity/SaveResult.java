package com.mcode.mybatisx.dal.entity;

import java.util.Iterator;
import java.util.List;


public class SaveResult {
    /***
     * 受影响行数
     */
    private Integer influencesRow = 0;

    /***
     * 主键
     */
    private List<Object> idList;

    public Object getFirst() {
        return idList.get(0);
    }

    public Object nextId() {
        if (idList == null) {
            return null;
        }
        Iterator<Object> iterator = idList.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public Integer getInfluencesRow() {
        return influencesRow;
    }

    public void setInfluencesRow(Integer influencesRow) {
        this.influencesRow = influencesRow;
    }

    public List<Object> getIds() {
        return idList;
    }

    public void setIdList(List<Object> id) {
        this.idList = id;
    }
}
