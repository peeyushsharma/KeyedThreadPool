package com.peeyush.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyedThreadPoolExecutor<T> {
	final Executor[] executors;

	final ConcurrentHashMap<T, Executor> keyExecutorMap;

	final AtomicInteger lastAssigment = new AtomicInteger(-1);

	public KeyedThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
		this(corePoolSize, threadFactory, new CallerRunsPolicy());
	}

	public KeyedThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		if (corePoolSize <= 0) {
			throw new IllegalArgumentException("invalid core pool size.");
		}

		if (threadFactory == null || handler == null) {
			throw new IllegalArgumentException("threadFactory or handler is null.");
		}

		executors = new Executor[corePoolSize];
		for (int i = 0; i < executors.length; i++) {
			executors[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
					threadFactory);
			((ThreadPoolExecutor) executors[i]).setRejectedExecutionHandler(handler);
		}
		keyExecutorMap = new ConcurrentHashMap<T, Executor>();
	}

	public void execute(T key, Runnable command) {
		Executor executor = keyExecutorMap.get(key);
		if (executor == null) {
			executor = allocateExecutor(key);
		}
		executor.execute(command);
	}

	private Executor allocateExecutor(T key) {
		Executor newExecutor = executors[lastAssigment.incrementAndGet() % executors.length];
		Executor executor = keyExecutorMap.putIfAbsent(key, newExecutor);
		if (executor == null)
			executor = newExecutor;
		return executor;
	}
}
