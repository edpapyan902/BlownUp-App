package com.BlownUp.app.screen.startup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.BlownUp.app.BaseActivity;
import com.BlownUp.app.MainApplication;
import com.BlownUp.app.R;
import com.BlownUp.app.global.Const;
import com.BlownUp.app.network.API;
import com.BlownUp.app.screen.activity.SuccessActivity;
import com.BlownUp.app.store.Store;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.GooglePayConfig;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.paymentsheet.ui.GooglePayButton;
import com.stripe.android.view.CardMultilineWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckOutActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQ_CODE_LOAD_PAYMENT_DATA = 53;

    private ScrollView scrollView;
    private CardMultilineWidget cardMultiLineWidget;
    private Button btnRegister;
    private GooglePayButton btnGooglePay;
    private LinearLayout progressLayout;
    private TextView txtAmount, txtCurrency;
    private ProgressBar loader;

    private PaymentsClient paymentsClient;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        initLayout();
        isReadyToPay();
    }

    private void initLayout() {
        scrollView = findViewById(R.id.scrollView);
        scrollView.setSmoothScrollingEnabled(true);

        cardMultiLineWidget = findViewById(R.id.cardMultiLineWidget);

        progressLayout = findViewById(R.id.progressLayout);
        progressLayout.setOnClickListener(this);
        loader = findViewById(R.id.loader);
        Sprite sprite = new FadingCircle();
        loader.setIndeterminateDrawable(sprite);

        txtAmount = findViewById(R.id.txtAmount);
        txtCurrency = findViewById(R.id.txtCurrency);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        btnGooglePay = findViewById(R.id.btnGooglePay);
        btnGooglePay.setOnClickListener(this);
        btnGooglePay.setEnabled(false);

        txtCurrency.setText(R.string.currency_usd);
        txtAmount.setText("9.99");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                if (cardMultiLineWidget.validateAllFields())
                    createPaymentMethod(cardMultiLineWidget.getPaymentMethodCreateParams(), false);
                else
                    showToast(getString(R.string.invalid_card));
                break;
            case R.id.btnGooglePay:
                try {
                    payWithGoogle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void createPaymentMethod(PaymentMethodCreateParams paymentMethodCreateParams, boolean isGooglePay) {
        progressLayout.setVisibility(View.VISIBLE);

        stripe.createPaymentMethod(paymentMethodCreateParams, new ApiResultCallback<PaymentMethod>() {
            @Override
            public void onSuccess(@NotNull PaymentMethod paymentMethod) {
                checkout(paymentMethod.id, isGooglePay);
            }

            @Override
            public void onError(@NotNull Exception e) {
                progressLayout.setVisibility(View.INVISIBLE);
                showToast("Please use valid card.");

                e.printStackTrace();
            }
        });
    }

    private void checkout(String payment_method, boolean isGooglePay) {
        JSONObject params = new JSONObject();
        try {
            params.put("payment_method", payment_method);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String token = MainApplication.getUser(this).token;
        API.POST(token, Const.CHECKOUT_CHARGE_URL, params, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressLayout.setVisibility(View.INVISIBLE);
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        cardMultiLineWidget.clear();

                        Store.setBoolean(CheckOutActivity.this, Const.CHARGED, true);

                        JSONObject json_data = response.getJSONObject("data");
                        String client_secret = json_data.getString("client_secret");

                        if (isGooglePay) {
                            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                                    .createWithPaymentMethodId(
                                            payment_method,
                                            client_secret
                                    );
                            stripe.confirmPayment(CheckOutActivity.this, confirmParams, null);
                        }

                        Intent intent = new Intent(CheckOutActivity.this, SuccessActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    String message = response.getString("message");
                    if (!TextUtils.isEmpty(message))
                        showToast(message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                progressLayout.setVisibility(View.INVISIBLE);
                anError.printStackTrace();
            }
        });
    }

    private void isReadyToPay() {
        stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());
        paymentsClient = Wallet.getPaymentsClient(
                this,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(Const.LIVE_PAYMENT ? WalletConstants.ENVIRONMENT_PRODUCTION : WalletConstants.ENVIRONMENT_TEST)
                        .build()
        );

        final IsReadyToPayRequest request;
        try {
            request = createIsReadyToPayRequest();
            paymentsClient.isReadyToPay(request)
                    .addOnCompleteListener(
                            task -> {
                                btnGooglePay.setEnabled(task.isSuccessful());
                            }
                    );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private IsReadyToPayRequest createIsReadyToPayRequest() throws JSONException {
        final JSONArray allowedAuthMethods = new JSONArray();
        allowedAuthMethods.put("PAN_ONLY");
        allowedAuthMethods.put("CRYPTOGRAM_3DS");

        final JSONArray allowedCardNetworks = new JSONArray();
        allowedCardNetworks.put("AMEX");
        allowedCardNetworks.put("DISCOVER");
        allowedCardNetworks.put("MASTERCARD");
        allowedCardNetworks.put("VISA");

        final JSONObject cardParameters = new JSONObject();
        cardParameters.put("allowedAuthMethods", allowedAuthMethods);
        cardParameters.put("allowedCardNetworks", allowedCardNetworks);

        final JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        cardPaymentMethod.put("parameters", cardParameters);

        final JSONArray allowedPaymentMethods = new JSONArray();
        allowedPaymentMethods.put(cardPaymentMethod);

        final JSONObject isReadyToPayRequestJson = new JSONObject();
        isReadyToPayRequestJson.put("apiVersion", 2);
        isReadyToPayRequestJson.put("apiVersionMinor", 0);
        isReadyToPayRequestJson.put("allowedPaymentMethods", allowedPaymentMethods);

        return IsReadyToPayRequest.fromJson(isReadyToPayRequestJson.toString());
    }

    private void payWithGoogle() throws Exception {
        AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(createPaymentDataRequest()),
                this,
                REQ_CODE_LOAD_PAYMENT_DATA
        );
    }

    @NonNull
    private PaymentDataRequest createPaymentDataRequest() throws Exception {
        final JSONObject tokenizationSpec =
                new GooglePayConfig(this).getTokenizationSpecification();
        final JSONObject cardPaymentMethod = new JSONObject()
                .put("type", "CARD")
                .put(
                        "parameters",
                        new JSONObject()
                                .put("allowedAuthMethods", new JSONArray()
                                        .put("PAN_ONLY")
                                        .put("CRYPTOGRAM_3DS"))
                                .put("allowedCardNetworks",
                                        new JSONArray()
                                                .put("AMEX")
                                                .put("DISCOVER")
                                                .put("MASTERCARD")
                                                .put("VISA"))

                                // require billing address
                                .put("billingAddressRequired", true)
                                .put("billingAddressParameters",
                                        new JSONObject()
                                                // require full billing address
                                                .put("format", "MIN")

                                                // require phone number
                                                .put("phoneNumberRequired", true)
                                )
                )
                .put("tokenizationSpecification", tokenizationSpec);

        // create PaymentDataRequest
        final JSONObject paymentDataRequest = new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0)
                .put("allowedPaymentMethods", new JSONArray().put(cardPaymentMethod))
                .put("transactionInfo", new JSONObject()
                        .put("totalPrice", "9.99")
                        .put("totalPriceStatus", "FINAL")
                        .put("currencyCode", "USD")
                )
                .put("merchantInfo", new JSONObject()
                        .put("merchantId", Const.GOOGLE_MERCHANT_ID)
                        .put("merchantName", "BlownUp"))

                // require email address
                .put("emailRequired", true);

        return PaymentDataRequest.fromJson(String.valueOf(paymentDataRequest));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_LOAD_PAYMENT_DATA:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null) {
                            try {
                                onGooglePayResult(data);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                break;
        }
    }

    private void onGooglePayResult(@NonNull Intent data) throws JSONException {
        final PaymentData paymentData = PaymentData.getFromIntent(data);
        if (paymentData == null) {
            return;
        }

        final PaymentMethodCreateParams paymentMethodCreateParams =
                PaymentMethodCreateParams.createFromGooglePay(
                        new JSONObject(paymentData.toJson()));

        createPaymentMethod(paymentMethodCreateParams, true);
    }
}