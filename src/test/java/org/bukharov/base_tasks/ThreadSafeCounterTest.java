package org.bukharov.base_tasks;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThreadSafeCounterTest {

	@Test
	void simpleTest() {
		ThreadSafeCounter counter = new ThreadSafeCounter(0);

		counter.increment();
		Assertions.assertEquals(1, counter.get(), "Unexpected counter value");
		counter.increment();
		Assertions.assertEquals(2, counter.get(), "Unexpected counter value");
		counter.decrement();
		Assertions.assertEquals(1, counter.get(), "Unexpected counter value");
	}

	@Test
	void initialValueTest() {
		ThreadSafeCounter counter = new ThreadSafeCounter(-10);

		counter.increment();
		Assertions.assertEquals(-9, counter.get(), "Unexpected counter value");
	}

	@Test
	void concurrentIncrementTest() throws InterruptedException {
		ThreadSafeCounter counter = new ThreadSafeCounter(0);

		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				counter.increment();
			}
		});

		Thread t2 = new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				counter.increment();
			}
		});

		t1.start();
		t2.start();

		Assertions.assertTrue(t1.join(Duration.ofMillis(500)));
		Assertions.assertTrue(t2.join(Duration.ofMillis(500)));

		Assertions.assertEquals(200, counter.get());
	}

	@Test
	void concurrentDecrementTest() throws InterruptedException {
		ThreadSafeCounter counter = new ThreadSafeCounter(0);

		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				counter.decrement();
			}
		});

		Thread t2 = new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				counter.decrement();
			}
		});

		t1.start();
		t2.start();

		Assertions.assertTrue(t1.join(Duration.ofMillis(500)));
		Assertions.assertTrue(t2.join(Duration.ofMillis(500)));

		Assertions.assertEquals(-200, counter.get());
	}

	@Test
	void concurrentIncrementAndDecrementTest() throws InterruptedException {
		ThreadSafeCounter counter = new ThreadSafeCounter(0);

		Thread t1 = new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				counter.increment();
			}
		});

		Thread t2 = new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				counter.decrement();
			}
		});

		t1.start();
		t2.start();

		Assertions.assertTrue(t1.join(Duration.ofMillis(500)));
		Assertions.assertTrue(t2.join(Duration.ofMillis(500)));

		Assertions.assertEquals(0, counter.get());
	}
}
