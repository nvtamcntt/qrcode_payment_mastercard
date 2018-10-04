package com.mastercard.labs.sng.qrscantester.model;

/**
 * Created by nvtamcntt on 2018/10/04.
 */

public class TransactionLocal {
    private String key_id;
    private String transfer_reference;
    private String status;
    private String resource_type;
    private String sender_account_uri;
    private String original_status;

    public TransactionLocal(String key_id, String transfer_reference, String status, String resource_type, String sender_account_uri, String original_status) {
        this.key_id = key_id;
        this.transfer_reference = transfer_reference;
        this.status = status;
        this.resource_type = resource_type;
        this.sender_account_uri = sender_account_uri;
        this.original_status = original_status;
    }

    public String getId() {
        return key_id;
    }

    public void setId(String key_id) {
        this.key_id = key_id;
    }

    public String getTransfer_reference() {
        return transfer_reference;
    }

    public void setTransfer_reference(String transfer_reference) {
        this.transfer_reference = transfer_reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getSender_account_uri() {
        return sender_account_uri;
    }

    public void setSender_account_uri(String sender_account_uri) {
        this.sender_account_uri = sender_account_uri;
    }

    public String getOriginal_status() {
        return original_status;
    }

    public void setOriginal_status(String original_status) {
        this.original_status = original_status;
    }
}
