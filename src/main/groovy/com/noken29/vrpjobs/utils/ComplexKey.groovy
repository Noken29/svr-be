package com.noken29.vrpjobs.utils

class ComplexKey<T, U> {
    T first
    U second
    Set<Object> set

    ComplexKey(T first, U second) {
        this.first = first
        this.second = second
        set = [first, second]
    }

    static ComplexKey<T, U> from(T first, U second) {
        return new ComplexKey<T, U>(first, second)
    }

    int hashCode() {
        return set.hashCode()
    }
}