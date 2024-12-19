package org.test.wex.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.test.wex.constants.ErrorMessages.*;

@Entity
@Table(name = "transaction_retrieve_history")
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRetrieveHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = RETRIEVE_CURRENCY_NOT_NULL_MESSAGE)
    private String currency;

    @NotNull(message = RETRIEVE_EXCHANGE_RATE_NOT_NULL_MESSAGE)
    private Double exchangeRate;

    @NotNull(message = RETRIEVE_CONVERTED_AMOUNT_NOT_NULL_MESSAGE)
    private BigDecimal convertedAmount;

    private LocalDateTime retrieveTimestamp;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction purchaseTransaction;

    public TransactionRetrieveHistory(Transaction purchaseTransaction, String currency, Double exchangeRate, BigDecimal convertedAmount) {
        this.id = null;
        this.exchangeRate = exchangeRate;
        this.currency=currency;
        this.convertedAmount = convertedAmount;
        this.retrieveTimestamp = null;
        this.purchaseTransaction = purchaseTransaction;
    }


    @PrePersist
    protected void onCreate() {
        this.retrieveTimestamp = LocalDateTime.now();
    }

}

