package org.test.wex.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.service.TransactionService;

@RestController
@RequestMapping("/transaction")
@Slf4j
public class TransactionController {

    private final TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/purchase")
    public ResponseEntity<TransactionResponseDTO> purchaseTransaction(
            @RequestBody @Valid TransactionRequestDTO transactionRequestDTO) throws Exception {
        log.info(
                "Class=TransactionController Method=purchaseTransaction amount={} description=\"{}\" transactionDate={}",
                transactionRequestDTO.amount, transactionRequestDTO.description, transactionRequestDTO.transactionDate
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(service.persistPurchaseTransaction(transactionRequestDTO));
    }

}
