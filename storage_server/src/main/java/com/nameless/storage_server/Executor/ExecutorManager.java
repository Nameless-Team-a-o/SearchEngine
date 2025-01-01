package com.nameless.storage_server.Executor;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Manages the thread pool for asynchronous task execution.
 */
@Component
public class ExecutorManager {

    /** Thread pool for executing asynchronous tasks. */
    private final ExecutorService executorService;

    /**
     * Initializes the ExecutorService with a dynamic thread pool.
     */
    public ExecutorManager() {
        // Creating a ThreadPoolExecutor with flexible settings.
        this.executorService = new ThreadPoolExecutor(
                10, // core pool size (minimum threads)
                20, // maximum pool size (maximum threads)
                60L, TimeUnit.SECONDS, // time to keep idle threads alive
                new LinkedBlockingQueue<>(100) // queue to hold tasks waiting for execution
        );
    }

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
                // Initiating an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
                executorService.shutdown();

                // Wait for all tasks to finish, if they don’t finish in 90 seconds, force shutdown.
                if (!executorService.awaitTermination(900, TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // Forcing shutdown if tasks do not complete in time.
                }
            } catch (InterruptedException e) {
                // If shutdown is interrupted, force the shutdown immediately.
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
