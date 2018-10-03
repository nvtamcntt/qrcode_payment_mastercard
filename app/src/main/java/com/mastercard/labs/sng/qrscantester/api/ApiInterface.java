package com.mastercard.labs.sng.qrscantester.api;

import com.mastercard.labs.sng.qrscantester.api.request.PaymentRequest;
import com.mastercard.labs.sng.qrscantester.api.response.PaymentResponse;
import com.mastercard.labs.sng.qrscantester.model.ListSimpleResponese;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;

public interface ApiInterface {

    @HTTP(method = "POST", path = "/v1.0/merchantTransferFundingAndPayment", hasBody = true)
    Call<PaymentResponse> makePayment(@Body PaymentRequest request);

    @GET("/v1.0")
    Call<ListSimpleResponese> makeIndex();
}
