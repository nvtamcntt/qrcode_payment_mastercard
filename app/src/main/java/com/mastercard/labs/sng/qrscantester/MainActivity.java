package com.mastercard.labs.sng.qrscantester;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.mastercard.labs.sng.qrscantester.model.Merchant;
import com.mastercard.labs.sng.qrscantester.model.PaymentData;
import com.mastercard.labs.sng.qrscantester.model.PaymentInstrument;
import com.mastercard.labs.sng.qrscantester.results.ScanActivity;
import com.mastercard.labs.sng.qrscantester.utils.DecodeImageThread;
import com.mastercard.mpqr.pushpayment.exception.FormatException;
import com.mastercard.mpqr.pushpayment.model.AdditionalData;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;
import com.mastercard.mpqr.pushpayment.parser.Parser;
import com.mastercard.mpqr.pushpayment.scan.PPIntentIntegrator;
import com.mastercard.mpqr.pushpayment.scan.constant.PPIntents;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMAGE = 0x0000c0dd; // Only use bottom 16 bits
    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;

    ImageButton buttonFromImage;
    ImageButton buttonFromCamera;

    private Executor mQrCodeExecutor;
    private Handler mHandler;
    private DecodeImageThread.DecodeImageCallback mDecodeImageCallback;

    long cameraScanStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonFromCamera = (ImageButton) findViewById(R.id.button_from_camera);
        buttonFromImage = (ImageButton) findViewById(R.id.button_from_image);
        ((TextView)findViewById(R.id.version)).setText('v' + BuildConfig.VERSION_NAME);

        mQrCodeExecutor = Executors.newSingleThreadExecutor();
        mHandler = new WeakHandler(this);
        mDecodeImageCallback = new DecodeImageThread.DecodeImageCallback() {
            @Override
            public void decodeSucceed(Result result, String performanceResult) {

                List<String> successMessage = new ArrayList<>();
                successMessage.add(result.getText());

                //performanceResult can be added in the future
                successMessage.add(performanceResult);
                mHandler.obtainMessage(MSG_DECODE_SUCCEED, successMessage).sendToTarget();
            }

            @Override
            public void decodeFail() {
                mHandler.sendEmptyMessage(MSG_DECODE_FAIL);
            }
        };

    }

    public void scanFromCamera(View view) { //shows the screen to scan QR code
        PPIntentIntegrator integrator = new PPIntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        cameraScanStartTime = System.nanoTime();
        integrator.initiateScan();
    }

    public void pickImageFromAlbum(View view) { //upload QR code from Album
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "Choose QR Code Picture");
        this.startActivityForResult(wrapperIntent, REQUEST_CODE_IMAGE);
    }

    /**
     * Launched upon uploading or scanning an image, handles exceptions and deciphered data from QR Codes.
     *
     * @param requestCode type of request that is being sent
     * @param resultCode  type of result that has been caught
     * @param data        data contained in intent that has been passed
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_IMAGE) {
                mQrCodeExecutor.execute(new DecodeImageThread(data.getData(), this.getApplicationContext(), mDecodeImageCallback));
            } else {
                Serializable qrData = data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA);
                updateResult(null, qrData);
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (data != null) {
                FormatException e = (FormatException) data.getSerializableExtra(PPIntents.PARSE_ERROR);
                if (e != null) {
                    Serializable qrData = data.getSerializableExtra(PPIntents.PUSH_PAYMENT_DATA);
                    updateResult(new ExceptionDetails(e), qrData);
                }
            }
        }

        this.getApplicationContext();
        getContentResolver();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateResult(Serializable exceptionDetails, Serializable qrData) {
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

        Intent intent = ScanActivity.newIntent(this, exceptionDetails, qrData, paymentData);
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

        return new PaymentData(101, selectedPaymentInstrument.getId(), true, pushPaymentData.getTransactionAmount(), tipInfo, tip, pushPaymentData.getTransactionCurrencyCode(), additionalData == null ? null : additionalData.getMobileNumber(), merchant(pushPaymentData));
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


    /**
     * Handler for managing results of uploading QR Codes.
     */
    private static class WeakHandler extends Handler {
        private WeakReference<MainActivity> mainActivityWeakReference;

        public WeakHandler(MainActivity activity) {
            super();
            this.mainActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mainActivityWeakReference.get();
            switch (msg.what) {
                case MSG_DECODE_SUCCEED:
                    List<String> successMessage = (List<String>) msg.obj;
                    PushPaymentData qrcode = null;
                    try {
                        qrcode = Parser.parse(successMessage.get(0));
                        mainActivity.updateResult(null, qrcode);
                    } catch (FormatException e) {
                        Toast.makeText(mainActivity, "Cannot properly read QR code!\nPlease try again.", Toast.LENGTH_LONG).show();
                    }

                    break;
                case MSG_DECODE_FAIL:
                    Toast.makeText(mainActivity, "Cannot get QR code!\nPlease try again.", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
