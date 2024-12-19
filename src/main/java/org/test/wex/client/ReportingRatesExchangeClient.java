package org.test.wex.client;

import org.springframework.stereotype.Component;
import org.test.wex.dto.ReportingRatesExchangeDTO;

import java.time.LocalDate;

@Component
public interface ReportingRatesExchangeClient {
    ReportingRatesExchangeDTO getExchangeRates(String currency, LocalDate purchaseDate, String country);
}
