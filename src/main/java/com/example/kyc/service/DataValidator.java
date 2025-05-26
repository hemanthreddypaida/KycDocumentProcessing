
package com.example.kyc.service;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component("DataValidator")
public class DataValidator implements JavaDelegate {
    // ISO 3166-1 alpha-3 country codes (partial list for demonstration)
    private static final Set<String> VALID_NATIONALITIES = new HashSet<>(Arrays.asList(
        "USA", "GBR", "CAN", "AUS", "DEU", "FRA", "JPN", "CHN", "IND", "BRA"
    ));

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String name = (String) execution.getVariable("customerName");
        String idNumber = (String) execution.getVariable("idNumber");
        String nationality = (String) execution.getVariable("nationality");

        // Validation results
        boolean isNameValid = validateName(name);
        boolean isIdNumberValid = validateIdNumber(idNumber);
        boolean isNationalityValid = validateNationality(nationality);

        boolean isValid = isNameValid && isIdNumberValid && isNationalityValid;
        execution.setVariable("isDataValid", isValid);

        if (!isValid) {
            String errorMessage = buildErrorMessage(isNameValid, isIdNumberValid, isNationalityValid, name, idNumber, nationality);
            throw new IllegalStateException(errorMessage);
        }
    }

    private boolean validateName(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        // Allow letters, spaces, hyphens; length 2-50
        return name.matches("^[a-zA-Z\\s-]{2,50}$");
    }

    private boolean validateIdNumber(String idNumber) {
        if (StringUtils.isBlank(idNumber)) {
            return false;
        }
        // MRZ ID number: 9 alphanumeric characters
        if (!idNumber.matches("^[A-Z0-9]{9}$")) {
            return false;
        }
        // Optional: MRZ checksum validation (simplified example for passport number)
        // Note: Actual MRZ checksum requires specific weights per ICAO 9303
        return true; // Placeholder; implement checksum if needed
    }

    private boolean validateNationality(String nationality) {
        if (StringUtils.isBlank(nationality)) {
            return false;
        }
        // Check against ISO 3166-1 alpha-3 codes
        return VALID_NATIONALITIES.contains(nationality.toUpperCase());
    }

    private String buildErrorMessage(boolean isNameValid, boolean isIdNumberValid, boolean isNationalityValid,
                                    String name, String idNumber, String nationality) {
        StringBuilder error = new StringBuilder("Data validation failed:");
        if (!isNameValid) {
            error.append(" Invalid name (").append(name).append(");");
        }
        if (!isIdNumberValid) {
            error.append(" Invalid ID number (").append(idNumber).append(");");
        }
        if (!isNationalityValid) {
            error.append(" Invalid nationality (").append(nationality).append(");");
        }
        return error.toString();
    }
}
