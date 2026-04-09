package org.bukharov.base_tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RateLimiterTest {

	@Test
	void simpleTest() {
		RateLimiter rl = new RateLimiter(3, 5);

		Assertions.assertTrue(rl.allowRequest(1000), "Unexpected reject");
	}

	@Test
	void rejectTest() {
		RateLimiter rl = new RateLimiter(3, 5);

		Assertions.assertTrue(rl.allowRequest(1000), "Unexpected reject");
		Assertions.assertTrue(rl.allowRequest(1001), "Unexpected reject");
		Assertions.assertTrue(rl.allowRequest(1002), "Unexpected reject");

		Assertions.assertFalse(rl.allowRequest(1003), "Expect reject");
	}

	@Test
	void slideWindowTest() {
		RateLimiter rl = new RateLimiter(2, 1);

		Assertions.assertTrue(rl.allowRequest(1000), "Unexpected reject");
		Assertions.assertTrue(rl.allowRequest(1001), "Unexpected reject");

		Assertions.assertFalse(rl.allowRequest(2000), "Expect reject");
		Assertions.assertTrue(rl.allowRequest(2001), "Unexpected reject");
	}
}
