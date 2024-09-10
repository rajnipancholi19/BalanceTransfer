package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

@Service
public class FundTransferService {

    private final AccountsService accountsService;
    private final NotificationService notificationService;

    @Autowired
    public FundTransferService(AccountsService accountsService, NotificationService notificationService) {
        this.accountsService = accountsService;
        this.notificationService = notificationService;
    }

    public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }

        // Fetch the account objects
        Account accountFrom = accountsService.getAccount(accountFromId);
        Account accountTo = accountsService.getAccount(accountToId);

        // Lock accounts in a consistent order to avoid deadlock
        Lock firstLock = accountFromId.compareTo(accountToId) < 0 ? accountFrom.getLock() : accountTo.getLock();
        Lock secondLock = accountFromId.compareTo(accountToId) < 0 ? accountTo.getLock() : accountFrom.getLock();

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                // Ensure no overdraft
                if (accountFrom.getBalance().compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient funds in accountFrom.");
                }

                // Perform the transfer
                accountFrom.withdraw(amount);
                accountTo.deposit(amount);

                // Notify both accounts
                notificationService.notifyAboutTransfer(accountFrom, "2");
                notificationService.notifyAboutTransfer(accountTo, "1");
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }
}
