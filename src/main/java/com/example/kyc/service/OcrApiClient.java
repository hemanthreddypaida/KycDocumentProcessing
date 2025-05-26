
package com.example.kyc.service;

import com.example.kyc.model.OcrResult;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class OcrApiClient {
    private static final String CAMUNDA_IDP_URL = "https://your-camunda-idp-endpoint/api/v1/document/process";
    private static final String API_KEY = "your-api-key"; // Replace with your Camunda IDP API key
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OcrResult extractData(String documentPath) throws Exception {
        // Create HTTP client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(CAMUNDA_IDP_URL);
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);

            // Attach passport file
            File file = new File(documentPath);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            httpPost.setEntity(builder.build());

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception("Camunda IDP API error: " + response.getStatusLine());
                }

                // Parse response (assuming JSON response with fields: name, idNumber, nationality)
                String jsonResponse = EntityUtils.toString(response.getEntity());
                // Example response: {"name": "John Doe", "idNumber": "123456789", "nationality": "USA"}
                DocumentExtractionResult result = objectMapper.readValue(jsonResponse, DocumentExtractionResult.class);

                return new OcrResult(result.getName(), result.getIdNumber(), result.getNationality());
            }
        }
    }

    // Inner class to map Camunda IDP response
    private static class DocumentExtractionResult {
        private String name;
        private String idNumber;
        private String nationality;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }
    }
}
