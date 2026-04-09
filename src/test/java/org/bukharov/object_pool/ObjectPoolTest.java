package org.bukharov.object_pool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectPoolTest {

	@Test
	void objectPoolTest() throws InterruptedException {
		AtomicInteger i = new AtomicInteger();
		ObjectPool<String> objectPool = new ObjectPool<>(2, () -> "String #" + i.getAndIncrement());

		CountDownLatch cdt = new CountDownLatch(3);

		new Thread(() -> {
			takeObject(objectPool, cdt);
		}).start();

		new Thread(() -> {
			takeObject(objectPool, cdt);
		}).start();

		new Thread(() -> {
			takeObject(objectPool, cdt);
		}).start();

		Assertions.assertFalse(cdt.await(1000, TimeUnit.MILLISECONDS), "Not all objects was taken");
		objectPool.release("String #1");

		Assertions.assertTrue(cdt.await(1000, TimeUnit.MILLISECONDS), "Last thread got the object");
	}

	@Test
	void objectPoolTestWithSemaphore() throws InterruptedException {
		AtomicInteger i = new AtomicInteger();
		ObjectPoolWithSemaphore<String> objectPool = new ObjectPoolWithSemaphore<>(2, () -> "String #" + i.getAndIncrement());

		CountDownLatch cdt = new CountDownLatch(3);

		new Thread(() -> {
			takeObject(objectPool, cdt);
		}).start();

		new Thread(() -> {
			takeObject(objectPool, cdt);
		}).start();

		new Thread(() -> {
			takeObject(objectPool, cdt);
		}).start();

		Assertions.assertFalse(cdt.await(1000, TimeUnit.MILLISECONDS), "Not all objects was taken");
		objectPool.release("String #1");

		Assertions.assertTrue(cdt.await(1000, TimeUnit.MILLISECONDS), "Last thread got the object");
	}

	private static void takeObject(ObjectPool<String> objectPool, CountDownLatch cdt) {
		String obj = objectPool.acquire();
		System.out.println("Take: " + obj);
		cdt.countDown();
	}

	private static void takeObject(ObjectPoolWithSemaphore<String> objectPool, CountDownLatch cdt) {
		String obj = objectPool.acquire();
		System.out.println("Take: " + obj);
		cdt.countDown();
	}
}
