package com.hsuk.video.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(EntityListener.class)
public abstract class AbstractEntity implements Serializable {

    @Column(name = "created_at", nullable = false, insertable = true, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false, insertable = true, updatable = false)
    protected String createdBy;

    @Column(name = "modified_at")
    protected LocalDateTime modifiedAt;

    @Column(name = "modified_by")
    protected String modifiedBy;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    protected String deletedBy;

    @Column(name = "is_deleted")
    protected Boolean isDeleted = false;
}
