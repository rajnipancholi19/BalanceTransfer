package com.dws.challenge.domain;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
  private String accountId;
  private BigDecimal balance;
  private final Lock lock = new ReentrantLock();

  public Account(String accountId, BigDecimal initialBalance) {
    this.accountId = accountId;
    this.balance = initialBalance;
  }

  public String getAccountId() {
    return accountId;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public Lock getLock() {
    return lock;
  }

  public void deposit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }

  public void withdraw(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }
}
