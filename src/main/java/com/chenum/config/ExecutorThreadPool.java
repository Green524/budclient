package com.chenum.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorThreadPool {

    private static ExecutorService service = Executors.newFixedThreadPool(10);

    public static ExecutorService service() {
        return service;
    }
}
