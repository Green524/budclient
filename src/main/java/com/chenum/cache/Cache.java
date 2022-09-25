package com.chenum.cache;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    public static final Map<String,Object> cache = new HashMap<>();


    public static void put(String key,String value){
        cache.put(key,value);
    }

    public static Object get(String key){
        return cache.get(key);
    }
}
