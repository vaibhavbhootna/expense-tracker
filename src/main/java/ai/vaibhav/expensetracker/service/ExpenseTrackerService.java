package ai.vaibhav.expensetracker.service;

import ai.vaibhav.expensetracker.dto.Summary;
import ai.vaibhav.expensetracker.entity.Image;
import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.entity.InvoiceDetails;
import ai.vaibhav.expensetracker.entity.InvoiceStatus;
import ai.vaibhav.expensetracker.repository.ImageRepository;
import ai.vaibhav.expensetracker.repository.InvoiceDetailsRepository;
import ai.vaibhav.expensetracker.repository.InvoiceItemRepository;
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
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceProcessorService invoiceProcessorService;
    private final ImageRepository imageRepository;

    @Transactional
    public Set<Long> uploadInvoices(List<MultipartFile> multipartFileList) {
        List<Invoice> invoices = new ArrayList<>();
        for (MultipartFile file : multipartFileList) {
            try {
                if(!imageRepository.existsDistinctByFileName(file.getOriginalFilename())) {
                    log.info("File {}: Size {}", file.getOriginalFilename(), file.getSize());
                    Image image = new Image(null,
                            Base64.getEncoder().encodeToString(file.getBytes()), file.getOriginalFilename(), file.getSize(), file.getContentType(), null);
                    Invoice invoice = saveInvoice(image, "ADMIN", "WEB");
                    invoices.add(invoice);
                }
            } catch (IOException e) {
                log.error("Fail to save image {}", file.getName(), e);
            } catch (Exception sqlException) {
                log.error("Fail to save image {}.", file.getName(), sqlException);
            }
        }
        return invoices.stream().map(Invoice::getId).collect(Collectors.toSet());
    }

    @Transactional
    public Invoice saveInvoice(Image image, String name, String source) {
        Invoice invoice = Invoice.builder()
                .invoiceUploadDate(LocalDateTime.now())
                .uploadType("QUICK_UPLOAD")
                .uploadedBy(name)
                .uploadSource(source)
                .status(InvoiceStatus.UPLOADED)
                .ocrStatus("PENDING")
                .retry(0)
                .build();
        invoice.setInvoiceImage(image);
        image.setInvoice(invoice);
        invoice = invoiceRepository.saveAndFlush(invoice);
        return invoice;
    }

    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoice(String criteria, String status) {
        InvoiceStatus invoiceStatus;
        LocalDateTime startDate;
        if ("today".equalsIgnoreCase(criteria)) {
            startDate = LocalDate.now().atStartOfDay();
        } else if ("thisWeek".equalsIgnoreCase(criteria)) {
            startDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        } else if ("thisMonth".equalsIgnoreCase(criteria)) {
            startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        } else {
            return invoiceRepository.findAll().stream().filter(i -> i.getInvoiceDetails() != null).collect(Collectors.toList());
        }
        return invoiceRepository.findInvoices(startDate);
    }

    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }


    @Transactional
    public Optional<Invoice> reprocessInvoiceId(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        clearExistingResults(invoice);
        return invoice;
    }

    @Transactional
    public Optional<Invoice> clearExistingResults(Optional<Invoice> invoice) {
        if (invoice.isPresent()) {
            invoice.get().setInvoiceDetails(null);
            invoice.get().setOcrStatus("PENDING");
            invoice.get().setStatus(InvoiceStatus.UPLOADED);
            return Optional.of(invoiceRepository.saveAndFlush(invoice.get()));
        }
        return Optional.empty();
    }

    public void deleteInvoice(Long invoiceId) {
        invoiceRepository.deleteById(invoiceId);
    }

    public Summary getSummary(Summary summary) {
        LocalDate date;
        Summary response = new Summary();
        if (summary == null) {
            date = LocalDate.now();
        } else {
            date = LocalDate.of(summary.getYear(), summary.getMonth(), 1);
        }
        response.setMonth(date.getMonth().getValue());
        response.setYear(date.getYear());
        response.setTotalExpense(invoiceDetailsRepository.getTotalExpenseByMonthAndYear(date.getMonth().getValue(), date.getYear()));
        List<Map<String, Object>> topFrequentlyItemByMonthAndYear = invoiceItemRepository.findTopFrequentlyItemByMonthAndYear(date.getMonth().getValue(), date.getYear());

        Map<String, Integer> itemFrequencyMap = topFrequentlyItemByMonthAndYear.stream()
                .collect(Collectors.toMap(
                        map -> ((String) map.get("item_name")).toUpperCase(),  // Key: item_name
                        map -> ((Number) map.get("frequency")).intValue()  // Value: frequency as Integer
                ));

        List<Map<String, Object>> topPriceItemsByMonthAndYear = invoiceItemRepository.findTopPriceItemsByMonthAndYear(date.getMonth().getValue(), date.getYear());

        Map<String, Integer> topPriceItemMap = topPriceItemsByMonthAndYear.stream()
                .collect(Collectors.toMap(
                        map -> ((String) map.get("item_name")).toUpperCase(),  // Key: item_name
                        map -> ((Number) map.get("item_amount")).intValue()  // Value: frequency as Integer
                ));

        response.setFrequentlyBought(itemFrequencyMap);
        response.setHighPricedItem(topPriceItemMap);
        return response;
    }

    public Optional<Invoice> updateInvoice(Long invoiceId, Map<String, String> request) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        invoice.ifPresent(value -> value.getInvoiceDetails().setInvoiceDateTime(LocalDate.parse(request.get("invoiceDateTime")).atStartOfDay()));
        return Optional.of(invoiceRepository.saveAndFlush(invoice.get()));
    }
}
