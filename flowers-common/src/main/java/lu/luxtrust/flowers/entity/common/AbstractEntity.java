package lu.luxtrust.flowers.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AbstractEntity {

    @JsonIgnore
    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @PrePersist
    public void initCreateTime() {
        this.createdTime = new Date();
        this.lastUpdateTime = new Date();
    }

    @PreUpdate
    public void initLastUpdateTime() {
        if (this.createdTime == null) {
            this.createdTime = new Date();
        }
        this.lastUpdateTime = new Date();
    }
}
