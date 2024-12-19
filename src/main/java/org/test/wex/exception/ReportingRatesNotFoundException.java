package org.test.wex.exception;

import static org.test.wex.constants.ErrorMessages.DEFAULT_NO_REPORTING_RATES_ERROR;

public class ReportingRatesNotFoundException extends RuntimeException {
    public ReportingRatesNotFoundException(){
        super(DEFAULT_NO_REPORTING_RATES_ERROR);
    }
}
