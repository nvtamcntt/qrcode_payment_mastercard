package com.mastercard.labs.sng.qrscantester.model;

/**
 * Created by nvtamcntt on 2018/10/04.
 */

public class Item {
    private String transactionId;
    private String transactionDetail;

    public Item(String transactionId, String transactionDetail) {
        this.transactionId = transactionId;
        this.transactionDetail = transactionDetail;
    }

    public String getTransactionDetail() {
        return transactionDetail;
    }

    public void setTransactionDetail(String transactionDetail) {
        this.transactionDetail = transactionDetail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
