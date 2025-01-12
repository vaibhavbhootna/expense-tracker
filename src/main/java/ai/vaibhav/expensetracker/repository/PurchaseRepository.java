package ai.vaibhav.expensetracker.repository;

import ai.vaibhav.expensetracker.dto.PurchaseProjection;
import ai.vaibhav.expensetracker.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<InvoiceItem, Long> {

    @Query(value = "SELECT DATE(d.invoice_date_time) AS invoice_date, i.item_common_name, i.item_amount " +
            "FROM invoice_item i JOIN invoice_details d ON i.invoice_details_id = d.id " +
            "WHERE LOWER(i.item_common_name) LIKE LOWER(:itemName) " +
            "ORDER BY d.invoice_date_time DESC LIMIT 10",
            nativeQuery = true)
    List<PurchaseProjection> findLastPurchasesByItem(@Param("itemName") String itemName);


}
