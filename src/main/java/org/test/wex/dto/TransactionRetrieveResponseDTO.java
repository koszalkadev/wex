package org.test.wex.dto;

import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRetrieveResponseDTO(
        String transactionId,
        String description,
        LocalDate transactionDate,
        BigDecimal originalAmount,
        Double exchangeRate,
        @Digits(integer = 99999, fraction = 2)
        BigDecimal convertedAmount) {

    public static TransactionRetrieveResponseDTO buildResponseDTO(
            TransactionResponseDTO purchaseTransaction,
            Double exchangeRate,
            BigDecimal convertedAmount
    ){
        return new TransactionRetrieveResponseDTO(
                purchaseTransaction.id(),
                purchaseTransaction.description(),
                purchaseTransaction.transactionDate(),
                purchaseTransaction.amount(),
                exchangeRate,
                convertedAmount
        );
    }
}