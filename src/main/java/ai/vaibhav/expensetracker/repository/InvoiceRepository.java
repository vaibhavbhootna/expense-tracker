package ai.vaibhav.expensetracker.repository;

import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.entity.InvoiceStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByOcrStatus(String status, Pageable pageable);
    List<Invoice> findByInvoiceUploadDateAfterAndStatusOrderByIdDesc(LocalDateTime localDateTime, InvoiceStatus status);

    @Query("from Invoice where invoiceUploadDate>:invoiceUploadDate order by invoiceDetails.invoiceDateTime desc ")
    List<Invoice> findInvoices(LocalDateTime invoiceUploadDate);

    List<Invoice> findByInvoiceDetails_InvoiceDateTimeAfter(LocalDateTime localDateTime);


}
