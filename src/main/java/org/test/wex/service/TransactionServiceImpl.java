package org.test.wex.service;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.wex.client.ReportingRatesExchangeClient;
import org.test.wex.domain.Transaction;
import org.test.wex.domain.TransactionRetrieveHistory;
import org.test.wex.dto.ReportingRatesExchangeDTO;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.dto.TransactionRetrieveResponseDTO;
import org.test.wex.mapper.TransactionMapper;
import org.test.wex.repository.TransactionRepository;
import org.test.wex.repository.TransactionRetrieveHistoryRepository;
import org.test.wex.util.TransactionUtil;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionRetrieveHistoryRepository historyRepository;
    private final ReportingRatesExchangeClient reportingRatesExchangeClient;

    @Autowired
    public TransactionServiceImpl(TransactionRepository repository,
                                  TransactionRetrieveHistoryRepository historyRepository,
                                  ReportingRatesExchangeClient reportingRatesExchangeClient) {
        this.transactionRepository = repository;
        this.historyRepository = historyRepository;
        this.reportingRatesExchangeClient = reportingRatesExchangeClient;
    }

    @Override
    public TransactionResponseDTO persistPurchaseTransaction(TransactionRequestDTO dto) throws Exception {
        log.debug(
            "Class=TransactionServiceImpl Method=persistPurchaseTransaction amount={} description=\"{}\" transactionDate={}",
            dto.amount(), dto.description(), dto.transactionDate()
        );

        Transaction transaction = transactionRepository.save(TransactionMapper.toModel(dto));

        return TransactionMapper.toResponseDto(transaction);
    }

    @Override
    public TransactionResponseDTO getPurchaseTransactionById(String transactionId) {
        log.debug("Class=TransactionServiceImpl Method=purchaseTransaction id={}", transactionId);
        Transaction transaction = transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(NotFoundException::new);
        return TransactionMapper.toResponseDto(transaction);
    }

    @Override
    public TransactionRetrieveResponseDTO retrievePurchaseTransaction(String id, String currency, String country) throws Exception {
        log.debug("Class=TransactionServiceImpl Method=retrievePurchaseTransaction id={}", id);
        TransactionResponseDTO purchaseTransactionDTO = getPurchaseTransactionById(id);

        ReportingRatesExchangeDTO exchangeRates =
                reportingRatesExchangeClient.getExchangeRates(currency, purchaseTransactionDTO.transactionDate(), country);

        Transaction purchaseTransaction = TransactionMapper.toModel(purchaseTransactionDTO);
        BigDecimal convertedAmount = TransactionUtil.convertAndRoundRetrieve(
                purchaseTransaction.getAmount(),
                exchangeRates.exchangeRate()
        );
        TransactionRetrieveHistory retrieveHistory =
                new TransactionRetrieveHistory(purchaseTransaction, currency, exchangeRates.exchangeRate(), convertedAmount);
        historyRepository.save(retrieveHistory);
        return TransactionRetrieveResponseDTO.buildResponseDTO(purchaseTransactionDTO, exchangeRates.exchangeRate(), convertedAmount);
    }

}
