package io.github.akumosstl.service;

import io.github.akumosstl.model.Operation;
import io.github.akumosstl.model.TaxResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CapitalGainServiceTest {

    private CapitalGainService service;

    @BeforeEach
    public void setUp() {
        service = new CapitalGainService();
    }

    private Operation createOperation(String type, double unitCost, int quantity) {
        Operation op = new Operation();
        op.setType(type);
        op.setUnitCost(BigDecimal.valueOf(unitCost));
        op.setQuantity(quantity);
        return op;
    }

    @Test
    public void testCase1() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 100));
        operations.add(createOperation("sell", 15.00, 50));
        operations.add(createOperation("sell", 15.00, 50));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(3, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(1).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
    }

    @Test
    public void testCase2() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("sell", 20.00, 5000));
        operations.add(createOperation("sell", 5.00, 5000));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(3, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(new BigDecimal("10000.00"), results.get(1).getTax());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
    }

    @Test
    public void testCase3() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("sell", 5.00, 5000));
        operations.add(createOperation("sell", 20.00, 3000));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(3, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(1).getTax().setScale(2));
        assertEquals(new BigDecimal("1000.00"), results.get(2).getTax());
    }
    
    @Test
    public void testCase4() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("buy", 25.00, 5000));
        operations.add(createOperation("sell", 15.00, 10000));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(3, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(1).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
    }

    @Test
    public void testCase5() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("buy", 25.00, 5000));
        operations.add(createOperation("sell", 15.00, 10000));
        operations.add(createOperation("sell", 25.00, 5000));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(4, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(1).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
        assertEquals(new BigDecimal("10000.00"), results.get(3).getTax());
    }

    @Test
    public void testCase6() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("sell", 2.00, 5000));
        operations.add(createOperation("sell", 20.00, 2000));
        operations.add(createOperation("sell", 20.00, 2000));
        operations.add(createOperation("sell", 25.00, 1000));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(5, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(1).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(3).getTax().setScale(2));
        assertEquals(new BigDecimal("3000.00"), results.get(4).getTax());
    }

    @Test
    public void testCase7() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("sell", 2.00, 5000));
        operations.add(createOperation("sell", 20.00, 2000));
        operations.add(createOperation("sell", 20.00, 2000));
        operations.add(createOperation("sell", 25.00, 1000));
        operations.add(createOperation("buy", 20.00, 10000));
        operations.add(createOperation("sell", 15.00, 5000));
        operations.add(createOperation("sell", 30.00, 4350));
        operations.add(createOperation("sell", 30.00, 650));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(9, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(1).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(3).getTax().setScale(2));
        assertEquals(new BigDecimal("3000.00"), results.get(4).getTax());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(5).getTax().setScale(2));
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(6).getTax().setScale(2));
        assertEquals(new BigDecimal("3700.00"), results.get(7).getTax());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(8).getTax().setScale(2));
    }

    @Test
    public void testCase8() {
        List<Operation> operations = new ArrayList<>();
        operations.add(createOperation("buy", 10.00, 10000));
        operations.add(createOperation("sell", 50.00, 10000));
        operations.add(createOperation("buy", 20.00, 10000));
        operations.add(createOperation("sell", 50.00, 10000));

        List<TaxResult> results = service.calculateTaxes(operations);

        assertEquals(4, results.size());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(0).getTax().setScale(2));
        assertEquals(new BigDecimal("80000.00"), results.get(1).getTax());
        assertEquals(BigDecimal.ZERO.setScale(2), results.get(2).getTax().setScale(2));
        assertEquals(new BigDecimal("60000.00"), results.get(3).getTax());
    }
}
