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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(CAMUNDA_IDP_URL);
            httpPost.setHeader("Authorization", "Bearer " + API_KEY);

            File file = new File(documentPath);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            httpPost.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception("Camunda IDP API error: " + response.getStatusLine());
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                DocumentExtractionResult result = objectMapper.readValue(jsonResponse, DocumentExtractionResult.class);

                return new OcrResult(
                    result.getName(),
                    result.getPassportNumber(),
                    result.getNationality(),
                    result.getDateOfBirth(),
                    result.getDateOfIssue(),
                    result.getDateOfExpiry(),
                    result.getAddress(),
                    result.getGender(),
                    result.getPlaceOfBirth(),
                    result.getIssuingAuthority(),
                    result.getMrz()
                );
            }
        }
    }

    private static class DocumentExtractionResult {
        private String name;
        private String passportNumber;
        private String nationality;
        private String dateOfBirth;
        private String dateOfIssue;
        private String dateOfExpiry;
        private String address;
        private String gender;
        private String placeOfBirth;
        private String issuingAuthority;
        private String mrz;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassportNumber() { return passportNumber; }
        public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
        public String getNationality() { return nationality; }
        public void setNationality(String nationality) { this.nationality = nationality; }
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getDateOfIssue() { return dateOfIssue; }
        public void setDateOfIssue(String dateOfIssue) { this.dateOfIssue = dateOfIssue; }
        public String getDateOfExpiry() { return dateOfExpiry; }
        public void setDateOfExpiry(String dateOfExpiry) { this.dateOfExpiry = dateOfExpiry; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getPlaceOfBirth() { return placeOfBirth; }
        public void setPlaceOfBirth(String placeOfBirth) { this.placeOfBirth = placeOfBirth; }
        public String getIssuingAuthority() { return issuingAuthority; }
        public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
        public String getMrz() { return mrz; }
        public void setMrz(String mrz) { this.mrz = mrz; }
    }
}
