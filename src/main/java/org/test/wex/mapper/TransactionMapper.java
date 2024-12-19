package org.test.wex.mapper;


import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.domain.Transaction;
import org.test.wex.util.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.UUID;

@Component
@Slf4j
public class TransactionMapper {

    private TransactionMapper() {}

    public static TransactionResponseDTO toResponseDto(Transaction model){
        log.info("Class=PurchaseTransactionMapper Method=toResponseDto id={}", model.getId());
        return new TransactionResponseDTO(
                model.getId().toString(),
                model.getAmount(),
                model.getDescription(),
                model.getTransactionDate(),
                model.getTransactionTimestamp());
    }

    public static TransactionRequestDTO toRequestDto(Transaction model){
        log.info("Class=PurchaseTransactionMapper Method=toRequestDto id={}", model.getId());
        return new TransactionRequestDTO(
                model.getAmount(),
                model.getDescription(),
                model.getTransactionDate());
    }


    public static Transaction toModel(TransactionRequestDTO dto){
        log.info(
                "Class=PurchaseTransactionMapper Method=toModel amount={} description=\"{}\"",
                dto.amount(), dto.description()
        );
        return new Transaction(
                null,
                dto.amount().setScale(2, RoundingMode.HALF_EVEN),
                dto.description(),
                dto.transactionDate(),
                null,
                null);
    }

    public static Transaction toModel(TransactionResponseDTO dto){
        log.info(
                "Class=PurchaseTransactionMapper Method=toModel amount={} description=\"{}\"",
                dto.amount(), dto.description()
        );
        return new Transaction(
                UUID.fromString(dto.id()),
                dto.amount().setScale(2, RoundingMode.HALF_EVEN),
                dto.description(),
                dto.transactionDate(),
                dto.transactionTimestamp(),
                null);
    }
}

