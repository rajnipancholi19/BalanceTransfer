package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.FundTransferService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FundTransferServiceTest {

    private FundTransferService fundTransferService;
    private AccountsService accountsService;
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {

        accountsService = mock(AccountsService.class);
        notificationService = mock(NotificationService.class);

        fundTransferService = new FundTransferService(accountsService, notificationService);
    }

    @Test
    public void testSimpleTransfer() {

        Account accountFrom = new Account("1", new BigDecimal("100.00"));
        Account accountTo = new Account("2", new BigDecimal("50.00"));


        when(accountsService.getAccount("1")).thenReturn(accountFrom);
        when(accountsService.getAccount("2")).thenReturn(accountTo);


        fundTransferService.transferMoney("1", "2", new BigDecimal("50.00"));

        // Check if account 1 balance is reduced by 50.00
        assertEquals(new BigDecimal("50.00"), accountFrom.getBalance());

        // Check if account 2 balance is increased by 50.00
        assertEquals(new BigDecimal("100.00"), accountTo.getBalance());

        // Verify that notifications were sent to both accounts about the transfer
        verify(notificationService).notifyAboutTransfer(accountFrom, "2");
        verify(notificationService).notifyAboutTransfer(accountTo, "1");
    }
}
