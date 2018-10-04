package com.mastercard.labs.sng.qrscantester.results;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mastercard.labs.sng.qrscantester.DetailResultActivity;
import com.mastercard.labs.sng.qrscantester.ExceptionDetails;
import com.mastercard.labs.sng.qrscantester.R;
import com.mastercard.labs.sng.qrscantester.api.ApiClient;
import com.mastercard.labs.sng.qrscantester.api.request.PaymentRequest;
import com.mastercard.labs.sng.qrscantester.api.response.PaymentResponse;
import com.mastercard.labs.sng.qrscantester.model.ListSimpleResponese;
import com.mastercard.labs.sng.qrscantester.model.Merchant;
import com.mastercard.labs.sng.qrscantester.model.PaymentData;
import com.mastercard.labs.sng.qrscantester.model.PaymentInstrument;
import com.mastercard.labs.sng.qrscantester.utils.DialogUtils;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
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

        Intent intent = DetailResultActivity.GenerateActivity.createNewIntent(this, generatedError, ppData, scanError);
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

    public void inputAmountSetting(View v) {
        //generatePPString
        String generatedFeedback = "", dumpedData = null;
        PaymentData paymentData = null;

        if (qrData != null) {
            try {
                PushPaymentData ppData = (PushPaymentData)qrData;
                dumpedData = ppData.dumpData();
                paymentData = paymentData(ppData);
            } catch (Exception e) {
                generatedFeedback += "Errors: " + e.getMessage() + "\n";
            }
        }
        if (dumpedData != null)
            generatedFeedback  += "Generated QR Code: " + dumpedData;

        Intent intent = InputAmountActivity.newIntent(this, exceptionDetails, qrData, paymentData);
        startActivity(intent);
    }

    private PaymentData paymentData(PushPaymentData pushPaymentData) { //implementation to convert payment data model from sdk to app to be used in payment api request
        PaymentData.TipInfo tipInfo = null;
        double tip = 0;
        if (pushPaymentData.getTipOrConvenienceIndicator() != null) {
            switch (pushPaymentData.getTipOrConvenienceIndicator()) {
                case PushPaymentData.TipConvenienceIndicator.FLAT_CONVENIENCE_FEE:
                    tipInfo = PaymentData.TipInfo.FLAT_CONVENIENCE_FEE;
                    tip = pushPaymentData.getValueOfConvenienceFeeFixed();

                    break;
                case PushPaymentData.TipConvenienceIndicator.PERCENTAGE_CONVENIENCE_FEE:
                    tipInfo = PaymentData.TipInfo.PERCENTAGE_CONVENIENCE_FEE;
                    tip = pushPaymentData.getValueOfConvenienceFeePercentage();

                    break;
                case PushPaymentData.TipConvenienceIndicator.PROMPTED_TO_ENTER_TIP:
                    tipInfo = PaymentData.TipInfo.PROMPTED_TO_ENTER_TIP;
                    tip = 0;

                    break;
            }
        }

        PaymentInstrument selectedPaymentInstrument = new PaymentInstrument();
        AdditionalData additionalData = pushPaymentData.getAdditionalData();

        return new PaymentData(101, selectedPaymentInstrument.getId(), true, pushPaymentData.getTransactionAmount(),
                tipInfo, tip, pushPaymentData.getTransactionCurrencyCode(), additionalData == null ? null : additionalData.getMobileNumber(),
                merchant(pushPaymentData));
    }

    private Merchant merchant(PushPaymentData pushPaymentData) { // fetch merchant data from payment data
        Merchant merchant = new Merchant();

        merchant.setName(pushPaymentData.getMerchantName());
        merchant.setCity(pushPaymentData.getMerchantCity());
        merchant.setCategoryCode(pushPaymentData.getMerchantCategoryCode());
        merchant.setIdentifierVisa02(pushPaymentData.getMerchantIdentifierVisa02());
        merchant.setIdentifierVisa03(pushPaymentData.getMerchantIdentifierVisa03());
        merchant.setIdentifierMastercard04(pushPaymentData.getMerchantIdentifierMastercard04());
        merchant.setIdentifierMastercard05(pushPaymentData.getMerchantIdentifierMastercard05());
        merchant.setIdentifierNPCI06(pushPaymentData.getMerchantIdentifierNPCI06());
        merchant.setIdentifierNPCI07(pushPaymentData.getMerchantIdentifierNPCI07());
        if (pushPaymentData.getAdditionalData() != null) {
            merchant.setTerminalNumber(pushPaymentData.getAdditionalData().getTerminalId());
            merchant.setStoreId(pushPaymentData.getAdditionalData().getStoreId());
        }

        return merchant;
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
