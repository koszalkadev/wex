package org.test.wex.unit.dto;

import org.test.wex.dto.TransactionRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.test.wex.constants.ErrorMessages.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRequestDTOTest {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void PurchaseTransactionRequestDTOValidationSuccessTest() {
        // Arrange
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                BigDecimal.valueOf(100.0),
                "Some description",
                LocalDate.now()
        );
        Set<?> violations = validator.validate(transaction);
        assertThat(violations).isEmpty();
    }

    @Test
    void PurchaseTransactionRequestDTOAmountLessThanZeroFailTest() {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                BigDecimal.valueOf(-100.0),
                "Some description",
                LocalDate.now()
        );

        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(TRANSACTION_AMOUNT_MIN_MESSAGE);
    }

    @Test
    void PurchaseTransactionRequestDTODescriptionTooLongFailTest() {
        // Arrange
        String longDescription = "Very very very very long description that exceeds the maximum length of 50 characters.";
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                BigDecimal.valueOf(100.0),
                longDescription,
                LocalDate.now()
        );

        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(DESCRIPTION_SIZE_MESSAGE);
    }

    @Test
    void PurchaseTransactionRequestDTONullAmountFailTest() {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                null,
                "Some description",
                LocalDate.now()
        );
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(TRANSACTION_AMOUNT_NOT_NULL_MESSAGE);
    }

    @Test
    void PurchaseTransactionRequestDTONullDescriptionFailTest() {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                BigDecimal.valueOf(100.0),
                null,
                LocalDate.now()
        );
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(DESCRIPTION_NOT_NULL_MESSAGE);
    }

    @Test
    void PurchaseTransactionRequestDTONullTransactionDateFailTest() {
        TransactionRequestDTO transaction = new TransactionRequestDTO(
                BigDecimal.valueOf(100.0),
                "Some description",
                null
        );
        Set<ConstraintViolation<TransactionRequestDTO>> violations = validator.validate(transaction);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo(TRANSACTION_DATE_NOT_NULL_MESSAGE);
    }
}
