package com.datum.client.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.Instant;


@MappedSuperclass
@Data
public abstract class AuditableEntity {

    @Column(name = "crtdt")
    private Instant createdAt;

    @Column(name = "updtdt")
    private Instant updatedAt;


    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
