package com.mastercard.labs.sng.qrscantester;

import android.os.Bundle;
import android.view.View;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mastercard.mpqr.pushpayment.scan.activity.PPCaptureActivity;


public class CustomizedCaptureActivity extends PPCaptureActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected DecoratedBarcodeView initializeContent() {
        this.setContentView(R.layout.activity_pp_capture_customized);
        return (DecoratedBarcodeView)this.findViewById(com.mastercard.mpqr.pushpayment.scan.R.id.zxing_barcode_scanner);
    }

    public void toggleTorch(View view) {
        super.toggleTorch(view);
    }

    public void goBack(View view) {
        super.goBack(view);
    }
}
