/***************       BEGIN-STANDARD-COPYRIGHT      ***************

 Copyright (c) 2009-2026, Spirent Communications.

 All rights reserved. Proprietary and confidential information of Spirent Communications.
 ***************        END-STANDARD-COPYRIGHT       ***************/
package org.bukharov;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LruCacheTest {

	@Test
	void simpleTest() {
		LruCache cache = new LruCache(3);

		cache.put(1, 2);
		Assertions.assertEquals(2, cache.get(1), "value not found");
	}

	@Test
	void notFoundTest() {
		LruCache cache = new LruCache(3);

		Assertions.assertEquals(-1, cache.get(2), "unexpected default value");
	}

	@Test
	void overrideTest() {
		LruCache cache = new LruCache(3);

		cache.put(1, 2);
		Assertions.assertEquals(2, cache.get(1), "value not found");
		cache.put(1, 3);
		Assertions.assertEquals(3, cache.get(1), "unexpected value");
	}

	@Test
	void capacityTest_byPut() {
		LruCache cache = new LruCache(2);

		cache.put(1, 2);
		cache.put(2, 3);
		cache.put(3, 4);
		Assertions.assertEquals(-1, cache.get(1), "value should be not found");
		Assertions.assertEquals(3, cache.get(2), "unexpected value");
		Assertions.assertEquals(4, cache.get(3), "unexpected value");
	}

	@Test
	void capacityTest_byGet() {
		LruCache cache = new LruCache(2);

		cache.put(1, 2);
		cache.put(2, 3);
		cache.get(1);
		cache.put(3, 4);
		Assertions.assertEquals(2, cache.get(1), "unexpected value");
		Assertions.assertEquals(-1, cache.get(2), "value should be not found");
		Assertions.assertEquals(4, cache.get(3), "unexpected value");
	}

	@Test
	void badCapacityTest() {
		var ex1 = Assertions.assertThrows(IllegalArgumentException.class, () -> new LruCache(0));
		Assertions.assertEquals("Invalid capacity. Capacity should be > 0", ex1.getMessage());

		var ex2 = Assertions.assertThrows(IllegalArgumentException.class, () -> new LruCache(-1));
		Assertions.assertEquals("Invalid capacity. Capacity should be > 0", ex2.getMessage());
	}
}
