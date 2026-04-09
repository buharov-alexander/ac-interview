/**
 * Ограничитель количества запросов за скользящее окно времени.
 * allowRequest возвращает true если лимит не превышен, false если превышен.
 * Внутри — очередь timestamps, устаревшие удаляются с головы.
 */
package org.bukharov.base_tasks;

import java.util.LinkedList;
import java.util.SequencedCollection;

public class RateLimiter {

	public static final long MILLISECONDS_IN_SECOND = 1000;
	private final int maxRequests;
	private final int windowSeconds;

	private final SequencedCollection<Long> queue;

	public RateLimiter(int maxRequests, int windowSeconds) {
		this.maxRequests = maxRequests;
		this.windowSeconds = windowSeconds;
		this.queue = new LinkedList<>();
	}

	public boolean allowRequest(long timestamp) {
		cleanQueue(timestamp);
		if (queue.size() >= maxRequests) return false;
		queue.addLast(timestamp);
		return true;
	}

	private void cleanQueue(long timestamp) {
		long left = timestamp - windowSeconds * MILLISECONDS_IN_SECOND;
		while(!queue.isEmpty() && queue.getFirst() < left) {
			queue.removeFirst();
		}
	}
}
