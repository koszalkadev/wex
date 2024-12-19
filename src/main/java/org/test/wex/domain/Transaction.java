package org.test.wex.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.test.wex.constants.ErrorMessages.*;

@Entity
@Getter
@Setter
@Table(name = "transaction")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private UUID id;

    @NotNull(message = TRANSACTION_AMOUNT_NOT_NULL_MESSAGE)
    @Min(value = 0, message = TRANSACTION_AMOUNT_MIN_MESSAGE)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull(message = DESCRIPTION_NOT_NULL_MESSAGE)
    @Size(max = 50, message = DESCRIPTION_SIZE_MESSAGE)
    private String description;

    @NotNull(message = TRANSACTION_DATE_NOT_NULL_MESSAGE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    private LocalDateTime transactionTimestamp;

    @PrePersist
    protected void onCreate() {
        this.transactionTimestamp = LocalDateTime.now();
    }
}

