package org.test.wex.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class TransactionResponseDTO {
        public UUID id;

        public BigDecimal amount;

        public String description;

        @JsonDeserialize(using = LocalDateDeserializer.class)
        public LocalDate transactionDate;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        public LocalDateTime transactionTimestamp;
}
