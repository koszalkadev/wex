package org.test.wex.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Slf4j
public class TransactionUtil {
    private TransactionUtil(){}
    public static BigDecimal convertAndRoundRetrieve(BigDecimal originalAmount,
                                                     Double exchangeRate){
        log.debug("Class=TransactionUtil Method=convertAndRoundRetrieve originalAmount={} exchangeRate={}",
                originalAmount,exchangeRate);
        return BigDecimal.valueOf(originalAmount.doubleValue() * exchangeRate)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
