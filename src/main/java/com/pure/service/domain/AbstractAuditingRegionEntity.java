package com.pure.service.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractAuditingRegionEntity extends AbstractAuditingEntity {

    @Column(name = "region_id", length = 10, nullable = false)
    private Long regionId;

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}
