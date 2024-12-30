package com.nameless.storage_server.Executor;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages the thread pool for asynchronous task execution.
 */
@Component
public class ExecutorManager {

    /** Thread pool for executing asynchronous tasks. */
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Provides access to the thread pool for executing tasks.
     *
     * @return the ExecutorService instance.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Shuts down the thread pool gracefully when the application is stopped.
     */
    @PreDestroy
    public void shutdownExecutor() {
        if (!executorService.isShutdown()) {
            try {
                executorService.shutdown();
                if (!executorService.awaitTermination(120, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
