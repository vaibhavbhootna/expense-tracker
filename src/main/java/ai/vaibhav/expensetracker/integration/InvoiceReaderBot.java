package ai.vaibhav.expensetracker.integration;

import ai.vaibhav.expensetracker.entity.Image;
import ai.vaibhav.expensetracker.entity.Invoice;
import ai.vaibhav.expensetracker.service.ExpenseTrackerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Component
public class InvoiceReaderBot extends TelegramLongPollingBot {

    @Autowired
    private ExpenseTrackerService expenseTrackerService;
    public InvoiceReaderBot() {
        super("6589480967:AAEAOzCHqL7tbXEABtK7NFuA65hVF24fS5Q");
    }
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Message Received. Processing");
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            // Handle photo message
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize largestPhoto = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);
            if (largestPhoto != null) {
                File imageFile = downloadPhotoByFileId(largestPhoto.getFileId());
                byte[] fileContent;
                try {
                    fileContent = Files.readAllBytes(imageFile.toPath());
                    Image image = new Image(null,
                            Base64.getEncoder().encodeToString(fileContent), imageFile.getName(), imageFile.length(),
                            FilenameUtils.getExtension(imageFile.getName()), null);
                    String senderName = update.getMessage().getChat().getFirstName()
                            + " " + update.getMessage().getChat().getLastName();
                    Invoice invoice = expenseTrackerService.saveInvoice(image, senderName, "TELEGRAM");
                    sendReply(update.getMessage(), "Invoice saved. Status " + invoice);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            sendReply(update.getMessage(), "I can only handle photo messages.");
        }
    }

    private File downloadPhotoByFileId(String fileId) {
        try {
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);
            String filePath = execute(getFile).getFilePath();
            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error downloading photo.", e);
        }
    }

    private void sendReply(Message message, String text) {
        SendMessage reply = new SendMessage();
        reply.setChatId(message.getChatId());
        reply.setText(text);
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error sending reply.", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "invoice_keeper_bot";
    }

}
