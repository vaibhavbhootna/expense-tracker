package ai.vaibhav.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "INVOICE_ITEM")
@Entity
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("Name")
    public String itemName;

    @JsonProperty("Common Name")
    public String itemCommonName;

    @JsonProperty("Qty")
    public Double itemQuantity;

    @JsonProperty("Weight")
    public String itemWeight;

    @JsonProperty("Amount")
    public Double itemAmount;

    @JsonProperty("Price")
    public Double itemPrice;

    @JsonIgnore
    @ManyToOne
    private InvoiceDetails invoiceDetails;

}

