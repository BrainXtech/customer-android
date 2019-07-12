package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Adapters.ImageAttachmentListAdapter;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.Helpers.KUSPermission;
import com.kustomer.kustomersdk.Interfaces.KUSBitmapListener;
import com.kustomer.kustomersdk.Interfaces.KUSInputBarTextChangeListener;
import com.kustomer.kustomersdk.Interfaces.KUSInputBarViewListener;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSBitmap;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Junaid on 2/27/2018.
 */

public class KUSInputBarView extends LinearLayout implements TextWatcher, TextView.OnEditorActionListener,
        ImageAttachmentListAdapter.onItemClickListener, KUSObjectDataSourceListener {

    @BindView(R2.id.etTypeMessage)
    EditText etTypeMessage;
    @BindView(R2.id.btnSendMessage)
    View btnSendMessage;
    @BindView(R2.id.ivAttachment)
    ImageView ivAttachment;
    @BindView(R2.id.rvImageAttachment)
    RecyclerView rvImageAttachment;

    @Nullable
    KUSInputBarTextChangeListener textChangeListener;

    @Nullable
    KUSInputBarViewListener listener;
    @Nullable
    ImageAttachmentListAdapter adapter;
    @Nullable
    KUSUserSession mUserSession;

    @Nullable
    Handler handler;

    private int imageProcessingCount = 0;

    //endregion

    //region LifeCycle
    public KUSInputBarView(@NonNull Context context) {
        super(context);
    }

    public KUSInputBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSInputBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KUSInputBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        initViews();
        setListeners();
        setupAdapter();
    }
    //endregion

    //region Initializer
    private void initViews() {
        updateSendButton();

        etTypeMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
        etTypeMessage.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        showKeyboard();
    }

    private void setListeners() {
        etTypeMessage.addTextChangedListener(this);
        etTypeMessage.setOnEditorActionListener(this);
    }

    private void setupAdapter() {
        adapter = new ImageAttachmentListAdapter(this);
        rvImageAttachment.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvImageAttachment.setLayoutManager(layoutManager);

        adapter.notifyDataSetChanged();
    }
    //endregion

    //region Private Methods

    private void showKeyboard() {
        handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                KUSUtils.showKeyboard(etTypeMessage);
            }
        };
        handler.postDelayed(runnable, 800);
    }

    private void updatePlaceHolder() {
        if (mUserSession != null && !mUserSession.getScheduleDataSource().isActiveBusinessHours()) {
            etTypeMessage.setHint(String.format("%s%s",
                    getResources().getString(R.string.com_kustomer_leave_a_message), "…"));
        } else {
            etTypeMessage.setHint(getResources().getString(R.string.com_kustomer_type_a_message___));
        }
    }

    private boolean isSendEnabled() {
        String text = getText();
        return ((adapter != null && adapter.getItemCount() > 0) || text.length() > 0)
                && imageProcessingCount == 0;
    }
    //endregion

    //region Public Methods
    public void initWithUserSession(@Nullable KUSUserSession userSession) {
        this.mUserSession = userSession;
        if (userSession != null) {
            userSession.getChatSettingsDataSource().addListener(this);
            userSession.getScheduleDataSource().addListener(this);
        }
        updatePlaceHolder();
    }

    public void setListener(@Nullable KUSInputBarViewListener listener) {
        this.listener = listener;
    }

    public void setTextChangeListener(@Nullable KUSInputBarTextChangeListener listener) {
        textChangeListener = listener;
    }

    public void setText(@NonNull String text) {
        etTypeMessage.setText(text.trim());
    }

    @NonNull
    public String getText() {
        return etTypeMessage.getText().toString().trim();
    }

    public void setAllowsAttachment(boolean allowAttachment) {
        if (!allowAttachment)
            ivAttachment.setVisibility(GONE);
        else {
            boolean shouldBeHidden = !KUSPermission.isCameraPermissionDeclared(getContext())
                    && !KUSPermission.isReadPermissionDeclared(getContext());

            ivAttachment.setVisibility(shouldBeHidden ? GONE : VISIBLE);
        }
    }

    public void removeAllAttachments() {
        if (adapter != null)
            adapter.removeAll();
        updateSendButton();
    }

    public void attachImage(@NonNull String imageUri, @NonNull final MemoryListener memoryListener) {
        if (adapter == null)
            return;

        imageProcessingCount++;
        updateSendButton();

        adapter.attachImage(new KUSBitmap(imageUri, new KUSBitmapListener() {
            @Override
            public void onBitmapCreated() {
                imageProcessingCount--;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        updateSendButton();
                    }
                });
            }

            @Override
            public void onMemoryError(@NonNull final OutOfMemoryError memoryError) {
                imageProcessingCount--;
                memoryListener.onOutOfMemoryError(memoryError);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        updateSendButton();

                    }
                });
            }
        }));

        if (adapter.getItemCount() == 1)
            rvImageAttachment.setVisibility(VISIBLE);

        rvImageAttachment.scrollToPosition(adapter.getItemCount() - 1);
    }

    @NonNull
    public List<KUSBitmap> getKUSBitmapList() {
        return adapter != null ?
                adapter.getImageBitmaps()
                : new ArrayList<KUSBitmap>();
    }

    public void requestInputFocus() {
        etTypeMessage.requestFocus();
    }

    public void clearInputFocus() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        etTypeMessage.clearFocus();
        KUSUtils.hideKeyboard(this);
    }
    //endregion

    //region Interface element methods
    @OnClick(R2.id.ivAttachment)
    void attachmentClicked() {
        if (listener != null)
            listener.inputBarAttachmentClicked();
    }

    @OnClick(R2.id.btnSendMessage)
    void sendPressed() {
        if (!isSendEnabled())
            return;

        if (listener != null)
            listener.inputBarSendClicked();
    }

    private void updateSendButton() {
        boolean shouldEnableSend = isSendEnabled();

        if (listener != null)
            shouldEnableSend = shouldEnableSend && listener.inputBarShouldEnableSend();

        btnSendMessage.setEnabled(shouldEnableSend);
        btnSendMessage.setAlpha(shouldEnableSend ? 1.0f : 0.5f);
    }
    //endregion

    //region Listeners
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        updateSendButton();

        if (textChangeListener != null)
            textChangeListener.inputBarTextChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEND) {
            if (listener != null && listener.inputBarShouldEnableSend())
                sendPressed();
            else return true;
        }
        return false;
    }

    @Override
    public void onAttachmentImageClicked(int position, @NonNull List<String> imageURIs) {
        new KUSLargeImageViewer(getContext()).showImages(imageURIs, position);
    }

    @Override
    public void onAttachmentImageRemoved() {
        if (adapter == null || adapter.getItemCount() == 0)
            rvImageAttachment.setVisibility(GONE);
        updateSendButton();
    }

    @Override
    public void objectDataSourceOnLoad(@NonNull KUSObjectDataSource dataSource) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updatePlaceHolder();
            }
        };
        handler.post(runnable);
    }

    @Override
    public void objectDataSourceOnError(@NonNull KUSObjectDataSource dataSource, @Nullable Error error) {

    }
    //endregion

    //region Listener

    public interface MemoryListener {
        void onOutOfMemoryError(OutOfMemoryError error);
    }

    //endregion
}
