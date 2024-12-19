package org.test.wex.unit.domain;

import org.test.wex.domain.Transaction;
import org.test.wex.domain.TransactionRetrieveHistory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.test.wex.constants.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void purchaseTransactionValidationSuccessTest() {
        // Arrange
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(100.0),
                "Some description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );
        Set<?> violations = validator.validate(transaction);
        assertThat(violations).isEmpty();
    }

    @Test
    void purchaseTransactionAmountLessThanZeroFailTest() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(-10.0),
                "Some description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(TRANSACTION_AMOUNT_MIN_MESSAGE);
    }

    @Test
    void purchaseTransactionDescriptionTooLongFailTest() {
        // Arrange
        String longDescription = "Very very very very long description that exceeds the maximum length of 50 characters.";
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(100.0),
                longDescription,
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(DESCRIPTION_SIZE_MESSAGE);
    }

    @Test
    void purchaseTransactionNullAmountFailTest() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                null,
                "Some description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(TRANSACTION_AMOUNT_NOT_NULL_MESSAGE);
    }

    @Test
    void purchaseTransactionNullDescriptionFailTest() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(20.00),
                null,
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(DESCRIPTION_NOT_NULL_MESSAGE);
    }

    @Test
    void purchaseTransactionNullTransactionDateFailTest() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(20.00),
                "Some description",
                null,
                LocalDateTime.now(),
                null
        );
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(TRANSACTION_DATE_NOT_NULL_MESSAGE);
    }

    @Test
    void purchaseTransactionGetTransactionRetrieveTest() {
        TransactionRetrieveHistory historyModel = new TransactionRetrieveHistory(
                new Transaction(
                        UUID.randomUUID(),
                        BigDecimal.valueOf(20.001),
                        "Some description",
                        LocalDate.now(),
                        LocalDateTime.now(),
                        null
                ),
                "Real",
                1.5,
                BigDecimal.valueOf(100.0)
        );

        List<TransactionRetrieveHistory> history = new ArrayList<>();
        history.add(historyModel);
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(20.001),
                "Some description",
                LocalDate.now(),
                LocalDateTime.now(),
                history
        );



        List<TransactionRetrieveHistory> transactionHistory = transaction.getTransactionRetrieveHistory();
        assertEquals(transactionHistory.getFirst(), historyModel);
    }
}
