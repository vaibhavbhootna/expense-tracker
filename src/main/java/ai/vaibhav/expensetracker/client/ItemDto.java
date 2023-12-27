package ai.vaibhav.expensetracker.client;

import lombok.Data;

@Data
public class ItemDto{
    public String itemName;
    public double itemQuantity;
    public double itemAmount;
    public double itemPrice;
}

