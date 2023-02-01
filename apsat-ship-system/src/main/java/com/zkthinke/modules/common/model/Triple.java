package com.zkthinke.modules.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author weicb
 * @date 2020/1/3 17:45
 */
@Data
public class Triple<F, S, T> extends KVPair<F, S> implements Serializable {

    private T third = null;

    public static <F, S, T> Triple<F, S, T> create(F f, S s, T t) {
        Triple p = new Triple();
        p.setName(f);
        p.setValue(s);
        p.setThird(t);
        return p;
    }

    public static <F, S, T> Triple<F, S, T> create(KVPair<F, S> pair, T t) {
        return create(pair.getName(), pair.getValue(), t);
    }

    public static <K, U> Collector<? super Triple<K, U, ?>, ?, Map<K, U>> toMap12() {
        return Collectors.toMap(t -> t.getName(), t -> t.getValue());
    }

    public static <K, U> Collector<? super Triple<K, ?, U>, ?, Map<K, U>> toMap13() {
        return Collectors.toMap(t -> t.getName(), t -> t.getThird());
    }

    public static <K, U> Collector<? super Triple<?, K, U>, ?, Map<K, U>> toMap23() {
        return Collectors.toMap(t -> t.getValue(), t -> t.getThird());
    }
}
