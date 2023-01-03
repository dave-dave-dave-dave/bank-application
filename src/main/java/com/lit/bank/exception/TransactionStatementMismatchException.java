package com.lit.bank.exception;

public class TransactionStatementMismatchException extends RuntimeException{

    public TransactionStatementMismatchException(String message) {
        super(message);
    }
}
