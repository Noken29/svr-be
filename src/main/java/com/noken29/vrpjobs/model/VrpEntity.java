package com.noken29.vrpjobs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
abstract class VrpEntity {
    private final String index;
    private final Long id;

    protected VrpEntity(Long id) {
        this.index = UUID.randomUUID().toString();
        this.id = id;
    }
}
