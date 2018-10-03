package com.mastercard.labs.sng.qrscantester.api.request;

import com.mastercard.labs.sng.qrscantester.model.PaymentData;
import com.mastercard.labs.sng.qrscantester.model.PaymentRequestData;

public class PaymentRequest {

    private String partnerId;
    private PaymentRequestData merchant_transfer = new PaymentRequestData();
    private MerchantTransfer notificationData = new MerchantTransfer();

    private class MerchantTransfer {

        private String receiverCardNumber;
        private Long senderCardId;
        private String currency;
        private double transactionAmount;
        private double tip;
        private String terminalNumber;

    }

    public PaymentRequest(String partnerId, String receiverCardNumber, PaymentData paymentData) {
        this.partnerId = partnerId;
        this.merchant_transfer.setTransferAmount(paymentData.getCurrencyCode().toString(), paymentData.getTransactionAmount());
        this.merchant_transfer.setMerchant(paymentData.getMerchant());

        this.notificationData.receiverCardNumber = receiverCardNumber;
        this.notificationData.senderCardId = paymentData.getCardId();
        this.notificationData.tip = paymentData.getTipAmount();
        this.notificationData.terminalNumber = paymentData.getMerchant().getTerminalNumber();
    }

    public Long getSenderCardId() {
        return notificationData.senderCardId;
    }

    public String getCurrency() {
        return merchant_transfer.getTransferAmount().getCurrency();
    }

    public double getTransactionAmount() {
        return merchant_transfer.getTransferAmount().getValue();
    }

    public double getTip() {
        return notificationData.tip;
    }

    public double getTotal() {
        return getTransactionAmount() + getTip();
    }

    public String getTerminalNumber() {
        return notificationData.terminalNumber;
    }
}
