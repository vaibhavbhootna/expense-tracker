package ai.vaibhav.expensetracker.repository;

import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.entity.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetails, Long> {

    @Query("SELECT COALESCE(SUM(e.totalAmount), 0) FROM InvoiceDetails e WHERE MONTH(e.invoiceDateTime) = :month AND YEAR(e.invoiceDateTime) = :year")
    BigDecimal getTotalExpenseByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
