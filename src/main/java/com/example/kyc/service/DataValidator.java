package com.example.kyc.service;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component("DataValidator")
public class DataValidator implements JavaDelegate {
    private static final Set<String> VALID_NATIONALITIES = new HashSet<>(Arrays.asList(
        "USA", "GBR", "CAN", "AUS", "DEU", "FRA", "JPN", "CHN", "IND", "BRA"
    ));
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final LocalDate TODAY = LocalDate.now();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String name = (String) execution.getVariable("customerName");
        String passportNumber = (String) execution.getVariable("passportNumber");
        String nationality = (String) execution.getVariable("nationality");
        String dateOfBirth = (String) execution.getVariable("dateOfBirth");
        String dateOfIssue = (String) execution.getVariable("dateOfIssue");
        String dateOfExpiry = (String) execution.getVariable("dateOfExpiry");
        String address = (String) execution.getVariable("address");
        String gender = (String) execution.getVariable("gender");
        String placeOfBirth = (String) execution.getVariable("placeOfBirth");
        String issuingAuthority = (String) execution.getVariable("issuingAuthority");
        String mrz = (String) execution.getVariable("mrz");

        // Validation results
        boolean isNameValid = validateName(name);
        boolean isPassportNumberValid = validatePassportNumber(passportNumber);
        boolean isNationalityValid = validateNationality(nationality);
        boolean isDateOfBirthValid = validateDateOfBirth(dateOfBirth);
        boolean isDateOfIssueValid = validateDateOfIssue(dateOfIssue);
        boolean isDateOfExpiryValid = validateDateOfExpiry(dateOfExpiry, dateOfIssue);
        boolean isAddressValid = validateAddress(address); // Optional
        boolean isGenderValid = validateGender(gender);
        boolean isPlaceOfBirthValid = validatePlaceOfBirth(placeOfBirth);
        boolean isIssuingAuthorityValid = validateIssuingAuthority(issuingAuthority);
        boolean isMrzValid = validateMrz(mrz); // Optional

        boolean isValid = isNameValid && isPassportNumberValid && isNationalityValid &&
                         isDateOfBirthValid && isDateOfIssueValid && isDateOfExpiryValid &&
                         isAddressValid && isGenderValid && isPlaceOfBirthValid &&
                         isIssuingAuthorityValid && isMrzValid;

        execution.setVariable("isDataValid", isValid);

        if (!isValid) {
            String errorMessage = buildErrorMessage(
                isNameValid, isPassportNumberValid, isNationalityValid,
                isDateOfBirthValid, isDateOfIssueValid, isDateOfExpiryValid,
                isAddressValid, isGenderValid, isPlaceOfBirthValid,
                isIssuingAuthorityValid, isMrzValid,
                name, passportNumber, nationality, dateOfBirth, dateOfIssue,
                dateOfExpiry, address, gender, placeOfBirth, issuingAuthority, mrz
            );
            throw new IllegalStateException(errorMessage);
        }
    }

    private boolean validateName(String name) {
        return !StringUtils.isBlank(name) && name.matches("^[a-zA-Z\\s-]{2,50}$");
    }

    private boolean validatePassportNumber(String passportNumber) {
        return !StringUtils.isBlank(passportNumber) && passportNumber.matches("^[A-Z0-9]{9}$");
    }

    private boolean validateNationality(String nationality) {
        return !StringUtils.isBlank(nationality) && VALID_NATIONALITIES.contains(nationality.toUpperCase());
    }

    private boolean validateDateOfBirth(String dateOfBirth) {
        if (StringUtils.isBlank(dateOfBirth)) return false;
        try {
            LocalDate dob = LocalDate.parse(dateOfBirth, DATE_FORMATTER);
            return !dob.isAfter(TODAY);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validateDateOfIssue(String dateOfIssue) {
        if (StringUtils.isBlank(dateOfIssue)) return false;
        try {
            LocalDate issue = LocalDate.parse(dateOfIssue, DATE_FORMATTER);
            return !issue.isAfter(TODAY);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validateDateOfExpiry(String dateOfExpiry, String dateOfIssue) {
        if (StringUtils.isBlank(dateOfExpiry)) return false;
        try {
            LocalDate expiry = LocalDate.parse(dateOfExpiry, DATE_FORMATTER);
            if (StringUtils.isNotBlank(dateOfIssue)) {
                LocalDate issue = LocalDate.parse(dateOfIssue, DATE_FORMATTER);
                return !expiry.isBefore(issue) && !expiry.isBefore(TODAY);
            }
            return !expiry.isBefore(TODAY);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validateAddress(String address) {
        // Optional field, allow null or empty
        return address == null || !address.trim().isEmpty();
    }

    private boolean validateGender(String gender) {
        return !StringUtils.isBlank(gender) && gender.matches("^[MFX]$");
    }

    private boolean validatePlaceOfBirth(String placeOfBirth) {
        return !StringUtils.isBlank(placeOfBirth) && placeOfBirth.matches("^[a-zA-Z\\s]{2,100}$");
    }

    private boolean validateIssuingAuthority(String issuingAuthority) {
        return !StringUtils.isBlank(issuingAuthority) && issuingAuthority.matches("^[a-zA-Z0-9\\s]{2,50}$");
    }

    private boolean validateMrz(String mrz) {
        // Optional field, validate format if provided
        if (StringUtils.isBlank(mrz)) return true;
        // Simplified MRZ validation (2 lines, 44 chars each for Type 1 passport)
        return mrz.matches("^[A-Z0-9<]{44}\\n[A-Z0-9<]{44}$");
    }

    private String buildErrorMessage(boolean isNameValid, boolean isPassportNumberValid,
                                    boolean isNationalityValid, boolean isDateOfBirthValid,
                                    boolean isDateOfIssueValid, boolean isDateOfExpiryValid,
                                    boolean isAddressValid, boolean isGenderValid,
                                    boolean isPlaceOfBirthValid, boolean isIssuingAuthorityValid,
                                    boolean isMrzValid, String name, String passportNumber,
                                    String nationality, String dateOfBirth, String dateOfIssue,
                                    String dateOfExpiry, String address, String gender,
                                    String placeOfBirth, String issuingAuthority, String mrz) {
        StringBuilder error = new StringBuilder("Data validation failed:");
        if (!isNameValid) error.append(" Invalid name (").append(name).append(");");
        if (!isPassportNumberValid) error.append(" Invalid passport number (").append(passportNumber).append(");");
        if (!isNationalityValid) error.append(" Invalid nationality (").append(nationality).append(");");
        if (!isDateOfBirthValid) error.append(" Invalid date of birth (").append(dateOfBirth).append(");");
        if (!isDateOfIssueValid) error.append(" Invalid date of issue (").append(dateOfIssue).append(");");
        if (!isDateOfExpiryValid) error.append(" Invalid date of expiry (").append(dateOfExpiry).append(");");
        if (!isAddressValid) error.append(" Invalid address (").append(address).append(");");
        if (!isGenderValid) error.append(" Invalid gender (").append(gender).append(");");
        if (!isPlaceOfBirthValid) error.append(" Invalid place of birth (").append(placeOfBirth).append(");");
        if (!isIssuingAuthorityValid) error.append(" Invalid issuing authority (").append(issuingAuthority).append(");");
        if (!isMrzValid) error.append(" Invalid MRZ (").append(mrz).append(");");
        return error.toString();
    }
}
