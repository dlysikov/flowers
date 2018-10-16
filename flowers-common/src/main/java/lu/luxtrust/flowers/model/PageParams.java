package lu.luxtrust.flowers.model;

import java.util.ArrayList;
import java.util.List;

public class PageParams {
    private Integer pageSize;
    private Integer pageNumber;
    private List<FieldFilter> filter = new ArrayList<>();

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<FieldFilter> getFilter() {
        return filter;
    }

    public void setFilter(List<FieldFilter> filter) {
        this.filter = filter;
    }
}
