package com.example.kyc.model;

public class OcrResult {
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

    public OcrResult(String name, String passportNumber, String nationality,
                     String dateOfBirth, String dateOfIssue, String dateOfExpiry,
                     String address, String gender, String placeOfBirth,
                     String issuingAuthority, String mrz) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.nationality = nationality;
        this.dateOfBirth = dateOfBirth;
        this.dateOfIssue = dateOfIssue;
        this.dateOfExpiry = dateOfExpiry;
        this.address = address;
        this.gender = gender;
        this.placeOfBirth = placeOfBirth;
        this.issuingAuthority = issuingAuthority;
        this.mrz = mrz;
    }

    public String getName() { return name; }
    public String getPassportNumber() { return passportNumber; }
    public String getNationality() { return nationality; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getDateOfIssue() { return dateOfIssue; }
    public String getDateOfExpiry() { return dateOfExpiry; }
    public String getAddress() { return address; }
    public String getGender() { return gender; }
    public String getPlaceOfBirth() { return placeOfBirth; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public String getMrz() { return mrz; }
}
