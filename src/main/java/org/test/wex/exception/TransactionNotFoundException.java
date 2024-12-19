package org.test.wex.exception;

import static org.test.wex.constants.ErrorMessages.DEFAULT_TRANSACTION_NOT_FOUND_ERROR;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(){
        super(DEFAULT_TRANSACTION_NOT_FOUND_ERROR);
    }
}

