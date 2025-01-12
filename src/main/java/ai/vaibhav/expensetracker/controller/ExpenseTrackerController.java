package ai.vaibhav.expensetracker.controller;

import ai.vaibhav.expensetracker.dto.InvoiceDto;
import ai.vaibhav.expensetracker.dto.PurchaseProjection;
import ai.vaibhav.expensetracker.dto.Summary;
import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.entity.InvoiceItem;
import ai.vaibhav.expensetracker.service.ExpenseTrackerService;
import ai.vaibhav.expensetracker.service.GooglePalmService;
import ai.vaibhav.expensetracker.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ExpenseTrackerController {

    private final ExpenseTrackerService expenseTrackerService;
    private final GooglePalmService googlePalmService;
    private final PurchaseService purchaseService;

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("index");
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        return new ModelAndView("dashboard");
    }

    @GetMapping("/invoices")
    public ModelAndView invoices() {
        return new ModelAndView("invoice");
    }

    @GetMapping("/invoice-details")
    public ModelAndView invoiceDetails() {
        return new ModelAndView("invoice-details");
    }

    @PostMapping("/upload-invoices")
    public ResponseEntity<Map<String, String>> uploadInvoices(@RequestParam("file") List<MultipartFile> multipartFileList) {
        Set<Long> uploadedIds = expenseTrackerService.uploadInvoices(multipartFileList);
        int totalFiles = multipartFileList.size();
        int uploadedFiles = uploadedIds.size();

        if (uploadedFiles == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload invoices."));
        }

        String message = uploadedFiles == totalFiles
                ? uploadedFiles + " invoices uploaded successfully."
                : uploadedFiles + " invoices uploaded successfully. Failed to upload " + (totalFiles - uploadedFiles) + " invoices.";

        return ResponseEntity.ok(Map.of("result", message));
    }

    @PostMapping(value = "/read-invoice", produces = "application/json")
    public ResponseEntity<Map<String, String>> readInvoice(@RequestParam("file") MultipartFile multipartFile) {
        String response = googlePalmService.readInvoice(multipartFile);

        if (response == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unable to read the invoice."));
        }

        return ResponseEntity.ok(Map.of("response", response));
    }

    @GetMapping(value = "/get-all-invoices", produces = "application/json")
    public ResponseEntity<List<InvoiceDto>> getAllInvoices(@RequestParam(required = false) String range, @RequestParam(required = false) String status) {
        List<InvoiceDto> invoices = expenseTrackerService.getAllInvoice(range, status);

        if (invoices == null || invoices.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(invoices);
    }

    @GetMapping(value = "/get-summary", produces = "application/json")
    public ResponseEntity<Summary> getSummary(@RequestBody(required = false) Summary summaryRequest) {
        Summary summary = expenseTrackerService.getSummary(summaryRequest);

        if (summary == null) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(summary);
    }

    @GetMapping(value = "/get-invoice/{id}", produces = "application/json")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long id) {
        return expenseTrackerService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @PostMapping(value = "/reprocess-invoice/{id}", produces = "application/json")
    public ResponseEntity<Invoice> reprocessInvoice(@PathVariable Long id) {
        return expenseTrackerService.reprocessInvoiceId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @PutMapping(value = "/update-invoice-item/{id}", produces = "application/json")
    public ResponseEntity<InvoiceItem> updateInvoice(@PathVariable Long id, @RequestBody Map<String, String> updateRequest) {
        return expenseTrackerService.updateInvoiceItem(id, updateRequest)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(null));
    }

    @DeleteMapping(value = "/delete-invoice/{id}", produces = "application/json")
    public ResponseEntity<Map<String, String>> deleteInvoice(@PathVariable Long id) {
        expenseTrackerService.deleteInvoice(id);
        return ResponseEntity.ok(Map.of("message", "Invoice deleted successfully."));
    }

    @GetMapping("/find-all-purchases")
    public ResponseEntity<List<PurchaseProjection>> getLastPurchases(
            @RequestParam String itemName) {
        List<PurchaseProjection> purchases = purchaseService.getLastPurchases(itemName);
        return ResponseEntity.ok(purchases);
    }
}
