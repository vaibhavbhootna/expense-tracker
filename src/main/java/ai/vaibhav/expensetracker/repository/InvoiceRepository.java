package ai.vaibhav.expensetracker.repository;

import ai.vaibhav.expensetracker.entity.Image;
import ai.vaibhav.expensetracker.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByOcrStatus(String status);
    List<Invoice> findByInvoiceUploadDateAfter(LocalDateTime localDateTime);

    List<Invoice> findByInvoiceDetails_InvoiceDateTimeAfter(LocalDateTime localDateTime);
}
