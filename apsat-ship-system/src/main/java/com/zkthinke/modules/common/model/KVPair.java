package com.zkthinke.modules.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author weicb
 * @date 2020/1/3 11:00
 */
@Data
public class KVPair<K, V>  implements Serializable {

    private K name;
    private V value;

    public static <F, S> KVPair<F, S> create(F f, S s) {
        KVPair p = new KVPair();
        p.setName(f);
        p.setValue(s);
        return p;
    }

    public static <K, U> Collector<? super KVPair<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(p -> p.getName(), p -> p.getValue());
    }
}
