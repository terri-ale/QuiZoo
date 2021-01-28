package com.example.quizoo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
// 444 8211
    public static final ExecutorService threadExecutorPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
}
