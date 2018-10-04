package com.mastercard.labs.sng.qrscantester.model;

/**
 * Created by nvtamcntt on 2018/10/04.
 */

public class Item {
    private String transactionId;
    private String transactionAmount;
    private String transactionStoreName;

    public Item(String transactionId, String transactionAmount,String transactionStoreName) {
        this.transactionId = transactionId;
        this.transactionAmount = transactionAmount;
        this.transactionStoreName = transactionStoreName;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionStoreName() {
        return transactionStoreName;
    }

    public void setTransactionStoreName(String transactionStoreName) {
        this.transactionStoreName = transactionStoreName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
