/**
 * Ограничитель одновременного доступа к ресурсу.
 * acquire захватывает разрешение и блокирует поток если все разрешения заняты,
 * release освобождает разрешение и будит один ожидающий поток.
 * Внутри — ReentrantLock + один Condition + счётчик доступных разрешений.
 */
package org.bukharov.base_tasks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {

	private final int permits;
	// added volatile to get correct availablePermits() behavior
	private volatile int availablePermits;
	private final ReentrantLock lock;
	private final Condition hasPermits;

	public Semaphore(int permits) {
		if (permits < 1) {
			throw new IllegalArgumentException("Invalid permits");
		}
		this.permits = permits;
		this.availablePermits = permits;
		this.lock = new ReentrantLock();
		this.hasPermits = lock.newCondition();
	}

	public void acquire() throws InterruptedException {
		lock.lock();
		try {
			while (availablePermits < 1) {
				hasPermits.await();
			}
			availablePermits--;
		} finally {
			lock.unlock();
		}

	}

	public void release() {
		lock.lock();
		try {
			if (availablePermits == permits) return;
			availablePermits++;
			hasPermits.signal();
		} finally {
			lock.unlock();
		}
	}

	public int availablePermits() {
		return availablePermits;
	}
}
