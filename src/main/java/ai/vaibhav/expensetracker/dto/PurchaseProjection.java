package ai.vaibhav.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PurchaseProjection {

    LocalDate getInvoiceDate();
    String getItemCommonName();
    BigDecimal getItemAmount();
}