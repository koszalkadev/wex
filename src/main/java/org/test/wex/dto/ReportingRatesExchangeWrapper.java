package org.test.wex.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ReportingRatesExchangeWrapper {
    private List<ReportingRatesExchangeDTO> data;
}
