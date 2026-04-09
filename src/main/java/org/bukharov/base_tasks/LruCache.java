/**
 * Кэш с фиксированной ёмкостью.
 * При переполнении вытесняет наименее недавно использованный элемент.
 * Внутри — HashMap + LinkedList. get и put обновляют порядок использования.
 */
package org.bukharov.base_tasks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SequencedCollection;

public class LruCache {
	public static final int NOT_FOUND_VALUE = -1;
	private final int capacity;

	private final Map<Integer, Integer> map;
	private final SequencedCollection<Integer> queue;

	public LruCache(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException("Invalid capacity. Capacity should be > 0");
		}
		this.capacity = capacity;
		this.map = new HashMap<>();
		this.queue = new LinkedList<>();
	}

	public int get(int key) {
		Integer value = map.get(key);
		if (value == null) return NOT_FOUND_VALUE;
		updateQueue(key);
		return value;
	}

	public void put(int key, int value) {
		Integer prevValue = map.put(key, value);
		if (prevValue == null) {
			if (queue.size() >= capacity) cleanOld();
			queue.addLast(key);
		} else {
			updateQueue(key);
		}
	}

	private void cleanOld() {
		Integer removedKey = queue.removeFirst();
		map.remove(removedKey);
	}


	private void updateQueue(int key) {
		queue.remove(key);
		queue.addLast(key);
	}
}
