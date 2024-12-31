package ai.vaibhav.expensetracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class Summary {

    private Integer month;
    private Integer year;
    private BigDecimal totalExpense;
    private Map<String, Integer> frequentlyBought;
    private Map<String, Integer> highPricedItem;
}
