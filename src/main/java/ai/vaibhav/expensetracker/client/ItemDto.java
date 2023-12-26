package ai.vaibhav.expensetracker.client;

import lombok.Data;

@Data
public class ItemDto{
    public String itemName;
    public int itemQuantity;
    public double itemSubTotal;
    public double itemPrice;
}

