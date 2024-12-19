package org.test.wex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record ReportingRatesExchangeDTO(
        @JsonProperty("record_date")
        LocalDate recordDate,
        String country,
        String currency,
        @JsonProperty("exchange_rate")
        Double exchangeRate
) {
    public String toJSONString() {
        return "{"
                .concat("\"record_date\":").concat("\""+this.recordDate.toString()+"\",")
                .concat("\"country\":").concat("\""+this.country+"\",")
                .concat("\"currency\":").concat("\""+this.currency+"\",")
                .concat("\"exchange_rate\":").concat(this.exchangeRate.toString())
                .concat("}");
    }
}