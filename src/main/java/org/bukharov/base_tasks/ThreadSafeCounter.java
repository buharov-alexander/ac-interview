/**
 * Счётчик для безопасного использования из нескольких потоков.
 * increment и decrement атомарны. Внутри — AtomicInteger на основе CAS операций.
 */
package org.bukharov.base_tasks;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter {

	private AtomicInteger value;

	public ThreadSafeCounter(int initialValue) {
		this.value = new AtomicInteger(initialValue);
	}

	public void increment() {
		value.incrementAndGet();
	}

	public void decrement() {
		value.decrementAndGet();
	}

	public int get() {
		return value.get();
	}

}
