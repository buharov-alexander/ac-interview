/**
 * Очередь с ограниченной ёмкостью для многопоточного использования.
 * put блокирует поток если очередь полна, take блокирует если очередь пуста.
 * Внутри — ReentrantLock + два Condition (notFull, notEmpty).
 */
package org.bukharov.base_tasks;

import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<T> {

	private final int capacity;
	private final ReentrantLock lock;
	private final Condition notFull;
	private final Condition notEmpty;
	private final SequencedCollection<T> queue;

	public BoundedBlockingQueue(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Invalid capacity");
		}
		this.capacity = capacity;
		this.queue = new LinkedList<>();
		this.lock = new ReentrantLock();
		this.notFull = lock.newCondition();
		this.notEmpty = lock.newCondition();
	}

	public void put(T item) throws InterruptedException {
		if (item == null) {
			throw new IllegalArgumentException("Cannot put null");
		}
		lock.lock();
		try {
			while (queue.size() == capacity) {
				notFull.await();
			}
			queue.addLast(item);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}

	}

	public T take() throws InterruptedException {
		lock.lock();
		try {
			while (queue.isEmpty()) {
				notEmpty.await();
			}
			T returnedItem = queue.removeFirst();
			notFull.signal();
			return returnedItem;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		return queue.size();
	}
}
