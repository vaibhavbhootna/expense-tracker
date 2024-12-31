package ai.vaibhav.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "INVOICE_DETAILS")
public class InvoiceDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "invoiceDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<InvoiceItem> items;

    @JsonIgnore
    @OneToOne
    private Invoice invoice;
    public Double totalAmount;
    public String storeType;
    public String storeAddress;
    public String paymentMode;
    public String invoiceNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDateTime invoiceDateTime;
    public Double rounding;
    public String storeName;
    public String currency;
    public Double subTotal;
    public String storePhoneNumber;
}
