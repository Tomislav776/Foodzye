package com.example.tomipc.foodzye;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomipc.foodzye.adapter.PremiumAdapter;
import com.example.tomipc.foodzye.model.Premium;
import com.example.tomipc.foodzye.model.User;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class GetPremiumAccountActivity extends Navigation {

    private static final String TAG = "PayPal";

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    private static final String CONFIG_CLIENT_ID = "AbrnAFV2bC2jWbo8pmFYaYrcEOIe22TSOUV5LJKeYA_eeXnwa3721b_lAJEsxEbbTfCBLO6VpCxr5T6m";

    private static final int REQUEST_CODE_PAYMENT = 1;

    private PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID).acceptCreditCards(false);

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Premium> premiumList;
    private TextView textview;
    private Context c;
    UserLocalStore userLocalStore;
    User user;
    String months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_premium_account);

        c = this;

        userLocalStore = new UserLocalStore(this);
        user = userLocalStore.getLoggedInUser();

        toolbar = (Toolbar) findViewById(R.id.PremiumToolbar);
        set(toolbar);

        textview = (TextView) findViewById(R.id.textView13);
        textview.setTextColor(getResources().getColor(R.color.primary));
        textview.setText("For the duration of your premium account, your profile is featured at the top on the main screen.");

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        premiumList = getData();

        mRecyclerView = (RecyclerView) findViewById(R.id.PremiumRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PremiumAdapter(premiumList, c);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(c, mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Premium premium = premiumList.get(position);

                PayPalPayment premiumAccount = new PayPalPayment(new BigDecimal(premium.getPrice()), premium.getCurrency(), "Premium account",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                months = premium.getMonths_duration();

                Intent intent = new Intent(GetPremiumAccountActivity.this, PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, premiumAccount);

                startActivityForResult(intent, REQUEST_CODE_PAYMENT);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GetPremiumAccountActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GetPremiumAccountActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private ArrayList<Premium> getData() {
        Database db = new Database();
        return db.readPremiumTypes("getPremiumTypes");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        JSONObject JSONobject = new JSONObject(confirm.toJSONObject().toString(4));
                        JSONObject jObj = JSONobject.getJSONObject("response");
                        String create_time = jObj.getString("create_time");
                        String transaction_id = jObj.getString("id");

                        JSONObject JSONobject2 = new JSONObject(confirm.getPayment().toJSONObject().toString(4));
                        String amount = JSONobject2.getString("amount");
                        String currency_code = JSONobject2.getString("currency_code");

                        HashMap<String,String> postData = new HashMap();
                        postData.put("user_id", Integer.toString(user.getId()));
                        postData.put("transaction_id", transaction_id);
                        postData.put("create_time", create_time);
                        postData.put("amount", amount);
                        postData.put("currency_code", currency_code);
                        Database db = new Database(this);
                        db.insert(postData, "postPayment");

                        Toast.makeText(c, "Thank you for buying a premium account. Duration - " + months + " months", Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor userLocalDatabaseEditor = userLocalStore.userLocalDatabase.edit();
                        userLocalDatabaseEditor.putInt("premium", 1);
                        userLocalDatabaseEditor.commit();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GetPremiumAccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        super.onBackPressed();
    }
}
