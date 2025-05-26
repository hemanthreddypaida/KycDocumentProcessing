
package com.example.kyc.service;

import com.example.kyc.model.OcrResult;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("PassportDataExtractor")
public class PassportDataExtractor implements JavaDelegate {
    private final OcrApiClient ocrApiClient;

    public PassportDataExtractor(OcrApiClient ocrApiClient) {
        this.ocrApiClient = ocrApiClient;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String documentPath = (String) execution.getVariable("passportFilePath");
        OcrResult ocrResult = ocrApiClient.extractData(documentPath);

        String name = ocrResult.getName();
        String idNumber = ocrResult.getIdNumber();
        String nationality = ocrResult.getNationality();

        if (name == null || idNumber == null || nationality == null) {
            throw new IllegalStateException("Incomplete data extracted from passport");
        }

        execution.setVariable("customerName", name);
        execution.setVariable("idNumber", idNumber);
        execution.setVariable("nationality", nationality);
    }
}
