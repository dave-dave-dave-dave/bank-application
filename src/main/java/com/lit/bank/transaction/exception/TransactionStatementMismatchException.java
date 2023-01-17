package com.lit.bank.transaction.exception;

public class TransactionStatementMismatchException extends RuntimeException{

    public TransactionStatementMismatchException(String message) {
        super(message);
    }
}
