package org.test.wex.unit.client;

import org.test.wex.client.ReportingRatesExchangeClientImpl;
import org.test.wex.dto.ReportingRatesExchangeDTO;
import org.test.wex.dto.ReportingRatesExchangeWrapper;
import org.test.wex.exception.ReportingRatesNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiscalDataRatesExchangeClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReportingRatesExchangeClientImpl exchangeClient;

    @BeforeEach
    void setup(){
        ReflectionTestUtils.setField(
                exchangeClient,
                "baseUrl",
                "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange"
        );
    }


    @Test
    void testGetExchangeRatesWithoutCountry() {
        String currency = "Real";
        LocalDate purchaseDate = LocalDate.now();
        ReportingRatesExchangeWrapper mockWrapper = new ReportingRatesExchangeWrapper();
        mockWrapper.setData(Collections.singletonList(new ReportingRatesExchangeDTO(
                LocalDate.now(), "Brazil", "Real", 5.634
        )));

        when(restTemplate.getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class)))
                .thenReturn(mockWrapper);


        ReportingRatesExchangeDTO result = exchangeClient.getExchangeRates(currency, purchaseDate, null);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class));
    }

    @Test
    void testGetExchangeRatesWithCountry() {
        String currency = "Real";
        LocalDate purchaseDate = LocalDate.now();
        ReportingRatesExchangeWrapper mockWrapper = new ReportingRatesExchangeWrapper();
        mockWrapper.setData(Collections.singletonList(new ReportingRatesExchangeDTO(
                LocalDate.now(), "Brazil", "Real", 5.634
        )));

        when(restTemplate.getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class)))
                .thenReturn(mockWrapper);


        ReportingRatesExchangeDTO result = exchangeClient.getExchangeRates(currency, purchaseDate, "Brazil");

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class));
    }

    @Test
    void testGetExchangeRatesWithException() {
        String currency = "Real";
        LocalDate purchaseDate = LocalDate.now();

        when(restTemplate.getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class)))
                .thenReturn(new ReportingRatesExchangeWrapper());
        assertThrows(ReportingRatesNotFoundException.class,
                () -> exchangeClient.getExchangeRates(currency, purchaseDate, null));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class));
    }

    @Test
    void testGetExchangeRatesDataEmpty() {
        String currency = "Real";
        LocalDate purchaseDate = LocalDate.now();
        ReportingRatesExchangeWrapper reportingRatesExchangeWrapper = new ReportingRatesExchangeWrapper();
        reportingRatesExchangeWrapper.setData(new ArrayList<>());
        when(restTemplate.getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class)))
                .thenReturn(reportingRatesExchangeWrapper);
        assertThrows(ReportingRatesNotFoundException.class,
                () -> exchangeClient.getExchangeRates(currency, purchaseDate, null));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class));
    }

    @Test
    void testGetExchangeRatesNull() {
        String currency = "Real";
        LocalDate purchaseDate = LocalDate.now();

        when(restTemplate.getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class)))
                .thenReturn(null);
        assertThrows(ReportingRatesNotFoundException.class,
                () -> exchangeClient.getExchangeRates(currency, purchaseDate, null));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class));
    }

    @Test
    void testGetExchangeRatesDataNull() {
        String currency = "Real";
        LocalDate purchaseDate = LocalDate.now();
        ReportingRatesExchangeWrapper reportingRatesExchangeWrapper = new ReportingRatesExchangeWrapper();
        reportingRatesExchangeWrapper.setData(null);
        when(restTemplate.getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class)))
                .thenReturn(reportingRatesExchangeWrapper);
        assertThrows(ReportingRatesNotFoundException.class,
                () -> exchangeClient.getExchangeRates(currency, purchaseDate, null));

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ReportingRatesExchangeWrapper.class));
    }
}