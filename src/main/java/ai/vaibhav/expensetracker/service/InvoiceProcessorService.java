package ai.vaibhav.expensetracker.service;

import ai.vaibhav.expensetracker.client.InvoiceDetailsDto;
import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.entity.InvoiceDetails;
import ai.vaibhav.expensetracker.entity.InvoiceStatus;
import ai.vaibhav.expensetracker.mapper.InvoiceMapper;
import ai.vaibhav.expensetracker.repository.InvoiceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceProcessorService {


    private final ObjectMapper objectMapper;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceRepository invoiceRepository;
    private final GooglePalmService googlePalmService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void processInvoices(){
        List<Invoice> pendingInvoices = invoiceRepository.findByOcrStatus("PENDING");
        pendingInvoices.forEach(this::processInvoice);
    }

    @Transactional
    @Async
    public Invoice processInvoice(Invoice invoice) {
        String response = googlePalmService.readInvoice(invoice.getInvoiceImage().getFileData());
        InvoiceDetailsDto invoiceDetailsDto;
        invoice.setOcrApi("GOOGLE_PALM");
        invoice.setOcrRawResponse(response);
        try {
            invoiceDetailsDto = objectMapper.readValue(response, InvoiceDetailsDto.class);
            if(invoiceDetailsDto != null && invoiceDetailsDto.getItems() != null ) {
                InvoiceDetails invoiceDetails = invoiceMapper.toEntity(invoiceDetailsDto);
                invoiceDetails.setInvoice(invoice);
                invoice.setInvoiceDetails(invoiceDetails);
                invoice.setOcrStatus("SUCCESS");
                invoice.setStatus(InvoiceStatus.PROCESSED);
                CollectionUtils.emptyIfNull(invoiceDetails.getItems()).forEach(i -> i.setInvoiceDetails(invoiceDetails));
            }else{
                invoice.setStatus(InvoiceStatus.PROCESSED);
                invoice.setOcrStatus("NOT_SATISFIED");
            }
        } catch (JsonProcessingException e) {
            log.error("Unable to read invoice.");
            if(invoice.getRetry() > 3) {
                invoice.setOcrStatus("FAILED");
            }else{
                invoice.setRetry(invoice.getRetry() + 1);
            }
        }
        return invoiceRepository.saveAndFlush(invoice);
    }
}
