package com.github.yhnysc.replicavt.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ObjectUtil {
    private ObjectUtil(){}

    public static <T> T firstNonNull(T... items){
        return Arrays.stream(items)
                .filter(Objects::nonNull)
                .findFirst()
                .get();
    }

    public static <T> T firstNonNullOrThrow(T... items){
        return Arrays.stream(items)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
    }

    public static <T> List<T> readOnlyCopy(List<T> items){
        return Collections.unmodifiableList(items);
    }
}
