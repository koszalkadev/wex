package org.test.wex.unit.service;

import jakarta.ws.rs.NotFoundException;
import org.test.wex.client.ReportingRatesExchangeClient;
import org.test.wex.dto.ReportingRatesExchangeDTO;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.dto.TransactionRetrieveResponseDTO;
import org.test.wex.exception.TransactionNotFoundException;
import org.test.wex.domain.Transaction;
import org.test.wex.domain.TransactionRetrieveHistory;
import org.test.wex.repository.TransactionRepository;
import org.test.wex.repository.TransactionRetrieveHistoryRepository;
import org.test.wex.service.TransactionServiceImpl;
import org.test.wex.util.TransactionUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionRetrieveHistoryRepository historyRepository;

    @Mock
    private ReportingRatesExchangeClient reportingRatesExchangeClient;


    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void persistPurchaseTransactionTest() throws Exception {
        TransactionRequestDTO inputDto = new TransactionRequestDTO(
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now()
        );
        Transaction mockedSavedTransaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );

        when(repository.save(any(Transaction.class))).thenReturn(mockedSavedTransaction);

        TransactionResponseDTO resultDto = transactionService.persistPurchaseTransaction(inputDto);

        verify(repository, times(1)).save(any(Transaction.class));
        assertEquals(mockedSavedTransaction.getId().toString(), resultDto.id().toString());
        assertEquals(mockedSavedTransaction.getDescription(), resultDto.description());
        assertEquals(mockedSavedTransaction.getTransactionDate(), resultDto.transactionDate());
        assertEquals(mockedSavedTransaction.getTransactionTimestamp(), resultDto.transactionTimestamp());
    }

    @Test
    void persistPurchaseTransactionExceptionTest() {
        TransactionRequestDTO inputDto = new TransactionRequestDTO(
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now()
        );
        Transaction mockedSavedTransaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );

        when(repository.save(any(Transaction.class))).thenThrow(new IllegalArgumentException("Database error"));

        try {
            TransactionResponseDTO resultDto = transactionService.persistPurchaseTransaction(inputDto);
        }catch (Exception ex){
            assertEquals(ex.getMessage(), "Database error");
        }


        verify(repository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getPurchaseTransactionByIdTest() {
        UUID transactionId = UUID.randomUUID();
        Transaction mockedTransaction = new Transaction(
                transactionId,
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );

        when(repository.findById(transactionId)).thenReturn(Optional.of(mockedTransaction));

        TransactionResponseDTO resultDto = transactionService.getPurchaseTransactionById(transactionId.toString());

        verify(repository, times(1)).findById(transactionId);
        assertEquals(mockedTransaction.getId().toString(), resultDto.id().toString());
        assertEquals(mockedTransaction.getDescription(), resultDto.description());
        assertEquals(mockedTransaction.getTransactionDate(), resultDto.transactionDate());
        assertEquals(mockedTransaction.getTransactionTimestamp(), resultDto.transactionTimestamp());
    }

    @Test
    void getPurchaseTransactionByIdNotFoundTest() {
        UUID transactionId = UUID.randomUUID();

        when(repository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.getPurchaseTransactionById(transactionId.toString()));

        verify(repository, times(1)).findById(transactionId);
    }

    @Test
    void testRetrievePurchaseTransaction() throws Exception {
        // Arrange
        UUID uuidTransactionId = UUID.randomUUID();
        String transactionId = uuidTransactionId.toString();
        Double exchangeRate = 5.634;
        BigDecimal originalAmount = BigDecimal.valueOf(21.25);
        BigDecimal convertedAmount = TransactionUtil.convertAndRoundRetrieve(originalAmount, exchangeRate);
        LocalDate transactionDate = LocalDate.now();
        String currency = "Real";
        String country = "Brazil";

        Transaction mockedTransaction = new Transaction(
                uuidTransactionId,
                originalAmount,
                "Test description",
                transactionDate,
                LocalDateTime.now(),
                null
        );

        // Mock dependencies
        TransactionResponseDTO purchaseTransactionDTO = new TransactionResponseDTO(
                transactionId,
                BigDecimal.valueOf(21.25),
                "Test description",
                transactionDate,
                LocalDateTime.now()
        );

        TransactionRetrieveHistory historyModel = new TransactionRetrieveHistory(
                1L, "Real", exchangeRate, convertedAmount, LocalDateTime.now(), new Transaction()
        );

        ReportingRatesExchangeDTO exchangeRates = new ReportingRatesExchangeDTO(LocalDate.now(), "Brazil", "Real", exchangeRate);

        when(repository.findById(uuidTransactionId)).thenReturn(Optional.of(mockedTransaction));
        when(historyRepository.save(any(TransactionRetrieveHistory.class))).thenReturn(historyModel);
        when(reportingRatesExchangeClient.getExchangeRates(currency, purchaseTransactionDTO.transactionDate(), country))
                .thenReturn(exchangeRates);

        TransactionRetrieveResponseDTO responseDTO = transactionService.retrievePurchaseTransaction(transactionId, currency, country);

        assertNotNull(responseDTO);
        assertEquals(responseDTO.transactionId(), transactionId);
        assertEquals(responseDTO.exchangeRate(), exchangeRate);
        assertEquals(responseDTO.originalAmount(), originalAmount);
        assertEquals(responseDTO.convertedAmount(), convertedAmount);
        assertEquals(responseDTO.transactionDate(), transactionDate);
    }
}
