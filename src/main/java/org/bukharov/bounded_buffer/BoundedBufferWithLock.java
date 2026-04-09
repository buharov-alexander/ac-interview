/**
 * Реализуй Producer-Consumer с ограниченным буфером
 */
package org.bukharov.bounded_buffer;

import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBufferWithLock {
	private final int capacity;
	private final SequencedCollection<Integer> queue;
	private final ReentrantLock lock;
	private final Condition notFull;
	private final Condition notEmpty;

	public BoundedBufferWithLock(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Invalid capacity");
		}
		this.capacity = capacity;
		this.queue = new LinkedList<>();
		this.lock = new ReentrantLock();
		this.notEmpty = lock.newCondition();
		this.notFull = lock.newCondition();
	}

	public void put(int item) throws InterruptedException {
		lock.lock();
		try {
			while (queue.size() >= capacity) {
				notFull.await();
			}
			queue.addLast(item);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	public int take() throws InterruptedException {
		lock.lock();
		try {
			while (queue.isEmpty()) {
				notEmpty.await();
			}
			Integer first = queue.removeFirst();
			notFull.signal();
			return first;
		} finally {
			lock.unlock();
		}
	}
}
