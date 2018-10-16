package lu.luxtrust.flowers.model;

import java.util.List;

public class PageResponse<T> {
    private List<T> data;
    private Long totalCount;

    public PageResponse(List<T> data, Long totalCount) {
        this.data = data;
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
