package com.noken29.vrpjobs.utils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@ToString
@EqualsAndHashCode
public class ComplexKey {
    private final Set<Object> storage;

    public static ComplexKey from(Object... elements) {
        return new ComplexKey(elements);
    }

    ComplexKey(Object... elements) {
        storage = Collections.unmodifiableSet(Set.of(elements));
    }
}
