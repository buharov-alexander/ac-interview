/**
 * Реализуй Producer-Consumer с ограниченным буфером
 */
package org.bukharov.bounded_buffer;

import java.util.LinkedList;
import java.util.SequencedCollection;

class BoundedBuffer {
	private final int capacity;
	private final SequencedCollection<Integer> queue;

	public BoundedBuffer(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Invalid capacity");
		}
		this.capacity = capacity;
		this.queue = new LinkedList<>();
	}

	public synchronized void put(int item) throws InterruptedException {
		while (queue.size() >= capacity) {
			this.wait();
		}
		queue.addLast(item);
		this.notifyAll();
	}

	public synchronized int take() throws InterruptedException {
		while (queue.isEmpty()) {
			this.wait();
		}
		Integer first = queue.removeFirst();
		this.notifyAll();
		return first;
	}
}
