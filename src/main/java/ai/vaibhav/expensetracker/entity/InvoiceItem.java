package ai.vaibhav.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "INVOICE_ITEM")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String itemName;
    public Integer itemQuantity;
    public Double itemAmount;
    public Double itemPrice;

    @JsonIgnore
    @ManyToOne
    private InvoiceDetails invoiceDetails;

}

