package ai.vaibhav.expensetracker.repository;

import ai.vaibhav.expensetracker.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem,Long> {

    @Query(value = "SELECT i.item_common_name as item_name, COUNT(i.item_common_name) AS frequency " +
            "FROM invoice_item i " +
            "JOIN invoice_details d ON i.invoice_details_id = d.id " +
            "WHERE EXTRACT(MONTH FROM d.invoice_date_time) = :month AND EXTRACT(YEAR FROM d.invoice_date_time) = :year " +
            "GROUP BY i.item_common_name " +
            "having COUNT(i.item_common_name) > 2 " +
            "ORDER BY frequency DESC " +
            "LIMIT 3", nativeQuery = true)
    List<Map<String, Object>> findTopFrequentlyItemByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT distinct i.item_common_name as item_name, i.item_amount as item_amount " +
            "FROM invoice_item i " +
            "JOIN invoice_details d ON i.invoice_details_id = d.id " +
            "WHERE EXTRACT(MONTH FROM d.invoice_date_time) = :month AND EXTRACT(YEAR FROM d.invoice_date_time) = :year and i.item_amount >= 10"
            , nativeQuery = true)
    List<Map<String, Object>> findTopPriceItemsByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query(value = "select upper(i.item_common_name) from  invoice_item i where upper(i.item_name) = upper(:itemName) LIMIT 1", nativeQuery = true)
    String findItemCommonNameByItemName(String itemName);

}
