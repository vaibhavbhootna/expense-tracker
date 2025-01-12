package ai.vaibhav.expensetracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceDto {

    private Long id;
    private LocalDateTime invoiceUploadDate;
    private LocalDateTime invoiceDate;
    private Double totalAmount;
    private String storeName;

}
