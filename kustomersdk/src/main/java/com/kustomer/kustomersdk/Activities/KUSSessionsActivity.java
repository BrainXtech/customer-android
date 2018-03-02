package com.kustomer.kustomersdk.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.SessionListAdapter;
import com.kustomer.kustomersdk.BaseClasses.BaseActivity;
import com.kustomer.kustomersdk.DataSources.KUSChatSessionsDataSource;
import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Views.KUSToolbar;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

public class KUSSessionsActivity extends BaseActivity implements KUSPaginatedDataSourceListener, SessionListAdapter.onItemClickListener, KUSToolbar.OnToolbarItemClickListener {

    //region Properties
    @BindView(R2.id.rvSessions)
    RecyclerView rvSessions;
    @BindView(R2.id.btnNewConversation)
    Button btnNewConversation;

    private KUSUserSession userSession;
    private KUSChatSessionsDataSource chatSessionsDataSource;

    private boolean didHandleFirstLoad = false;
    private SessionListAdapter adapter;
    private boolean animateChatScreen = false;
    //endregion

    //region LifeCycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayout(R.layout.activity_kussessions,R.id.toolbar_main,null,false);
        super.onCreate(savedInstanceState);

        userSession = Kustomer.getSharedInstance().getUserSession();
        userSession.getPushClient().setSupportScreenShown(true);

        chatSessionsDataSource = userSession.getChatSessionsDataSource();
        chatSessionsDataSource.addListener(this);
        chatSessionsDataSource.fetchLatest();

        if(!chatSessionsDataSource.isFetched() || chatSessionsDataSource.getSize() > 1) {
            animateChatScreen = false;
        }
        else {
            animateChatScreen = true;
        }

        setupAdapter();
        setupToolbar();

        if(!getResources().getBoolean(R.bool.kusNewSessionButtonHasShadow)) {
            btnNewConversation.setElevation(0);
            btnNewConversation.setStateListAnimator(null);
        }

        if(chatSessionsDataSource.isFetched()){
            handleFirstLoadIfNecessary();
        }else{
            rvSessions.setVisibility(View.INVISIBLE);
            btnNewConversation.setVisibility(View.INVISIBLE);
            showProgressBar();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(chatSessionsDataSource != null)
            chatSessionsDataSource.fetchLatest();
    }

    @Override
    protected void onDestroy() {
        if(chatSessionsDataSource != null)
            chatSessionsDataSource.removeListener(this);

        userSession.getPushClient().setSupportScreenShown(false);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.stay, R.anim.kus_slide_down);
    }
    //endregion

    //region Initializer
    private void setupToolbar(){
        KUSToolbar kusToolbar = (KUSToolbar)toolbar;
        kusToolbar.initWithUserSession(userSession);
        kusToolbar.setShowLabel(false);
        kusToolbar.setListener(this);
        kusToolbar.setShowDismissButton(true);
    }

    private void setupAdapter(){
        adapter = new SessionListAdapter(rvSessions, chatSessionsDataSource, userSession, this);
        rvSessions.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);
        rvSessions.setLayoutManager(layoutManager);

        adapter.notifyDataSetChanged();
    }
    //endregion

    //region Private Methods
    private void handleFirstLoadIfNecessary(){
        if(didHandleFirstLoad)
            return;

        didHandleFirstLoad = true;

        if(chatSessionsDataSource != null && chatSessionsDataSource.getSize() == 0){
            Intent intent = new Intent(this, KUSChatActivity.class);
            intent.putExtra(KUSConstants.BundleName.CHAT_SCREEN_BACK_BUTTON_KEY,false);
            startActivity(intent);

            if(animateChatScreen)
                overridePendingTransition(R.anim.kus_slide_up, R.anim.stay);
            else
                overridePendingTransition(0, 0);
        }else if (chatSessionsDataSource != null && chatSessionsDataSource.getSize() == 1){
            KUSChatSession chatSession = (KUSChatSession) chatSessionsDataSource.getFirst();

            Intent intent = new Intent(this, KUSChatActivity.class);
            intent.putExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE_KEY,chatSession);
            startActivity(intent);

            if(animateChatScreen)
                overridePendingTransition(R.anim.kus_slide_up, R.anim.stay);
            else
                overridePendingTransition(0, 0);
        }

    }
    //endregion

    //region Listeners
    @Optional @OnClick(R2.id.btnRetry)
    void userTappedRetry(){
        chatSessionsDataSource.fetchLatest();
        showProgressBar();
    }

    @OnClick(R2.id.btnNewConversation)
    void newConversationClicked(){
        Intent intent = new Intent(this, KUSChatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.kus_slide_left, R.anim.stay);
    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                handleFirstLoadIfNecessary();
                rvSessions.setVisibility(View.VISIBLE);
                btnNewConversation.setVisibility(View.VISIBLE);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                String errorText = getResources().getString(R.string.something_went_wrong_please_try_again);
                showErrorWithText(errorText);
                rvSessions.setVisibility(View.INVISIBLE);
                btnNewConversation.setVisibility(View.INVISIBLE);
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onContentChange(final KUSPaginatedDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                adapter.setData((KUSChatSessionsDataSource) dataSource);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void onSessionItemClicked(KUSChatSession chatSession) {
        Intent intent = new Intent(this, KUSChatActivity.class);
        intent.putExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE_KEY,chatSession);
        startActivity(intent);
        overridePendingTransition(R.anim.kus_slide_left, R.anim.stay);
    }

    @Override
    public void onToolbarBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onToolbarClosePressed() {
        clearAllLibraryActivities();
    }
    //endregion
}
