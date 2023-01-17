package com.lit.bank.service;

import com.lit.bank.transaction.dto.TransactionDTO;
import com.lit.bank.transaction.exception.TransactionStatementMismatchException;
import com.lit.bank.transaction.model.BankAccount;
import com.lit.bank.transaction.model.Transaction;
import com.lit.bank.transaction.repository.BankAccountRepository;
import com.lit.bank.transaction.repository.TransactionRepository;
import com.lit.bank.transaction.service.TransactionService;
import com.lit.bank.transaction.util.TransactionMapper;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepositoryMock;
    @Mock
    private TransactionRepository transactionRepositoryMock;
    @Mock
    private TransactionMapper transactionMapperMock;

    @InjectMocks
    private TransactionService transactionService;

    TransactionDTO transactionDTO;
    Transaction transaction;
    BankAccount bankAccount;

    @BeforeEach
    void setup() {
        this.transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionReference(1L);
        transactionDTO.setIban("NL64ABNA6291988119"); //this is a random valid IBAN
        transactionDTO.setStartBalance("10");
        transactionDTO.setMutation("5.5");
        transactionDTO.setEndBalance("15.5");
        transactionDTO.setDescription("description of transaction");

        this.transaction = new Transaction();
        transaction.setId(1L);
        transaction.setBankAccount(new BankAccount("NL64ABNA6291988119", new BigDecimal(10)));
        transaction.setStartBalance(new BigDecimal(10));
        transaction.setMutation(new BigDecimal("5.5"));
        transaction.setEndBalance(new BigDecimal("15.5"));
        transaction.setDescription("description of transaction");

        bankAccount = new BankAccount("NL64ABNA6291988119", new BigDecimal(10));
    }

    @Test
    void saveStatement_happyFlow_savesTransaction() {
        //arrange
        when(transactionRepositoryMock.findById(transactionDTO.getTransactionReference())).thenReturn(Optional.empty());
        when(bankAccountRepositoryMock.findById(transactionDTO.getIban())).thenReturn(Optional.of(bankAccount));
        when(transactionMapperMock.toTransaction(transactionDTO)).thenReturn(transaction);

        //act
        transactionService.processStatement(transactionDTO);

        //assert
        verify(transactionRepositoryMock).save(transaction);
    }

    @Test
    void saveStatement_duplicateTransactionReference_throwsException() {
        //arrange
        when(transactionRepositoryMock.findById(any())).thenReturn(Optional.of(transaction));

        //assert
        assertThrows(EntityExistsException.class, () -> transactionService.processStatement(transactionDTO));
    }

    @Test
    void saveStatement_newBankAccount_createsNewBankAccount() {
        //arrange
        BankAccount bankAccount = new BankAccount(transactionDTO.getIban(), new BigDecimal(transactionDTO.getStartBalance()));
        when(transactionRepositoryMock.findById(transactionDTO.getTransactionReference())).thenReturn(Optional.empty());
        when(bankAccountRepositoryMock.findById(transactionDTO.getIban())).thenReturn(Optional.empty());
        when(bankAccountRepositoryMock.save(bankAccount)).thenReturn(bankAccount);
        when(transactionMapperMock.toTransaction(transactionDTO)).thenReturn(transaction);

        //act
        transactionService.processStatement(transactionDTO);

        //assert
        verify(bankAccountRepositoryMock).save(bankAccount);
    }

    @Test
    void saveStatement_existingBankAccount_getsExistingBankAccount() {
        //arrange
        when(transactionRepositoryMock.findById(transactionDTO.getTransactionReference())).thenReturn(Optional.empty());
        when(bankAccountRepositoryMock.findById(transactionDTO.getIban())).thenReturn(Optional.of(bankAccount));
        when(transactionMapperMock.toTransaction(transactionDTO)).thenReturn(transaction);

        //act
        transactionService.processStatement(transactionDTO);

        //assert
        verify(bankAccountRepositoryMock, times(0)).save(bankAccount);
    }

    @Test
    void saveStatement_balanceMisMatch_throwsException() {
        //arrange
        bankAccount.setBalance(new BigDecimal(0));
        when(transactionRepositoryMock.findById(transactionDTO.getTransactionReference())).thenReturn(Optional.empty());
        when(bankAccountRepositoryMock.findById(transactionDTO.getIban())).thenReturn(Optional.of(bankAccount));

        //assert
        assertThrows(TransactionStatementMismatchException.class, () -> transactionService.processStatement(transactionDTO));
    }

    @Test
    void saveStatement_invalidMutation_throwsException() {
        //arrange
        transaction.setMutation(new BigDecimal(100));
        when(transactionRepositoryMock.findById(transactionDTO.getTransactionReference())).thenReturn(Optional.empty());
        when(bankAccountRepositoryMock.findById(transactionDTO.getIban())).thenReturn(Optional.of(bankAccount));
        when(transactionMapperMock.toTransaction(transactionDTO)).thenReturn(transaction);

        //assert
        assertThrows(TransactionStatementMismatchException.class, () -> transactionService.processStatement(transactionDTO));
    }


}