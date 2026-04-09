package org.bukharov.task_scheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskSchedulerTest {

	@Test
	void test() throws InterruptedException {
		TaskScheduler scheduler = new TaskScheduler();

		CountDownLatch latch = new CountDownLatch(3);
		long start = System.currentTimeMillis();

		// Задачи с разным временем
		scheduler.schedule(() -> {
			long now = System.currentTimeMillis();
			System.out.println("Task 1 executed after " + (now - start) + " ms");
			latch.countDown();
		}, 1000);

		scheduler.schedule(() -> {
			long now = System.currentTimeMillis();
			System.out.println("Task 2 executed after " + (now - start) + " ms");
			latch.countDown();
		}, 500);

		scheduler.schedule(() -> {
			long now = System.currentTimeMillis();
			System.out.println("Task 3 executed after " + (now - start) + " ms");
			latch.countDown();
		}, 1500);

		// Ждём выполнения всех задач, таймаут чуть больше, чем максимальная задержка
		boolean allTasksExecuted = latch.await(2, TimeUnit.SECONDS);

		assertTrue(allTasksExecuted, "All tasks should be executed in time");
	}
}
