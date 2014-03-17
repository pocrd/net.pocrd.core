package net.pocrd.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 658416766948053541L;
    private List<T>           list;                                  // list result of this page
    private int               totalCount;                            // totalCount of　data
    private int               pageIndex;
    private int               pageSize;

    /**
     * @param list
     * @param totalCount
     */
    public Page(List<T> list, int totalCount, int pageIndex, int pageSize) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Page(int totalCount, int pageIndex, int pageSize) {
        this.list = new ArrayList<T>(totalCount > pageIndex * pageSize ? pageSize : totalCount - (pageIndex - 1) * pageSize);
        this.totalCount = totalCount;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public void addItem(T item) {
        list.add(item);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}