package com.mastercard.labs.sng.qrscantester.results;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.view.View;

import com.mastercard.labs.sng.qrscantester.R;
import com.mastercard.labs.sng.qrscantester.utils.QRGenerator;

import java.io.Serializable;

/**
 * Created by kaile on 22/8/17.
 * Branches from ResultActivity, is specific to outcome of generating Qrs.
 */

public class GenerateActivity extends ScanActivity {

    private static String BUNDLE_SCANERROR = "SCAN_ERROR";

    private QRGenerator qrGenerator;
    private ProgressBar progressBar;
    private FrameLayout qrFrame;
    private ImageView imgQrCode;

    public static Intent createNewIntent(Context context, Serializable exceptionDetails, Serializable qrString, boolean scanError) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(BUNDLE_PPDATA, qrString);
        bundle.putSerializable(BUNDLE_EXCEPTIONDETAILS, exceptionDetails);
        bundle.putBoolean(BUNDLE_SCANERROR, scanError);

        Intent intent = new Intent(context, GenerateActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBindingUtil.setContentView(this, R.layout.results_generate_layout);

        qrFrame = (FrameLayout) findViewById(R.id.frame_qr_code);
        progressBar = (ProgressBar) findViewById(R.id.pb_qr_code);
        imgQrCode = (ImageView) findViewById(R.id.img_qr_code);


        if (savedInstanceState != null) {
            scanError = savedInstanceState.getBoolean(BUNDLE_SCANERROR);
        } else {
            scanError = getIntent().getBooleanExtra(BUNDLE_SCANERROR, false);
        }

        if (exceptionDetails == null) {
            generateError = false;
        } else generateError = true;

        type = "GENERATE";
        initialiseFields();
        updateStatus();

        if (!generateError)
            fillQRCode();
        else QRFailed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(BUNDLE_SCANERROR, scanError);
        super.onSaveInstanceState(bundle);
    }

    /**
     * Creates QR Code image from received qrData.
     */
    private void fillQRCode() {
        if (qrGenerator != null && qrGenerator.getStatus() == AsyncTask.Status.RUNNING) {
            qrGenerator.cancel(true);
        }

        int width = getResources().getDimensionPixelSize(R.dimen.size_qr_code);
        int height = getResources().getDimensionPixelSize(R.dimen.size_qr_code);

        qrGenerator = new QRGenerator(width, height, new QRGenerator.QRGeneratorListener() {
            @Override
            public void qrGenerationStarted() {
                showQRLoadingProgress();
            }

            @Override
            public void qrGenerated(Bitmap bitmap) {
                hideQRLoadingProgress();

                if (bitmap == null) {
                    progressBar.setVisibility(View.GONE);
                }

                imgQrCode.setImageBitmap(bitmap);
            }
        });

        qrGenerator.execute(qrData.toString());
    }

    private void showQRLoadingProgress() {
        imgQrCode.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideQRLoadingProgress() {
        imgQrCode.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void QRFailed() {
        qrFrame.setVisibility(View.GONE);
        updateStatus();
    }


    public void onGoBack(View view) {
        finish();
    }

    public void onScanClicked(View view) {
        finish();
    }
}
