package ai.vaibhav.expensetracker.controller;

import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.service.ExpenseTrackerService;
import ai.vaibhav.expensetracker.service.GooglePalmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ExpenseTrackerController {

    private final ExpenseTrackerService expenseTrackerService;
    private final GooglePalmService googlePalmService;

    @RequestMapping("/")
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping("/invoices")
    public ModelAndView invoices(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("invoice");
        return modelAndView;
    }
    @PostMapping("/upload-invoices")
    public ResponseEntity uploadInvoices(@RequestParam("file") List<MultipartFile> multipartFileList){
        Set<Long> longs = expenseTrackerService.uploadInvoices(multipartFileList);
        if(longs.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload invoice"));
        }
        if(longs.size() == multipartFileList.size()) {
            return ResponseEntity.ok(
                    Map.of("result", String.format("%s invoice uploaded successfully", longs.size())));
        }else {
            return ResponseEntity.ok(
                    Map.of("result", String.format("%s invoice uploaded successfully. Failed to upload %s invoices",
                    longs.size(),multipartFileList.size() - longs.size() )));
        }
    }


    @PostMapping(value = "/read-invoice", produces = "application/json")
    public ResponseEntity readInvoice(@RequestParam("file") MultipartFile multipartFile){
        String response = googlePalmService.readInvoice(multipartFile);
        if(response == null){
            return ResponseEntity.badRequest().body(Map.of("error", "Unable to read image"));
        }else{
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping(value = "/get-all-invoices", produces = "application/json")
    public ResponseEntity getAllInvoice(String range, String status){
        List<Invoice> response = expenseTrackerService.getAllInvoice(range, status);
        if(response == null){
            return ResponseEntity.badRequest().body(Map.of("error", "Unable to read image"));
        }else{
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping(value = "/get-invoice/{id}", produces = "application/json")
    public ResponseEntity getInvoice(@PathVariable(name = "id") Long invoiceId){
        Optional<Invoice> response = expenseTrackerService.getInvoiceById(invoiceId);
        if(response.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error", "Unable to find invoice id " + invoiceId));
        }else{
            return ResponseEntity.ok(response.get());
        }
    }

    @PostMapping(value = "/reprocess-invoice/{id}", produces = "application/json")
    public ResponseEntity reprocessInvoiceId(@PathVariable(name = "id") Long invoiceId){
        Optional<Invoice> response = expenseTrackerService.reprocessInvoiceId(invoiceId);
        if(response.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error", "Unable to find invoice id " + invoiceId));
        }else{
            return ResponseEntity.ok(response.get());
        }
    }

}
