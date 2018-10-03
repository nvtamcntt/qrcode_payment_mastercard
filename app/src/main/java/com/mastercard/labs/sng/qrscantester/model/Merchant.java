package com.mastercard.labs.sng.qrscantester.model;

import java.io.Serializable;

public class Merchant implements Serializable {
    private String name;
    private String city;
    private String categoryCode;
    private String identifierVisa02;
    private String identifierVisa03;
    private String identifierMastercard04;
    private String identifierMastercard05;
    private String identifierNPCI06;
    private String identifierNPCI07;
    private String terminalNumber;
    private String storeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getIdentifierVisa02() {
        return identifierVisa02;
    }

    public void setIdentifierVisa02(String identifierVisa02) {
        this.identifierVisa02 = identifierVisa02;
    }

    public String getIdentifierVisa03() {
        return identifierVisa03;
    }

    public void setIdentifierVisa03(String identifierVisa03) {
        this.identifierVisa03 = identifierVisa03;
    }

    public String getIdentifierMastercard04() {
        return identifierMastercard04;
    }

    public void setIdentifierMastercard04(String identifierMastercard04) {
        this.identifierMastercard04 = identifierMastercard04;
    }

    public String getIdentifierMastercard05() {
        return identifierMastercard05;
    }

    public void setIdentifierMastercard05(String identifierMastercard05) {
        this.identifierMastercard05 = identifierMastercard05;
    }

    public String getIdentifierNPCI06() {
        return identifierNPCI06;
    }

    public void setIdentifierNPCI06(String identifierNPCI06) {
        this.identifierNPCI06 = identifierNPCI06;
    }

    public String getIdentifierNPCI07() {
        return identifierNPCI07;
    }

    public void setIdentifierNPCI07(String identifierNPCI07) {
        this.identifierNPCI07 = identifierNPCI07;
    }

    public String getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(String terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
