package io.github.akumosstl.service;

import io.github.akumosstl.model.Operation;
import io.github.akumosstl.model.TaxResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class CapitalGainService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.20");
    private static final BigDecimal EXEMPTION_LIMIT = new BigDecimal("20000.00");

    // Semaphore to limit the number of concurrent simulations
    private final Semaphore semaphore = new Semaphore(10);

    public List<TaxResult> calculateTaxes(List<Operation> operations) {
        try {
            semaphore.acquire();
            var results = new ArrayList<TaxResult>();
            var state = new PortfolioState();

            for (Operation op : operations) {
                var tax = BigDecimal.ZERO;

                if ("buy".equals(op.getType())) {
                    buy(op, state);
                } else if ("sell".equals(op.getType())) {
                    tax = sell(op, state);
                }

                results.add(new TaxResult(tax));
            }
            return results;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simulation interrupted", e);
        } finally {
            semaphore.release();
        }
    }

    private void buy(Operation op, PortfolioState state) {
        var totalCost = op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity()));
        var currentTotalValue = state.weightedAveragePrice.multiply(BigDecimal.valueOf(state.currentQuantity));

        state.currentQuantity += op.getQuantity();
        if (state.currentQuantity > 0) {
            state.weightedAveragePrice = currentTotalValue.add(totalCost)
                    .divide(BigDecimal.valueOf(state.currentQuantity), 2, RoundingMode.HALF_UP);
        }
    }

    private BigDecimal sell(Operation op, PortfolioState state) {
        var tax = BigDecimal.ZERO;
        var totalSaleValue = op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity()));
        var profit = op.getUnitCost().subtract(state.weightedAveragePrice)
                .multiply(BigDecimal.valueOf(op.getQuantity()));

        if (profit.compareTo(BigDecimal.ZERO) < 0) {
            state.accumulatedLoss = state.accumulatedLoss.add(profit.abs());
        } else {
            // Deduct accumulated loss from profit regardless of exemption limit
            if (state.accumulatedLoss.compareTo(BigDecimal.ZERO) > 0) {
                if (state.accumulatedLoss.compareTo(profit) >= 0) {
                    state.accumulatedLoss = state.accumulatedLoss.subtract(profit);
                    profit = BigDecimal.ZERO;
                } else {
                    profit = profit.subtract(state.accumulatedLoss);
                    state.accumulatedLoss = BigDecimal.ZERO;
                }
            }

            if (totalSaleValue.compareTo(EXEMPTION_LIMIT) > 0) {
                if (profit.compareTo(BigDecimal.ZERO) > 0) {
                    tax = profit.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }
        state.currentQuantity -= op.getQuantity();
        return tax;
    }

    private static class PortfolioState {
        int currentQuantity = 0;
        BigDecimal weightedAveragePrice = BigDecimal.ZERO;
        BigDecimal accumulatedLoss = BigDecimal.ZERO;
    }
}
