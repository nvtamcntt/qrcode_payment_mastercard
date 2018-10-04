package com.mastercard.labs.sng.qrscantester;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mastercard.labs.sng.qrscantester.results.ScanActivity;
import com.mastercard.labs.sng.qrscantester.results.tag.SubDataModelTag;
import com.mastercard.labs.sng.qrscantester.results.tag.Tag;
import com.mastercard.labs.sng.qrscantester.utils.QRGenerator;

import java.io.Serializable;

import static com.mastercard.labs.sng.qrscantester.DetailFragment.IS_GENERATE;
import static com.mastercard.labs.sng.qrscantester.DetailFragment.TAG_INSTANCE;

public class DetailResultActivity extends AppCompatActivity {

    /**
     * Tag that details page is displaying.
     */
    private Tag mainTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        boolean isGenerate = bundle.getBoolean(IS_GENERATE);

        if (bundle.getSerializable(TAG_INSTANCE) instanceof SubDataModelTag) {
            mainTag = (SubDataModelTag) (bundle.getSerializable(TAG_INSTANCE));
        } else {
            mainTag = (Tag) (bundle.getSerializable(TAG_INSTANCE));
        }

        setContentView(R.layout.activity_detail_result);

        TextView tagView = (TextView) findViewById(R.id.activity_detail_result_tag);
        TextView tagNameView = (TextView) findViewById(R.id.activity_detail_result_tag_name);

        tagView.setText(mainTag.getTagNumber());
        tagNameView.setText(mainTag.getTagName());

        setDescription();
        setRootTag();
        setTitle(isGenerate);
        setValues(bundle);
    }

    /**
     * Handles view for displaying root-tag.
     */
    public void setRootTag() {
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rootTag);
        TextView rootValue = (TextView) findViewById(R.id.roottag_value);
        if (mainTag.getRootTag() != null) {
            rootValue.setText(mainTag.getRootTag().getTagName());
        } else {
            rootView.setVisibility(View.GONE);
        }
    }

    /**
     * Handles view for displaying tag's description.
     */
    public void setDescription() {
        RelativeLayout descriptionView = (RelativeLayout) findViewById(R.id.description);
        TextView descriptionValue = (TextView) findViewById(R.id.description_value);
        if (mainTag.getTagDesc() != null && mainTag.getTagDesc() != "") {
            descriptionValue.setText(mainTag.getTagDesc());
        } else {
            descriptionView.setVisibility(View.GONE);
        }
    }

    /**
     * Handles view when for displaying title of activity page.
     *
     * @param isGenerate true if tag a result of generation, false if not
     */
    public void setTitle(boolean isGenerate) {
        TextView toolbarTitle = (TextView) findViewById(R.id.tbTitle);
        if (mainTag.getRootTag() != null && isGenerate) {
            toolbarTitle.setText("GENERATE DETAILS (TAG " + mainTag.getRootTag().getTagNumber() + ")");
        } else if (mainTag.getRootTag() != null && !isGenerate) {
            toolbarTitle.setText("SCAN DETAILS (TAG " + mainTag.getRootTag().getTagNumber() + ")");
        } else if (mainTag.getRootTag() == null && isGenerate) {
            toolbarTitle.setText("GENERATE DETAILS");
        } else {
            toolbarTitle.setText("SCAN DETAILS");
        }
    }

    /**
     * Handles view when for displaying value(s) of tag.
     * If tag contains a SubDataModelInstance, array list of tags is displayed, else a single value is displayed.
     *
     * @param bundle
     */
    public void setValues(Bundle bundle) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        TextView tagValueText = (TextView) findViewById(R.id.tag_value_value);
        DetailFragment detailFragment = (DetailFragment) fragmentManager.findFragmentById(R.id.fragment1);

        if (mainTag instanceof SubDataModelTag) {
            findViewById(R.id.tag_value_view).setVisibility(View.GONE);
            if (((SubDataModelTag) mainTag).getSubData().isEmpty()) {
                (findViewById(R.id.subtags_view)).setVisibility(View.GONE);
            } else {
                detailFragment.relayout(bundle);
            }
        } else {
            (findViewById(R.id.subtags_view)).setVisibility(View.GONE);
            tagValueText.setText(mainTag.getTagValue());
            setLayout();
        }
    }

    /**
     * Handles view when for displaying single value of tag accordingly to if tag is erroneous.
     */
    public void setLayout() {
        RelativeLayout tagValueView = (RelativeLayout) findViewById(R.id.tag_value_view);
        TextView tagValueText = (TextView) findViewById(R.id.tag_value_value);
        TextView errorTagValueView = (TextView) findViewById(R.id.error);
        if (mainTag.hasError()) {
            tagValueView.setBackgroundColor(getResources().getColor(R.color.errorBackground));

            if (mainTag.getErrorMessage().isEmpty()) {
                errorTagValueView.setVisibility(View.GONE);
            } else {
                if (mainTag.hasInvalidValue()) {
                    errorTagValueView.setText(mainTag.getErrorMessage());
                    errorTagValueView.setVisibility(View.VISIBLE);
                } else
                    errorTagValueView.setVisibility(View.GONE);
            }
            tagValueText.setTextColor(getResources().getColor(R.color.colorMuted));
        }
    }

    /**
     * Action for when clicking on back arrow.
     */
    public void onGoBack(View view) {
        finish();
    }

    /**
     * Created by kaile on 22/8/17.
     * Branches from ResultActivity, is specific to outcome of generating Qrs.
     */

    public static class GenerateActivity extends ScanActivity {

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
}
