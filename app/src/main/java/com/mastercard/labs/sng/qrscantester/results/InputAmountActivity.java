package com.mastercard.labs.sng.qrscantester.results;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mastercard.labs.sng.qrscantester.R;
import com.mastercard.labs.sng.qrscantester.TransactionListActivity;
import com.mastercard.labs.sng.qrscantester.api.ApiClient;
import com.mastercard.labs.sng.qrscantester.api.request.PaymentRequest;
import com.mastercard.labs.sng.qrscantester.api.response.PaymentResponse;
import com.mastercard.labs.sng.qrscantester.model.Merchant;
import com.mastercard.labs.sng.qrscantester.model.PaymentData;
import com.mastercard.labs.sng.qrscantester.model.PaymentInstrument;
import com.mastercard.labs.sng.qrscantester.model.TransactionData;
import com.mastercard.labs.sng.qrscantester.model.TransactionLocal;
import com.mastercard.labs.sng.qrscantester.results.ResultsActivity;
import com.mastercard.labs.sng.qrscantester.results.ScanActivity;
import com.mastercard.labs.sng.qrscantester.sql.DatabaseHandler;
import com.mastercard.labs.sng.qrscantester.utils.DialogUtils;
import com.mastercard.labs.sng.qrscantester.utils.TransactionDialog;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PATCH;

/**
 * Created by nvtamcntt on 2018/10/03.
 */

public class InputAmountActivity extends ResultsActivity {
    private String TAG = "InputAmountActivity";
    private DatabaseHandler mDatabaseHandler;

    public static Intent newIntent(Context context, Serializable exceptionDetails, Serializable qrString, PaymentData paymentData) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(BUNDLE_PPDATA, qrString);
        bundle.putSerializable(BUNDLE_EXCEPTIONDETAILS, exceptionDetails);
        bundle.putSerializable(BUNDLE_PAYMENTDATA, paymentData);

        Intent intent = new Intent(context, InputAmountActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    private EditText mTransactionAmount;

    private TextView mStoreName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.fragment_input_layout);
//        initialiseFields();

        mDatabaseHandler = new DatabaseHandler(this, null);

        if (qrData != null) {
            try {
                Parser.parse(qrData.generatePushPaymentString());
                generateError = false;
            } catch (Exception e) {
                generateError = true;
            }
        } else generateError = true;

        mTransactionAmount = (EditText) findViewById(R.id.transactionAmount);
        mStoreName = (TextView)findViewById(R.id.nameStore);

        mStoreName.setText(paymentData.getMerchant().getName());
//        updateStatus();
    }

    public void requestPayment(View v) {

        if (paymentRequest != null) {
            paymentRequest.cancel();
        }

        showProcessingPaymentLoading();

        final String receiverIdentifier = paymentData.getMerchant().getIdentifierMastercard04();
        if (mTransactionAmount != null && !"".equals(mTransactionAmount.getText().toString()))
            paymentData.setTransactionAmount((double) Double.parseDouble(mTransactionAmount.getText().toString()));


        Log.d("nvtamcntt >>>> " , paymentData.getTransactionAmount() + "");
        final PaymentRequest requestData = new PaymentRequest("ptnr_BEeCrYJHh2BXTXPy_PEtp-8DBOo", receiverIdentifier, paymentData);

        paymentRequest = ApiClient.getInterface().makePayment(requestData);
        paymentRequest.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                hideProcessingPaymentLoading();
                paymentRequest = null;

                if (!response.isSuccessful()) {
                    DialogUtils.showDialog(InputAmountActivity.this, R.string.error, R.string.payment_failed);
                    return;
                }

                PaymentResponse paymentResponse = response.body();
                Log.d(TAG,"nvtamcntt >>>> respond " + paymentResponse);
                if (paymentResponse != null){
                    TransactionData transactionData = paymentResponse.merchant_transfer;
                    TransactionLocal transactionLocal = new TransactionLocal(
                        transactionData.getId(),
                        transactionData.getTransferReference(),
                        transactionData.getStatus(),
                        transactionData.getResourceType(),
                        transactionData.getSenderAccountUri(),
                        transactionData.getOriginalStatus(),
                        transactionData.getTransferAmount().getValue().toString(),
                        transactionData.getParticipant().getCard_acceptor_name()
                    );
                    mDatabaseHandler.addTransaction(transactionLocal);

                    TransactionDialog.showDialog(InputAmountActivity.this, R.string.error, R.string.receipt_success_payment);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                hideProcessingPaymentLoading();
                paymentRequest = null;

                if (!call.isCanceled()) {
                    DialogUtils.showDialog(InputAmountActivity.this, R.string.error, R.string.unexpected_error);
                }
            }
        });
    }

}
