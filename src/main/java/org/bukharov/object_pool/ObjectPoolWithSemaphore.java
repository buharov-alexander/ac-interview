package org.bukharov.object_pool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class ObjectPoolWithSemaphore<T> {

	private final SequencedCollection<T> freeObjects;
	private final Set<T> occupiedObjects;
	private final Semaphore semaphore;

	ObjectPoolWithSemaphore(int size, Supplier<T> factory) {
		occupiedObjects = new HashSet<>();
		freeObjects = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			freeObjects.addFirst(factory.get());
		}

		semaphore = new Semaphore(size);
	}

	public T acquire() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		synchronized (this) {
			T obj = freeObjects.removeFirst();
			occupiedObjects.add(obj);
			return obj;
		}
	}

	public void release(T obj) {
		synchronized (this) {
			occupiedObjects.remove(obj);
			freeObjects.addFirst(obj);
		}

		semaphore.release();
	}
}
