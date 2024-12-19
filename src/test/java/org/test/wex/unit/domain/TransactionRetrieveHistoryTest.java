package org.test.wex.unit.domain;

import org.test.wex.domain.Transaction;
import org.test.wex.domain.TransactionRetrieveHistory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.test.wex.constants.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRetrieveHistoryTest {

    private Validator validator;
    private Transaction purchaseTransaction;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Initialize any required objects or dependencies
        purchaseTransaction = new Transaction(
            UUID.randomUUID(), BigDecimal.valueOf(12.22), "Test Description", LocalDate.now(), LocalDateTime.now(), null
        );
    }

    @Test
    void testValidModel() {
        TransactionRetrieveHistory historyModel = new TransactionRetrieveHistory(
                purchaseTransaction,
                "Real",
                1.5,
                BigDecimal.valueOf(100.0)
        );

        Set<ConstraintViolation<TransactionRetrieveHistory>> violations = validator.validate(historyModel);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidExchangeRate() {
        TransactionRetrieveHistory historyModel = new TransactionRetrieveHistory(
                purchaseTransaction,
                "Real",
                null,  // Invalid: Exchange rate is null
                BigDecimal.valueOf(100.0)
        );

        Set<ConstraintViolation<TransactionRetrieveHistory>> violations = validator.validate(historyModel);
        assertEquals(1, violations.size());
        assertEquals(RETRIEVE_EXCHANGE_RATE_NOT_NULL_MESSAGE, violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidConvertedAmount() {
        TransactionRetrieveHistory historyModel = new TransactionRetrieveHistory(
                purchaseTransaction,
                "Real",
                1.5,
                null
        );

        Set<ConstraintViolation<TransactionRetrieveHistory>> violations = validator.validate(historyModel);
        assertEquals(1, violations.size());
        assertEquals(RETRIEVE_CONVERTED_AMOUNT_NOT_NULL_MESSAGE, violations.iterator().next().getMessage());
    }


    @Test
    void testInvalidCurrency() {
        TransactionRetrieveHistory historyModel = new TransactionRetrieveHistory(
                purchaseTransaction,
                null,
                1.5,
                BigDecimal.valueOf(100.0)
        );

        Set<ConstraintViolation<TransactionRetrieveHistory>> violations = validator.validate(historyModel);
        assertEquals(1, violations.size());
        assertEquals(RETRIEVE_CURRENCY_NOT_NULL_MESSAGE, violations.iterator().next().getMessage());
    }
}
