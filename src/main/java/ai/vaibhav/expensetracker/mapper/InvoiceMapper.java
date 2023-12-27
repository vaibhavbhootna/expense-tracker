package ai.vaibhav.expensetracker.mapper;

import ai.vaibhav.expensetracker.client.InvoiceDetailsDto;
import ai.vaibhav.expensetracker.client.ItemDto;
import ai.vaibhav.expensetracker.entity.InvoiceDetails;
import ai.vaibhav.expensetracker.entity.InvoiceItem;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    InvoiceDetails toEntity (InvoiceDetailsDto source);
    InvoiceItem toEntity (ItemDto source);

    default LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        if(dateTimeString != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try {
                return LocalDateTime.parse(dateTimeString, formatter);
            }catch (Exception exception){
                return null;
            }
        }
        return null;
    }
}
