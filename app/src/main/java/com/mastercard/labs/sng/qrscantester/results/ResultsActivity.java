package com.mastercard.labs.sng.qrscantester.results;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mastercard.labs.sng.qrscantester.DetailResultActivity;
import com.mastercard.labs.sng.qrscantester.ExceptionDetails;
import com.mastercard.labs.sng.qrscantester.R;
import com.mastercard.labs.sng.qrscantester.api.response.PaymentResponse;
import com.mastercard.labs.sng.qrscantester.model.PaymentData;
import com.mastercard.labs.sng.qrscantester.results.tag.SubDataModelTag;
import com.mastercard.labs.sng.qrscantester.results.tag.Tag;
import com.mastercard.mpqr.pushpayment.enums.PPTag;
import com.mastercard.mpqr.pushpayment.model.AbstractDataModel;
import com.mastercard.mpqr.pushpayment.model.PushPaymentData;

import static com.mastercard.labs.sng.qrscantester.DetailFragment.IS_GENERATE;
import static com.mastercard.labs.sng.qrscantester.DetailFragment.TAG_INSTANCE;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by kaile on 22/8/17.
 */

public class ResultsActivity extends AppCompatActivity {

    protected TextView tvResultTitle;
    protected TextView tvResultSubtitle;
    protected TextView navTitle;
    protected ImageView navIcon;
    protected ImageView statusIcon;

    protected RecyclerView tagRecycler;
    private ResultsAdapter resultsAdapter;

    protected static String BUNDLE_PPDATA = "PP_DATA";
    protected static String BUNDLE_EXCEPTIONDETAILS = "EXCEPTION_DETAILS";
    protected static String BUNDLE_PAYMENTDATA = "PAYMENT_DATA";
    protected static String type = "";

    private ArrayList<Tag> tagList = new ArrayList<>();
    protected PushPaymentData qrData;
    protected ExceptionDetails exceptionDetails;

    protected boolean scanError;
    protected boolean generateError;

    protected PaymentData paymentData;
    protected Call<PaymentResponse> paymentRequest;
    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            qrData = (PushPaymentData) savedInstanceState.getSerializable(BUNDLE_PPDATA);
            exceptionDetails = (ExceptionDetails) savedInstanceState.getSerializable(BUNDLE_EXCEPTIONDETAILS);
            paymentData = (PaymentData) savedInstanceState.getSerializable(BUNDLE_PAYMENTDATA);
        } else {
            qrData = (PushPaymentData) getIntent().getSerializableExtra(BUNDLE_PPDATA);
            exceptionDetails = (ExceptionDetails) getIntent().getSerializableExtra(BUNDLE_EXCEPTIONDETAILS);
            paymentData = (PaymentData) getIntent().getSerializableExtra(BUNDLE_PAYMENTDATA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putSerializable(BUNDLE_PPDATA, qrData);
        bundle.putSerializable(BUNDLE_EXCEPTIONDETAILS, exceptionDetails);
        bundle.putSerializable(BUNDLE_PAYMENTDATA, paymentData);
    }

    protected void initialiseFields() {
        tvResultTitle = (TextView) findViewById(R.id.outcome_title);
        tvResultSubtitle = (TextView) findViewById(R.id.outcome_subtitle);
        navTitle = (TextView) findViewById(R.id.btn_name);
        navIcon = (ImageView) findViewById(R.id.btn_icon);
        statusIcon = (ImageView) findViewById(R.id.outcome_icon);
        initializeRecycler();
    }

    private void initializeRecycler() {

        tagRecycler = (RecyclerView) findViewById(R.id.rv_tags);
        resultsAdapter = new ResultsAdapter(tagList, new ResultsAdapter.TagClickListener() {
            @Override
            public void onItemClick(Tag item) {
                openDetailResult(item);
            }

        });

        tagRecycler.setFocusable(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        tagRecycler.setLayoutManager(mLayoutManager);

        tagRecycler.setAdapter(resultsAdapter);

        if (qrData != null) {
            prepareTagData();
        }
    }

    /**
     * Begins activity to display details for selected tag in list.
     *
     * @param tag tag that has been selected
     */
    public void openDetailResult(Tag tag) {
        Intent openDetailResultIntent = new Intent(this, DetailResultActivity.class);
        Bundle bundle = new Bundle();

        if (type == "SCAN") {
            bundle.putSerializable(IS_GENERATE, false);
        } else if (type == "GENERATE") {
            bundle.putSerializable(IS_GENERATE, true);
        }
        bundle.putSerializable(TAG_INSTANCE, tag);
        openDetailResultIntent.putExtras(bundle);
        startActivity(openDetailResultIntent);
    }

    /**
     * Derives tag objects from data of QRCode.
     */
    private void prepareTagData() {

        tagList.clear();

        // value of current tag being checked.
        Serializable currentTagValue;

        // stores tag number of sub-tag with error (if any)
        String erroneousSubtag = null;

        // indicates if current tag being initialized has an error due to an invalid value.
        boolean isInvalid;

        // indicates if current tag being initialized has an error.
        boolean hasError;

        if (exceptionDetails != null) {
            erroneousSubtag = exceptionDetails.getSubTagNumber();
        }

        for (PPTag tag : PPTag.values()) {
            currentTagValue = qrData.getValue(tag);
            if (currentTagValue != null) {

                if (isAnErrorTag(tag.getTag())) {
                    hasError = true;
                    isInvalid = exceptionDetails.isInvalid();
                } else {
                    hasError = false;
                    isInvalid = false;
                }

                if (currentTagValue instanceof AbstractDataModel) {
                    initializeTagWithSubDataModel(currentTagValue, tag, erroneousSubtag, isInvalid, hasError);
                } else
                    tagList.add(new Tag(this, tag.name(), tag.getTag(), (String) currentTagValue, Tag.PP_TAG, isInvalid, hasError));
            }
        }

        resultsAdapter.notifyDataSetChanged();
    }

    /**
     * Initializes a SubDataModelTag accordingly to error detected.
     * @param currentTagValue tag value of current tag
     * @param ppTag tag being checked against
     * @param erroneousSubtag number of sub-tag with error (if any)
     * @param hasError true if exception detected
     * @param isInvalid true if error is due to an invalid value
     */
    private void initializeTagWithSubDataModel(Serializable currentTagValue, PPTag ppTag, String erroneousSubtag, boolean isInvalid, boolean hasError) {
        if (hasError) {
            if (exceptionDetails.getRootTag() != null) {
                // if error is of a multi-enclosed subdata, save with innerSubTagwithError (eg PPTAG 62 > SUB-TAG 50 > SUB-TAG 00)
                tagList.add(new SubDataModelTag(this, (AbstractDataModel) currentTagValue, ppTag.name(), ppTag.getTag(), Tag.PP_TAG, exceptionDetails.getRootTag(), erroneousSubtag, isInvalid, hasError));
            } else {
                // if error is of a single-enclosed subdata, save without innerSubTagwithError (eg: PPTAG62 > SUB-TAG 50)
                tagList.add(new SubDataModelTag(this, (AbstractDataModel) currentTagValue, ppTag.name(), ppTag.getTag(), Tag.PP_TAG, erroneousSubtag, isInvalid, hasError));
            }
        } else {
            tagList.add(new SubDataModelTag(this, (AbstractDataModel) currentTagValue, ppTag.name(), ppTag.getTag(), Tag.PP_TAG));
        }
    }

    /**
     * Checks if a tag number is an erroneous tag.
     * @param number tag number to check for.
     */
    private boolean isAnErrorTag(String number) {
        if (exceptionDetails != null) {
            for (String s : exceptionDetails.getTagNumbers()) {
                if (number.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Updates view of displayed activity accordingly to existence of errors and type of activity.
     */
    protected void updateStatus() {
        if (exceptionDetails == null) {
            statusIcon.setImageDrawable(getResources().getDrawable(R.drawable.tick));
            tvResultTitle.setText(type + " " + getResources().getString(R.string.success_title));
            tvResultSubtitle.setText(getResources().getString(R.string.success_subtitle));
            navTitle.setTextColor(getResources().getColor(R.color.colorAccent));

        } else {
            statusIcon.setImageDrawable(getResources().getDrawable(R.drawable.cross));
            tvResultTitle.setText(type + " " + getResources().getString(R.string.fail_title));
            tvResultSubtitle.setText(exceptionDetails.getErrorMessage());
        }

        if (type.equals("SCAN")) {
            if (generateError) {
                navIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_arrow_right));
                navTitle.setTextColor(getResources().getColor(R.color.colorError));
                if (!scanError)
                    tvResultSubtitle.setText(getResources().getString(R.string.success_exception));
            } else {
                navIcon.setImageDrawable(getResources().getDrawable(R.drawable.nav_arrow_right));
                navTitle.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        } else if (type.equals("GENERATE")) {
            if (scanError) {
                navIcon.setImageDrawable(getResources().getDrawable(R.drawable.error_arrow_left));
                navTitle.setTextColor(getResources().getColor(R.color.colorError));
            } else {
                navIcon.setImageDrawable(getResources().getDrawable(R.drawable.nav_arrow_left));
                navTitle.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    protected void showProcessingPaymentLoading() {
        hideProcessingPaymentLoading();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.processing_payment_message));
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    protected void hideProcessingPaymentLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onGoBack(View view) {
        finish();
    }

}
