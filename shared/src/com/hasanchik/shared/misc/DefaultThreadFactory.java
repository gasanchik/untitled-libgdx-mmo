package com.hasanchik.shared.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class DefaultThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);

        final Logger logger = LogManager.getLogger(thread);
        thread.setUncaughtExceptionHandler((thread2, exception) -> exception.printStackTrace());
        return thread;
    }
}