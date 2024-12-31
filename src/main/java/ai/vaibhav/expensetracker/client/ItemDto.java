package ai.vaibhav.expensetracker.client;

import lombok.Data;

@Data
public class ItemDto{
    public String itemName;

    private String itemCommonName;

    public double itemQuantity;
    public double itemAmount;
    public double itemPrice;
    public String itemWeight;
}

