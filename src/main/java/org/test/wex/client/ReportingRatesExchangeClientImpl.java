package org.test.wex.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.test.wex.dto.ReportingRatesExchangeDTO;
import org.test.wex.dto.ReportingRatesExchangeWrapper;
import org.test.wex.exception.ReportingRatesNotFoundException;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Slf4j
public class ReportingRatesExchangeClientImpl implements ReportingRatesExchangeClient {

    private final RestTemplate restTemplate;

    @Autowired
    public ReportingRatesExchangeClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${treasury.fiscal.data.rates.exchange.url}")
    private String baseUrl;

    public ReportingRatesExchangeDTO getExchangeRates(String currency, LocalDate purchaseDate, String country){
        log.debug("Class=FiscalDataRatesExchangeClient Mehtod=getExchangeRates currency={} purchaseDate={}",
                currency, purchaseDate);
        String request = buildGetRequest(
                currency, country, purchaseDate.toString(), purchaseDate.minusMonths(6).toString()
        );
        log.debug("Class=FiscalDataRatesExchangeClient Mehtod=getExchangeRates request={}", request);
        ReportingRatesExchangeWrapper reports = restTemplate.getForObject(request, ReportingRatesExchangeWrapper.class);
        if(Objects.isNull(reports) || Objects.isNull(reports.getData()) || reports.getData().isEmpty()){
            throw new ReportingRatesNotFoundException();
        }

        log.debug("Class=FiscalDataRatesExchangeClient Mehtod=getExchangeRates exchangeRate={}", reports.getData().get(0).exchangeRate());
        return reports.getData().getFirst();
    }

    private String buildGetRequest(String currency, String country, String purchaseDate, String dateMinus6Months){
        String filterCurrency = "currency:eq:".concat(currency);
        String filterFinalDate = "record_date:lte:".concat(purchaseDate);
        String filterInitDate = "record_date:gte:".concat(dateMinus6Months);
        String filter = String.join(",", filterCurrency,filterInitDate,filterFinalDate);

        if (Objects.nonNull(country)){
            String filterCountry = "country:eq:".concat(country);
            filter = String.join(",", filter,filterCountry);
        }

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("fields", "record_date,country,currency,exchange_rate")
                .queryParam("filter", filter)
                .queryParam("sort", "-record_date")
                .build()
                .toUriString();
    }
}