package com.mastercard.labs.sng.qrscantester.results;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.mastercard.labs.sng.qrscantester.ExceptionDetails;
import com.mastercard.labs.sng.qrscantester.R;
import com.mastercard.labs.sng.qrscantester.api.ApiClient;
import com.mastercard.labs.sng.qrscantester.api.request.PaymentRequest;
import com.mastercard.labs.sng.qrscantester.api.response.PaymentResponse;
import com.mastercard.labs.sng.qrscantester.model.ListSimpleResponese;
import com.mastercard.labs.sng.qrscantester.model.PaymentData;
import com.mastercard.labs.sng.qrscantester.model.TransactionData;
import com.mastercard.labs.sng.qrscantester.utils.DialogUtils;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kaile on 22/8/17.
 * Branches from ResultActivity, is specific to outcome of scanning Qrs.
 */

public class ScanActivity extends ResultsActivity {

    public static Intent newIntent(Context context, Serializable exceptionDetails, Serializable qrString, PaymentData paymentData) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(BUNDLE_PPDATA, qrString);
        bundle.putSerializable(BUNDLE_EXCEPTIONDETAILS, exceptionDetails);
        bundle.putSerializable(BUNDLE_PAYMENTDATA, paymentData);

        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.results_scan_layout);
        type = "SCAN";
        initialiseFields();

        scanError = exceptionDetails != null;

        if (qrData != null) {
            try {
                Parser.parse(qrData.generatePushPaymentString());
                generateError = false;
            } catch (Exception e) {
                generateError = true;
            }
        } else generateError = true;

        updateStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    /**
     * Generates QR Code from stored QRString on click, launches GenerateActivity.
     *
     * @param view
     */
    public void onGenerateClicked(View view) {
        PushPaymentData ppData = qrData;
        ExceptionDetails generatedError = null;
        if (qrData != null) {
            try {
                ppData = Parser.parse(qrData.generatePushPaymentString());
            } catch (Exception e) {
                generatedError = new ExceptionDetails(e);
            }
        } else generatedError = exceptionDetails;

        Intent intent = GenerateActivity.createNewIntent(this, generatedError, ppData, scanError);
        startActivity(intent);
    }
    public void requestPayment1(View v) {
        Call<ListSimpleResponese> simple = ApiClient.getInterface().makeIndex();
        simple.enqueue(new Callback<ListSimpleResponese>() {
            @Override
            public void onResponse(Call<ListSimpleResponese> call, Response<ListSimpleResponese> response) {
                ListSimpleResponese list = response.body();
                if (list != null) {
                    DialogUtils.showDialog(ScanActivity.this, R.string.error, R.string.receipt_success_payment);
                }
            }

            @Override
            public void onFailure(Call<ListSimpleResponese> call, Throwable t) {
                if (!call.isCanceled()) {
                    DialogUtils.showDialog(ScanActivity.this, R.string.error, R.string.unexpected_error);
                }
            }
        });
    }

    public void requestPayment(View v) {

        if (paymentRequest != null) {
            paymentRequest.cancel();
        }

        showProcessingPaymentLoading();

        final String receiverIdentifier = paymentData.getMerchant().getIdentifierMastercard04();
        final PaymentRequest requestData = new PaymentRequest("ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo", receiverIdentifier, paymentData);

        paymentRequest = ApiClient.getInterface().makePayment(requestData);
        paymentRequest.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                hideProcessingPaymentLoading();
                paymentRequest = null;

                if (!response.isSuccessful()) {
                    DialogUtils.showDialog(ScanActivity.this, R.string.error, R.string.payment_failed);
                    return;
                }

                PaymentResponse paymentResponse = response.body();
                if (paymentResponse != null) {
                    DialogUtils.showDialog(ScanActivity.this, R.string.error, R.string.receipt_success_payment);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                hideProcessingPaymentLoading();
                paymentRequest = null;

                if (!call.isCanceled()) {
                    DialogUtils.showDialog(ScanActivity.this, R.string.error, R.string.unexpected_error);
                }
            }
        });
    }
}
