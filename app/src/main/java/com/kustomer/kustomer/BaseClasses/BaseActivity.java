package com.kustomer.kustomer.BaseClasses;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.kustomer.kustomer.R;
import com.kustomer.kustomer.Receivers.NetworkStateReceiver;


public class BaseActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    //region Properties
    public ProgressDialog progressDialog;
    private NetworkStateReceiver networkStateReceiver;
    protected Toolbar toolbar;
    View internetStatusView;
    //endregion

    //region Activity LifeCycle
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.com_kustomer_processing));
        progressDialog.setCancelable(false);


        networkStateReceiver = new NetworkStateReceiver();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        networkStateReceiver.addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkStateReceiver.removeListener(this);
        unregisterReceiver(networkStateReceiver);
    }

    //endregion

    //region Methods
    private void initViews() {
        internetStatusView = findViewById(R.id.internet_status_view);
    }

    protected void setLayout(int layoutId, int toolbarId, String title, boolean homeEnabled) {
        setContentView(layoutId);
        setupToolbar(title, toolbarId, homeEnabled);
    }

    protected void setLayout(int layoutId) {
        setContentView(layoutId);
    }

    private void setupToolbar(String title, int toolbarId, boolean enabled) {
        toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
        if (!title.isEmpty())
            setTitle(title);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
    }

    protected void showSnackBar(View view, int textId, int colorId) {
        Snackbar snackbar = Snackbar
                .make(view, textId, Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(getResources().getColor(colorId));

        snackbar.show();
    }

    protected void showSnackBar(View view, String text, int colorId) {
        Snackbar snackbar = Snackbar
                .make(view, text, Snackbar.LENGTH_LONG);

        snackbar.getView().setBackgroundColor(getResources().getColor(colorId));

        snackbar.show();
    }
    //endregion

    //region Callbacks
    @Override
    public void networkAvailable() {
        if (internetStatusView != null && internetStatusView.getVisibility() == View.VISIBLE) {
            internetStatusView.setVisibility(View.GONE);
            internetStatusView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        }
    }

    @Override
    public void networkUnavailable() {
        if (internetStatusView != null) {
            internetStatusView.setVisibility(View.VISIBLE);
            internetStatusView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_down));
        }
    }
    //endregion
}
