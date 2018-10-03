package com.mastercard.labs.sng.qrscantester.model;

import com.mastercard.labs.sng.qrscantester.utils.CurrencyCode;

import java.io.Serializable;

public class PaymentData implements Serializable {
    private long userId;
    private long cardId;
    private boolean isDynamic;
    private double transactionAmount;
    private TipInfo tipType;
    private double tip;
    private String currencyNumericCode;
    private String mobile;
    private Merchant merchant;

    public PaymentData(long userId, long cardId, boolean isDynamic, Double transactionAmount, TipInfo tipType, Double tip, String currencyNumericCode, String mobile, Merchant merchant) {
        this.userId = userId;
        this.cardId = cardId;
        this.isDynamic = isDynamic;
        this.transactionAmount = transactionAmount == null ? 0 : transactionAmount;
        this.tipType = tipType;
        this.tip = tip == null ? 0 : tip;
        this.currencyNumericCode = currencyNumericCode;
        this.merchant = merchant;
        this.mobile = mobile;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public TipInfo getTipType() {
        return tipType;
    }

    public void setTipType(TipInfo tipType) {
        this.tipType = tipType;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public void setCurrencyNumericCode(String transactionCurrencyCode) {
        this.currencyNumericCode = transactionCurrencyCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public double getTipAmount() {
        if (PaymentData.TipInfo.PERCENTAGE_CONVENIENCE_FEE.equals(tipType)) {
            return transactionAmount * tip / 100;
        } else {
            return tip;
        }
    }

    public double getTotal() {
        return transactionAmount + getTipAmount();
    }

    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.fromNumericCode(currencyNumericCode);
    }

    public enum TipInfo {
        PROMPTED_TO_ENTER_TIP,
        FLAT_CONVENIENCE_FEE,
        PERCENTAGE_CONVENIENCE_FEE;
    }
}
