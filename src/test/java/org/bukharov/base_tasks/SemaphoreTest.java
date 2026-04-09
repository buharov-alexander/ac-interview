package org.bukharov.base_tasks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SemaphoreTest {

	@Test
	void simpleTest() throws InterruptedException {
		Semaphore semaphore = new Semaphore(2);

		semaphore.acquire();
		semaphore.acquire();
		Assertions.assertEquals(0, semaphore.availablePermits(), "Unexpected permit count");
		semaphore.release();
		Assertions.assertEquals(1, semaphore.availablePermits(), "Unexpected permit count");
	}

	@Test
	void invalidPermitsTest() {
		var ex1 = Assertions.assertThrows(IllegalArgumentException.class, () -> new Semaphore(0));
		Assertions.assertEquals("Invalid permits", ex1.getMessage());

		var ex2 = Assertions.assertThrows(IllegalArgumentException.class, () -> new Semaphore(-1));
		Assertions.assertEquals("Invalid permits", ex2.getMessage());
	}

	@Test
	void blockThreadTest() throws InterruptedException {
		Semaphore semaphore = new Semaphore(2);

		semaphore.acquire();
		semaphore.acquire();
		Assertions.assertEquals(0, semaphore.availablePermits(), "Unexpected permit count");

		CountDownLatch beforeAcquire = new CountDownLatch(1);
		CountDownLatch afterAcquire = new CountDownLatch(1);

		Thread t1 = new Thread(() -> {
			beforeAcquire.countDown();
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			afterAcquire.countDown();
		});

		t1.start();

		beforeAcquire.await();
		Assertions.assertFalse(afterAcquire.await(100, TimeUnit.MILLISECONDS), "Thread was not blocked");
		Assertions.assertEquals(0, semaphore.availablePermits(), "Unexpected permit count");

		semaphore.release();
		Assertions.assertTrue(afterAcquire.await(100, TimeUnit.MILLISECONDS), "Thread was not released");
		Assertions.assertEquals(0, semaphore.availablePermits(), "Unexpected permit count");
	}
}
