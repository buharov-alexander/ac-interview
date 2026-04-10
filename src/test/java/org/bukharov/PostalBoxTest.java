package org.bukharov;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PostalBoxTest {

	private static UserNotificationApi notificationApi;

	@BeforeAll
	static void setUp() {
		notificationApi = mock(UserNotificationApi.class);
	}

	@Test
	void negativeCreateTest() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new PostalBox(null, 3));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new PostalBox(notificationApi, 0));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new PostalBox(notificationApi, -1));
	}

	@Test
	void placeOrderTest() {
		PostalBox postalBox = new PostalBox(notificationApi, 3);

		int boxNumber = postalBox.placeOrder(1);
		Assertions.assertTrue(boxNumber > 0);
		verify(notificationApi).sendMessage(anyInt(), anyString());
	}

	@Test
	void getOrderTest() {
		PostalBox postalBox = new PostalBox(notificationApi, 3);

		int boxNumber = postalBox.placeOrder(1);
		Assertions.assertTrue(boxNumber > 0);

		Optional<Integer> firstKey = postalBox.boxMap.keySet().stream().findFirst();
		Assertions.assertTrue(firstKey.isPresent());

		String message = postalBox.getOrder(firstKey.get());
		Assertions.assertNotNull(message);
		Assertions.assertTrue(message.contains("1"));
	}
}
