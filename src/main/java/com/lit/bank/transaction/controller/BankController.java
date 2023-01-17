package com.lit.bank.transaction.controller;

import com.lit.bank.transaction.dto.TransactionDTO;
import com.lit.bank.transaction.model.Transaction;
import com.lit.bank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class BankController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Transaction> save(@Valid @RequestBody TransactionDTO transaction) {
        Transaction tx = transactionService.processStatement(transaction);
        return new ResponseEntity<>(tx, HttpStatus.CREATED);
    }
}
