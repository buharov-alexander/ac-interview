package org.bukharov.base_tasks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BoundedBlockingQueueTest<T> {

	@Test
	public void simpleTest() throws InterruptedException {
		var queue = new BoundedBlockingQueue<String>(3);

		String item1 = "First";
		String item2 = "Second";
		queue.put(item1);
		queue.put(item2);

		String first = queue.take();
		String second = queue.take();
		Assertions.assertEquals(item1, first, "Unexpected element");
		Assertions.assertEquals(item2, second, "Unexpected element");
		Assertions.assertEquals(0, queue.size(), "Size is not valid");
	}

	@Test
	public void putNullTest() {
		var queue = new BoundedBlockingQueue<String>(3);

		var ex = Assertions.assertThrows(IllegalArgumentException.class, () -> queue.put(null));
		Assertions.assertEquals("Cannot put null", ex.getMessage());
	}

	@Test
	public void invalidCapacityTest() {
		var ex1 = Assertions.assertThrows(IllegalArgumentException.class, () -> new BoundedBlockingQueue<String>(-1));
		Assertions.assertEquals("Invalid capacity", ex1.getMessage());

		var ex2 = Assertions.assertThrows(IllegalArgumentException.class, () -> new BoundedBlockingQueue<String>(0));
		Assertions.assertEquals("Invalid capacity", ex2.getMessage());
	}

	@Test
	public void blockPutTest() throws InterruptedException {
		var queue = new BoundedBlockingQueue<String>(2);
		String item1 = "First";
		String item2 = "Second";
		String item3 = "Third";
		queue.put(item1);
		queue.put(item2);

		CountDownLatch startPut = new CountDownLatch(1);
		CountDownLatch finishPut = new CountDownLatch(1);

		Thread t1 = new Thread(() -> {
			startPut.countDown();
			try {
				queue.put(item3);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			finishPut.countDown();
		});

		t1.start();

		startPut.await();
		Assertions.assertFalse(finishPut.await(100, TimeUnit.MILLISECONDS), "put was not blocked");

		String returnItem = queue.take();
		Assertions.assertTrue(finishPut.await(100, TimeUnit.MILLISECONDS), "put was not unblocked");
		Assertions.assertEquals(item1, returnItem, "Unexpected item");
	}

	@Test
	public void blockTakeTest() throws InterruptedException {
		var queue = new BoundedBlockingQueue<String>(2);
		String item1 = "First";
		AtomicReference<String> returnItem = new AtomicReference<>();

		CountDownLatch startTake = new CountDownLatch(1);
		CountDownLatch finishTake = new CountDownLatch(1);

		Thread t1 = new Thread(() -> {
			startTake.countDown();
			try {
				returnItem.set(queue.take());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			finishTake.countDown();
		});

		t1.start();

		startTake.await();
		Assertions.assertFalse(finishTake.await(100, TimeUnit.MILLISECONDS), "take was not blocked");

		queue.put(item1);
		Assertions.assertTrue(finishTake.await(100, TimeUnit.MILLISECONDS), "take was not unblocked");
		Assertions.assertEquals(item1, returnItem.get(), "Unexpected item");
	}

}
