package ai.vaibhav.expensetracker.service;

import ai.vaibhav.expensetracker.entity.Image;
import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.entity.InvoiceDetails;
import ai.vaibhav.expensetracker.entity.InvoiceStatus;
import ai.vaibhav.expensetracker.repository.InvoiceDetailsRepository;
import ai.vaibhav.expensetracker.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseTrackerService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailsRepository invoiceDetailsRepository;
    private final InvoiceProcessorService invoiceProcessorService;

    @Transactional
    public Set<Long> uploadInvoices(List<MultipartFile> multipartFileList) {
        List<Invoice> invoices = new ArrayList<>();
        for(MultipartFile file : multipartFileList) {
            Image image;
            try {
                Invoice invoice = Invoice.builder()
                        .invoiceUploadDate(LocalDateTime.now())
                        .uploadType("QUICK_UPLOAD")
                        .uploadedBy("ADMIN")
                        .uploadSource("WEB")
                        .status(InvoiceStatus.UPLOADED)
                        .ocrStatus("PENDING")
                        .retry(0)
                        .build();
                image = new Image(null,
                        Base64.getEncoder().encodeToString(file.getBytes()), file.getOriginalFilename(), file.getSize(), file.getContentType(), invoice);
                invoice.setInvoiceImage(image);
                invoice = invoiceRepository.saveAndFlush(invoice);
                invoices.add(invoice);
            } catch (IOException e) {
                log.error("Fail to save image {}", file.getName(), e);
            } catch (Exception sqlException){
                log.error("Fail to save image {}. Error {}", file.getName(), sqlException);
            }
        }
        invoiceProcessorService.processInvoices();
        return invoices.stream().map(Invoice :: getId).collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoice(String criteria) {
        LocalDateTime startDate;
        if ("today".equalsIgnoreCase(criteria)) {
            startDate = LocalDate.now().atStartOfDay();
        } else if ("thisWeek".equalsIgnoreCase(criteria)) {
            startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        } else if ("thisMonth".equalsIgnoreCase(criteria)) {
            startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        } else {
            startDate = LocalDate.now().atStartOfDay();
        }
        return invoiceRepository.findByInvoiceUploadDateAfter(startDate);
    }

    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }


    @Transactional
    public Optional<Invoice> reprocessInvoiceId(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        clearExistingResults(invoice);
        invoice.ifPresent(invoiceProcessorService::processInvoice);
        return invoice;
    }

    @Transactional
    public Optional<Invoice> clearExistingResults(Optional<Invoice> invoice) {
        if(invoice.isPresent()){
            invoice.get().setInvoiceDetails(null);
            invoice.get().setOcrStatus("PENDING");
            invoice.get().setStatus(InvoiceStatus.UPLOADED);
            return Optional.of(invoiceRepository.saveAndFlush(invoice.get()));
        }
        return Optional.empty();
    }
}
