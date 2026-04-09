package org.bukharov.task_scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskScheduler {

	private final PriorityQueue<Task> queue;
	private final Thread worker;
	private final ReentrantLock lock;
	private final Condition hasTask;

	TaskScheduler() {
		lock = new ReentrantLock();
		hasTask = lock.newCondition();
		queue = new PriorityQueue<>(Comparator.comparing(Task::time));

		worker = new Thread(() -> {
			while (true) {
				lock.lock();
				Task task;
				try {
					while (queue.isEmpty()) {
						hasTask.await();
					}

					task = queue.peek();
					long delay = task.time() - System.currentTimeMillis();
					while (delay > 0) {
						hasTask.await(delay, TimeUnit.MILLISECONDS);
						task = queue.peek();
						delay = task.time() - System.currentTimeMillis();
					}
					queue.poll();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} finally {
					lock.unlock();
				}
				task.runnable().run();
			}
		});

		worker.start();
	}

	void schedule(Runnable task, long delayMillis) {
		lock.lock();
		try {
			long time = System.currentTimeMillis() + delayMillis;
			queue.add(new Task(task, time));
			hasTask.signal();
		} finally {
			lock.unlock();
		}
	}
}
