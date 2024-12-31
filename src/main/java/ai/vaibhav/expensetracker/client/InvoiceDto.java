package ai.vaibhav.expensetracker.client;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceDto {

    public InvoiceDetailsDto invoiceDetails;
    public List<ItemDto> itemDetails;
}
