package com.lit.bank.service;

import com.lit.bank.dto.TransactionDTO;
import com.lit.bank.exception.TransactionStatementMismatchException;
import com.lit.bank.model.BankAccount;
import com.lit.bank.model.Transaction;
import com.lit.bank.repository.BankAccountRepository;
import com.lit.bank.repository.TransactionRepository;
import com.lit.bank.util.TransactionMapper;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public Transaction processStatement(TransactionDTO transaction) {

        validateTransactionReference(transaction.getTransactionReference());

        BankAccount bankAccount = getOrCreateBankAccount(transaction);

        validateBalance(transaction, bankAccount);

        Transaction tx = transactionMapper.toTransaction(transaction);

        validateMutation(tx);

        bankAccount.setBalance(tx.getEndBalance());

        tx.setBankAccount(bankAccount);

        return saveTransaction(tx);
    }

    private void validateTransactionReference(long transactionReference) {
        //check transaction ID
        if (transactionExistsById(transactionReference)) {
            throw new EntityExistsException("Transaction with id " + transactionReference + " already exists in database!");
        }
    }

    private BankAccount getOrCreateBankAccount(TransactionDTO transaction) {
        return getBankAccount(transaction.getIban())
                .orElseGet(() -> saveNewBankAccount(transaction.getIban(), new BigDecimal(transaction.getStartBalance())));
    }

    private void validateBalance(TransactionDTO transaction, BankAccount bankAccount) {
        //check bank balance in db against start balance in transaction
        if (new BigDecimal(transaction.getStartBalance()).compareTo(bankAccount.getBalance()) != 0) {
            throw new TransactionStatementMismatchException("Start balance on transaction does not match account balance in database.");
        }
    }

    private void validateMutation(Transaction tx) {
        if (tx.getStartBalance().add(tx.getMutation()).compareTo(tx.getEndBalance()) != 0) {
            throw new TransactionStatementMismatchException("Mutation on start balance does not result in given end balance!");
        }
    }

    public Optional<BankAccount> getBankAccount(String IBAN) {
        return bankAccountRepository.findById(IBAN);
    }

    public BankAccount saveNewBankAccount(String IBAN, BigDecimal balance) {
        BankAccount bankAccount = new BankAccount(IBAN, balance);
        return bankAccountRepository.save(bankAccount);
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public boolean transactionExistsById(Long id) {
        return transactionRepository.findById(id).isPresent();
    }
}
