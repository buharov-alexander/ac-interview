package org.bukharov.transfer;

class Account {
	private final int id;
	private int balance;

	public Account(int id, int balance) {
		this.id = id;
		this.balance = balance;
	}

	public int getId() { return id; }
	public int getBalance() { return balance; }

	public void deposit(int amount) { balance += amount; }
	public void withdraw(int amount) { balance -= amount; }
}