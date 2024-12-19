package org.test.wex.service;

import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.dto.TransactionRetrieveResponseDTO;

public interface TransactionService {

    TransactionResponseDTO persistPurchaseTransaction(TransactionRequestDTO dto) throws Exception;
    TransactionResponseDTO getPurchaseTransactionById(String transactionId) throws Exception;
    TransactionRetrieveResponseDTO retrievePurchaseTransaction(String id, String currency, String country) throws Exception;

}
