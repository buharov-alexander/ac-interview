package org.bukharov.transfer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class Bank {
	public void transferWithDeadlock(Account from, Account to, int amount) {
		synchronized (from) {
			synchronized (to) {
				if (from.getBalance() >= amount) {
					from.withdraw(amount);
					to.deposit(amount);
				}
			}
		}
	}

	public void transferWithOrder(Account from, Account to, int amount) {
		var first = from;
		var second = to;
		if (from.getId() > to.getId()) {
			first = to;
			second = from;
		}
		synchronized (first) {
			synchronized (second) {
				if (from.getBalance() >= amount) {
					from.withdraw(amount);
					to.deposit(amount);
				}
			}
		}
	}

	public void transferWithTryLock(Account from, Account to, int amount) throws InterruptedException {
		ReentrantLock lockFrom = getLock(from);
		ReentrantLock lockTo = getLock(to);

		while (true) {
			if (lockFrom.tryLock(50, TimeUnit.MILLISECONDS)) {
				try {
					if (lockTo.tryLock(50, TimeUnit.MILLISECONDS)) {
						try {
							if (from.getBalance() >= amount) {
								from.withdraw(amount);
								to.deposit(amount);
							}
							return; // успех — выходим
						} finally {
							lockTo.unlock();
						}
					}
				} finally {
					lockFrom.unlock();
				}
			}
			// не удалось захватить оба — делаем паузу и повторяем
			Thread.sleep(10);
		}
	}
	private final ConcurrentHashMap<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();

	private ReentrantLock getLock(Account account) {
		return locks.computeIfAbsent(account.getId(), id -> new ReentrantLock());
	}
}
