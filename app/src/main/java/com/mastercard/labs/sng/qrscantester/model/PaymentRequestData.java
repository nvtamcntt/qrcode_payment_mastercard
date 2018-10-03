package com.mastercard.labs.sng.qrscantester.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentRequestData {
    private String mastercard_assigned_id;
    private String transaction_local_date_time;
    private String original_status;
    private String payment_origination_country;
    private String resource_type;
    private String sender_account_uri;
    private Participant participant = new Participant();
    private String participation_id;
    private String recipient_account_uri;
    private String payment_type;
    private TransactionAmount transfer_amount;
    private Person sender = new Person();
    private Person recipient = new Person();
    private String transfer_reference;
    private String additional_message;

    public PaymentRequestData() {
        this.transfer_reference = "400230517077183589418977281159883110" + (int)(Math.random() * 100);
        this.payment_type = "P2M";
        this.payment_origination_country = "USA";
        this.sender_account_uri = "pan:5184680430000006;exp=2020-08;cvc=123";
//        this.transaction_local_date_time = "2017-11-30T10:22:11-05:30";
        this.transaction_local_date_time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date());
        this.sender.first_name = "John~Tiger";
        this.sender.middle_name = "T";
        this.sender.last_name = "Jones";
        this.sender.address.line1 = "1 Main St";
        this.sender.address.line2 = "Apartment 9";
        this.sender.address.city = "OFallon";
        this.sender.address.country_subdivision = "MO";
        this.sender.address.postal_code = 63368;
        this.sender.address.country = "USA";
        this.sender.phone = "21234567891";
        this.sender.email = "Jane.Sender123@abcmail.com";
        this.sender.token_cryptogram.type = "CONTACTLESS_CHIP";
        this.sender.token_cryptogram.value = "hjjoutwsdgfdou124354ljlsdhgout96895";
        this.sender.authentication_value = "ucaf:jJJLtQa+Iws8AREAEbjsA1MAAAB";
        this.recipient_account_uri = "pan:5184680430000014;exp=2020-08;cvc=123";
//        this.recipient.merchant_category_code = "";
        this.recipient.first_name = "Jane";
        this.recipient.middle_name = "T";
        this.recipient.last_name = "Smith";
        this.recipient.address.line1 = "1 Main St";
        this.recipient.address.line2 = "Apartment 9";
        this.recipient.address.city = "OFallon";
        this.recipient.address.country_subdivision = "MO";
        this.recipient.address.postal_code = 63368;
        this.recipient.address.country = "USA";
        this.recipient.phone = "11234567890";
        this.recipient.email = "Jane.Smith123@abcmail.com";
        this.recipient.token_cryptogram.type = "CONTACTLESS_CHIP";
        this.recipient.token_cryptogram.value = "hjjoutwsdgfdou124354ljlsdhgout96896";
        this.recipient.authentication_value = "ucaf:jJJLtQa+Iws8AREAEbjsA1MAAAb";
//        this.participant.card_acceptor_name = "";
        this.participation_id = "TERMINAL34728";
        this.additional_message = "myadditionalmessage";
        this.mastercard_assigned_id = "101010";
    }

    public TransactionAmount getTransferAmount() {
        return transfer_amount;
    }

    public void setTransferAmount(String currency, Double value) {
        this.transfer_amount = new TransactionAmount(currency, value);
    }

    public void setMerchant(Merchant merchant) {
        this.recipient.merchant_category_code = merchant.getCategoryCode();
        this.participant.card_acceptor_name = merchant.getName();

    }

    public class TransactionAmount {
        private String currency;
        private String value;

        private TransactionAmount(String currency, Double value) {
            this.currency = currency;
            this.value = String.format(Locale.getDefault(), "%d", (int)(value * 100));
        }

        public String getCurrency() {
            return currency;
        }

        public Double getValue() {
            return Integer.valueOf(value) / 100.0;
        }
    }

    private class Person {
        private String authentication_value;
        private Address address = new Address();
        private String phone;
        private TokenCryptogram token_cryptogram = new TokenCryptogram();
        private String first_name;
        private String middle_name;
        private String last_name;
        private String email;
        private String merchant_category_code;
    }

    private class Address {
        private String country;
        private String city;
        private int postal_code;
        private String line1;
        private String line2;
        private String country_subdivision;
    }

    private class TokenCryptogram {
        private String type;
        private String value;
    }

    public class Participant {
//        private String card_acceptor_id;
        private String card_acceptor_name;
    }
}
