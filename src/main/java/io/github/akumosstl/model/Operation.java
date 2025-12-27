package io.github.akumosstl.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Operation {
    @SerializedName("operation")
    private String type;
    
    @SerializedName("unit-cost")
    private BigDecimal unitCost;
    
    private int quantity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
