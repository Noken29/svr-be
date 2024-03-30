package com.noken29.vrpjobs.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Distribution {

    public static int discrete(List<BigDecimal> weights, BigDecimal total) {
        if (Objects.equals(total, BigDecimal.ZERO))
            throw new IllegalArgumentException("Nothing to choose. Sum of weights is equal to 0.");

        BigDecimal sum = BigDecimal.ZERO;
        int index = 0;

        for (int i = 0; i < weights.size() && Set.of(-1, 0).contains(sum.compareTo(total.multiply(BigDecimal.valueOf(Math.random())))); i++) {
            sum = sum.add(weights.get(i));
            if (!Objects.equals(weights.get(i), BigDecimal.ZERO))
                index = i;
        }
        return index;
    }
}
