package org.test.wex.service;

import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;

public interface TransactionService {

    TransactionResponseDTO persistPurchaseTransaction(TransactionRequestDTO dto) throws Exception;

}
