package io.github.akumosstl.model;

import java.math.BigDecimal;

public class TaxResult {
    private BigDecimal tax;

    public TaxResult(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
}
