package org.bukharov;

/**
 * Синхронный клиент, вызывающий postalbox.notify.market.yandex.net
 * Реализацию интерфейса описывать не нужно.
 */
interface UserNotificationApi {
	// нужно описать метод(ы) для отправки сообщения с кодом выдачи

	void sendMessage(int orderId, String sms);
}