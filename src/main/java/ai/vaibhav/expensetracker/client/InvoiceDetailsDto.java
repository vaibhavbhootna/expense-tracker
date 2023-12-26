package ai.vaibhav.expensetracker.client;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDetailsDto {
    public double totalAmount;
    public String storeType;
    public String storeAddress;
    public String paymentMode;
    public String invoiceNumber;
    public String invoiceDateTime;
    public double rounding;
    public String storeName;
    public String currency;
    public double subTotal;
    public String storePhoneNumber;
    public List<ItemDto> items;

}