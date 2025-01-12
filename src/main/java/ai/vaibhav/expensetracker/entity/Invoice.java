package ai.vaibhav.expensetracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd hh::mm")
    private LocalDateTime invoiceUploadDate;

    private String uploadedBy;

    private String uploadSource = "WEB";

    private String uploadType;

    @OneToOne(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Image invoiceImage;

    @OneToOne(mappedBy = "invoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private InvoiceDetails invoiceDetails;

    private String ocrStatus;

    private String ocrApi;

    private Integer retry = 0;

    private InvoiceStatus status;

    @JsonIgnore
    private String ocrRawResponse;

}
