package com.mastercard.labs.sng.qrscantester.model;

import java.util.ArrayList;
import java.util.Date;

public class TransactionData {
    private String mastercard_assigned_id;
    private Date transaction_local_date_time;
//    private String device_id;
    private String original_status;
    private TransactionHistory transaction_history;
    private Date created;
    private String payment_origination_country;
    private String resource_type;
//    private String channel;
    private String sender_account_uri;
    private Participant participant;
    private String participation_id;
    private String recipient_account_uri;
    private String payment_type;
    private TransactionAmount transfer_amount;
    private Person sender;
//    private String funding_source;
    private Person recipient;
//    private String location;
    private String id;
    private String transfer_reference;
    private Date status_timestamp;
    private String status;
    private String additional_message;

    public Date getTransactionLocalDateTime() {
        return transaction_local_date_time;
    }

    public String getOriginalStatus() {
        return original_status;
    }

    public TransactionHistory getTransactionHistory() {
        return transaction_history;
    }

    public Date getCreated() {
        return created;
    }

    public String getPaymentOriginationCountry() {
        return payment_origination_country;
    }

    public String getResourceType() {
        return resource_type;
    }

    public String getSenderAccountUri() {
        return sender_account_uri;
    }

    public Participant getParticipant() {
        return participant;
    }

    public String getParticipationId() {
        return participation_id;
    }

    public String getRecipientAccountUri() {
        return recipient_account_uri;
    }

    public String getPaymentType() {
        return payment_type;
    }

    public TransactionAmount getTransferAmount() {
        return transfer_amount;
    }

    public Person getSender() {
        return sender;
    }

    public Person getRecipient() {
        return recipient;
    }

    public String getId() {
        return id;
    }

    public String getTransferReference() {
        return transfer_reference;
    }

    public Date getStatusTimestamp() {
        return status_timestamp;
    }

    public String getStatus() {
        return status;
    }

    public class TransactionHistory {
        private int item_count;
        private Data data;
        private String resource_type;

        public int getItemCount() {
            return item_count;
        }

        public ArrayList<TransactionDetail> getData() {
            return data.transaction;
        }

        public String getResourceType() {
            return resource_type;
        }
    }

    private class Data {
        private ArrayList<TransactionDetail> transaction;
    }

    public class TransactionDetail {
        private String switch_serial_number;
        private String retrieval_reference;
        private String system_trace_audit_number;
        private TransactionAmount transaction_amount;
        private String resource_type;
        private String type;
        private String network;
        private String network_status_code;
        private String create_timestamp;
        private String network_status_description;
        private String funds_availability;
        private String id;
        private Date status_timestamp;
        private String status_reason;
        private String account_uri;
        private String unique_reference_number;
        private String status;

        public String getSwitchSerialNumber() {
            return switch_serial_number;
        }

        public String getRetrievalReference() {
            return retrieval_reference;
        }

        public String getSystemTraceAuditNumber() {
            return system_trace_audit_number;
        }

        public TransactionAmount getTransactionAmount() {
            return transaction_amount;
        }

        public String getResourceType() {
            return resource_type;
        }

        public String getType() {
            return type;
        }

        public String getNetwork() {
            return network;
        }

        public String getNetworkStatusCode() {
            return network_status_code;
        }

        public String getCreateTimestamp() {
            return create_timestamp;
        }

        public String getNetworkStatusDescription() {
            return network_status_description;
        }

        public String getFundsAvailability() {
            return funds_availability;
        }

        public String getId() {
            return id;
        }

        public Date getStatusTimestamp() {
            return status_timestamp;
        }

        public String getStatusReason() {
            return status_reason;
        }

        public String getAccountUri() {
            return account_uri;
        }

        public String getUniqueReferenceNumber() {
            return unique_reference_number;
        }

        public String getStatus() {
            return status;
        }
    }

    public class TransactionAmount {
        private String currency;
        private String value;

        public String getCurrency() {
            return currency;
        }

        public Double getValue() {
            return Integer.valueOf(value) / 100.0;
        }
    }

    private class Person {
        private String authentication_value;
        private Address address;
        private String phone;
        private TokenCryptogram token_cryptogram;
        private String first_name;
        private String middle_name;
        private String last_name;
        private String email;
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

        public String getCard_acceptor_name() {
            return card_acceptor_name;
        }
    }
}
