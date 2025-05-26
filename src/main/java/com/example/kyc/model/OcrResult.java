package com.example.kyc.model;

public class OcrResult {
    private String name;
    private String idNumber;
    private String nationality;

    public OcrResult(String name, String idNumber, String nationality) {
        this.name = name;
        this.idNumber = idNumber;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getNationality() {
        return nationality;
    }
}
