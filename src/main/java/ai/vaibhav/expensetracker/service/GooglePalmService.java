package ai.vaibhav.expensetracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GooglePalmService {

    @Value("${google.pall.api_key}")
    private String apiKey;

    public String readInvoice(MultipartFile multipartFile){
        try {
            return this.readInvoice(Base64.getEncoder().encodeToString(multipartFile.getBytes()));
        } catch (IOException e) {
            log.error("Unable to read file", e);
            return null;
        }
    }
    public String readInvoice(String invoiceImage){
        String json = prepareRequest(invoiceImage);
        if(json != null){
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent?key=" + apiKey)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return extractResponse(response);
            } catch (IOException e) {
                log.error("Error occurred while calling Google Palm API ", e);
                return null;
            }
        }
        return null;
    }

    private static String extractResponse(Response response) throws IOException {
        if(response != null && response.body() != null) {
            String responseString = response.body().string();
            log.info("Google Palm API response {}", responseString);
            JSONObject jsonObject = new JSONObject(responseString);
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            JSONObject part = parts.getJSONObject(0);
            String jsonText = part.getString("text");

            jsonText = jsonText.replaceAll("\\n","").replace("```json", "").replace("```", "").trim();

            // Parse the extracted JSON text
            if(jsonText.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonText);
                return jsonArray.getJSONObject(0).toString(2);
            }else{
                JSONObject node = new JSONObject(jsonText);
                return node.toString(2);
            }
        }
        return null;
    }

    private String prepareRequest(String base64Image){
        String json = readFile("prompt.json");
        if(json != null) {
            return json.replace("${image}", base64Image);
        }
        return null;
    }

    public static String readFile(String fileName){
        try {
            File file = ResourceUtils.getFile("classpath:"+fileName);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
           return null;
        }
    }
}
