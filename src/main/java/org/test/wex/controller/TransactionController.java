package org.test.wex.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.dto.TransactionRetrieveResponseDTO;
import org.test.wex.service.TransactionService;

import static org.test.wex.constants.ErrorMessages.RETRIEVE_CURRENCY_NOT_NULL_MESSAGE;
import static org.test.wex.constants.ErrorMessages.TRANSACTION_ID_NOT_NULL_MESSAGE;

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
        log.debug(
                "Class=TransactionController Method=purchaseTransaction amount={} description=\"{}\" transactionDate={}",
                transactionRequestDTO.amount(), transactionRequestDTO.description(), transactionRequestDTO.transactionDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(service.persistPurchaseTransaction(transactionRequestDTO));
    }

    @GetMapping("/purchase/{id}")
    public ResponseEntity<TransactionResponseDTO> getPurchaseTransactionById(@PathVariable String id) throws Exception {
        log.debug("Class=TransactionController Method=getPurchaseTransactionById id={}", id);
        return ResponseEntity.status(HttpStatus.OK).body(service.getPurchaseTransactionById(id));
    }

    @GetMapping("/retrieve")
    public ResponseEntity<TransactionRetrieveResponseDTO> retrievePurchaseTransaction(
            @Valid
            @RequestParam @NotNull(message = TRANSACTION_ID_NOT_NULL_MESSAGE) String id,
            @RequestParam @NotNull(message = RETRIEVE_CURRENCY_NOT_NULL_MESSAGE) String currency,
            @RequestParam(required = false) String country
    ) throws Exception
    {
        log.debug("Class=TransactionController Method=retrievePurchaseTransaction id={} currency={} country={}", id, currency, country);
        return ResponseEntity.status(HttpStatus.OK).body(service.retrievePurchaseTransaction(id,currency,country));
    }

}
