package com.example.utils;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;
import java.util.concurrent.Callable;

public class Polling {

    private static final RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(30)
            .waitDuration(Duration.ofSeconds(2))
            .build();
    private static final RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
    private static final Retry retry = retryRegistry.retry("retryService");

    public static <T> T executeWithRetry(Callable<T> task) throws Exception {
        return Retry.decorateCallable(retry, task).call();
    }
}
