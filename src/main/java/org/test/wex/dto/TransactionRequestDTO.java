package org.test.wex.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.test.wex.exception.ErrorMessages.*;

@Setter
@Getter
public class TransactionRequestDTO{

    @NotNull(message = TRANSACTION_AMOUNT_NOT_NULL_MESSAGE)
    @Min(value = 0, message = TRANSACTION_AMOUNT_MIN_MESSAGE)
    public BigDecimal amount;

    @NotNull(message = DESCRIPTION_NOT_NULL_MESSAGE)
    @Size(max = 50, message = DESCRIPTION_SIZE_MESSAGE)
    public String description;

    @NotNull(message = TRANSACTION_DATE_NOT_NULL_MESSAGE)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate transactionDate;

}