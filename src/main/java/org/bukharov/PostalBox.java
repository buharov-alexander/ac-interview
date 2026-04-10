package org.bukharov;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.SequencedSet;

/**
 * Постамат - автоматическая станция приёма/выдачи посылок.
 * В маркете формируются заказы, и хочется добавить возможность получения через постамат.
 * Запускаем MVP: небольшая аудитория пользователей, несколько постаматов в Москве.
 * При заказе пользователь сможет выбрать, что хочет получить заказ в постамате.
 * <p>
 * В рамках задачи нужно реализовать код для MVP решения:
 * - курьер привозит заказ и пробует положить его в ячейку, указывая номер заказа. Постамат сам выбирает ячейку и возвращает в ответ. Она откроется вызывающим этот метод кодом.
 * - после того, как заказ положили в ячейку, пользователю отправляется СМС c кодом получения. Заказ будет ждать вечно
 * - в случае любых ошибок - курьер забирает заказ назад и попробует положить заказ в ячейку на следующий день (для MVP это ок)
 * - пользователь может получить заказ по коду выдачи из СМС. При вводе кода выдачи постамат должен вывести на экран текст "ваш заказ ХХХ в ячейке YYY", ячейка откроется сама.
 * <p>
 * Ограничения:
 * - все ячейки одного размера, но их может быть разное количество, зависит от конкретного постамата
 * - один заказ - одна коробка, она влезает в ячейку
 * - ячейки каждого постамата пронумерованы
 * - каждый постамат сам хранит своё состояние
 * <p>
 * Для отправки сообщения пользователю надо использовать клиент UserNotificationApi.
 */
class PostalBox {
	public static final int INVALID_BOX_INDEX = -1;
	private final UserNotificationApi notificationApi;
	private final SequencedSet<Integer> freeBoxNumbers;
	final Map<Integer, Box> boxMap;

	public PostalBox(UserNotificationApi notificationApi, int size) {
		if (notificationApi == null || size < 1) {
			throw new IllegalArgumentException("Invalid parameters");
		}
		this.notificationApi = notificationApi;
		this.freeBoxNumbers = new LinkedHashSet<>();
		for (int i = 0; i < size; i++) {
			freeBoxNumbers.addFirst(i);
		}
		this.boxMap = new HashMap<>();
	}

	public int placeOrder(int orderId) {
		// получить boxNumber
		if (freeBoxNumbers.isEmpty()) {
			throw new RuntimeException("No free boxes");
		}
		if (boxMap.values().stream().anyMatch(box -> orderId == box.orderId())) {
			throw new RuntimeException("Order duplicate");
		}
		// сгенерировать code
		int code = generateCode();
		// TODO: check collision
		String sms = String.format("ваш код для получения %d", code);
		try {
			notificationApi.sendMessage(orderId, sms);
		} catch (Exception e) {
			return INVALID_BOX_INDEX;
		}
		int boxNumber = freeBoxNumbers.removeFirst();
		boxMap.put(code, new Box(boxNumber, orderId));
		return boxNumber;
	}

	public String getOrder(int receiveCode) {
		Box box = boxMap.get(receiveCode);
		if (box == null) {
			throw new RuntimeException("Box not found");
		}
		int orderId = box.orderId();
		int boxNumber = box.boxNumber();
		freeBoxNumbers.add(boxNumber);
		boxMap.remove(receiveCode);
		return String.format("ваш заказ %d в ячейке %d", orderId, boxNumber);
	}

	private int generateCode() {
		return (int) ((Math.random() * (9999 - 1000)) + 1000);
	}
}
