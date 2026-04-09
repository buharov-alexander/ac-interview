package org.bukharov.object_pool;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ObjectPool<T> {

	private final SequencedCollection<T> freeObjects;
	private final Set<T> occupiedObjects;
	private final ReentrantLock lock;
	private final Condition hasFree;

	ObjectPool(int size, Supplier<T> factory) {
		occupiedObjects = new HashSet<>();
		freeObjects = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			freeObjects.addFirst(factory.get());
		}

		lock = new ReentrantLock();
		hasFree = lock.newCondition();
	}

	public T acquire() {
		lock.lock();
		try {
			while (freeObjects.isEmpty()) {
				hasFree.await();
			}
			T obj = freeObjects.removeFirst();
			occupiedObjects.add(obj);
			return obj;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	public void release(T obj) {
		lock.lock();
		try {
			occupiedObjects.remove(obj);
			freeObjects.addFirst(obj);
			hasFree.signal();
		} finally {
			lock.unlock();
		}
	}
}
