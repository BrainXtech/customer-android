package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.kustomer.kustomersdk.Adapters.KUSMLSelectedValueListAdapter;
import com.kustomer.kustomersdk.Interfaces.KUSMLFormValuesPickerViewListener;
import com.kustomer.kustomersdk.Interfaces.KUSOptionPickerViewListener;
import com.kustomer.kustomersdk.Models.KUSMLNode;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.ViewHolders.KUSSelectedValueViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KUSMLFormValuesPickerView extends LinearLayout implements KUSOptionPickerViewListener, KUSSelectedValueViewHolder.onItemClickListener {
    //region Properties
    @BindView(R2.id.mlFormOptionPicker)
    KUSOptionsPickerView optionsPickerView;
    @BindView(R2.id.btnSendMessage)
    View btnSendMessage;
    @BindView(R2.id.rvMlSelectedValues)
    RecyclerView rvMlSelectedValues;

    private ArrayList<KUSMLNode> valuesTree;
    private ArrayList<KUSMLNode> currentOptionsToShow;
    private ArrayList<KUSMLNode> selectedValuesStack;
    private ArrayList<String> currentOptionsValues;

    private boolean isLastNodeRequired;
    private KUSMLSelectedValueListAdapter adapter;
    private KUSMLFormValuesPickerViewListener listener;
    //endregion

    //region LifeCycle
    public KUSMLFormValuesPickerView(Context context) {
        super(context);
    }

    public KUSMLFormValuesPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSMLFormValuesPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KUSMLFormValuesPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        optionsPickerView.setListener(this);
        setupAdapter();
    }
    //endregion

    //region Public Methods
    public void setOptionPickerMaxHeight(int px){
        optionsPickerView.setMaxHeight(px);
    }

    public void setMlFormValues(ArrayList<KUSMLNode> valuesTree, boolean isLastNodeRequired){
        this.valuesTree = new ArrayList<>(valuesTree);
        this.currentOptionsToShow = new ArrayList<>(valuesTree);
        selectedValuesStack = new ArrayList<>();
        this.isLastNodeRequired = isLastNodeRequired;
        showCurrentOptionsAndUpdateView();
    }

    public void setListener(KUSMLFormValuesPickerViewListener listener){
        this.listener = listener;
    }
    //endregion

    //region Private Methods
    private void setupAdapter() {
        adapter = new KUSMLSelectedValueListAdapter(getContext(),this);
        rvMlSelectedValues.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMlSelectedValues.setLayoutManager(layoutManager);
    }

    private void updateSendButton(){

        boolean shouldEnableSend = false;

        if(selectedValuesStack.size() != 0) {
            shouldEnableSend = !isLastNodeRequired
                    || selectedValuesStack.get(selectedValuesStack.size() - 1).getChildNodes().size() == 0;
        }

        btnSendMessage.setEnabled(shouldEnableSend);
        btnSendMessage.setAlpha(shouldEnableSend ? 1.0f : 0.5f);
    }

    private void showCurrentOptionsAndUpdateView(){
        if(currentOptionsToShow.size() == 0){
            optionsPickerView.setVisibility(GONE);
        }else{
            optionsPickerView.setVisibility(VISIBLE);
            currentOptionsValues = new ArrayList<>(currentOptionsToShow.size());

            for (KUSMLNode node : currentOptionsToShow) {
                currentOptionsValues.add(node.getDisplayName());
            }

            optionsPickerView.setOptions(currentOptionsValues);
        }

        updateSendButton();
        adapter.setSelectedValuesStack(selectedValuesStack);
        adapter.notifyDataSetChanged();

        if(selectedValuesStack.size() > 0) {
            rvMlSelectedValues.smoothScrollToPosition(selectedValuesStack.size());
        }
    }
    //endregion

    //region Listeners
    @OnClick(R2.id.btnSendMessage)
    void sendPressed() {
        if(listener != null){
            listener.mlFormValueSelected(
                    selectedValuesStack.get(selectedValuesStack.size()-1).getDisplayName(),
                    selectedValuesStack.get(selectedValuesStack.size()-1).getNodeId());
        }
    }
    //endregion

    //region Callbacks
    @Override
    public void optionPickerOnOptionSelected(String option) {
        int optionIndex = optionsPickerView.getOptions().indexOf(option);

        if(optionIndex != -1 && optionIndex < currentOptionsToShow.size()){
            selectedValuesStack.add(currentOptionsToShow.get(optionIndex));
        }

        currentOptionsToShow.clear();
        if(selectedValuesStack.size() > 0
                && selectedValuesStack.get(selectedValuesStack.size() - 1).getChildNodes().size() > 0){
            currentOptionsToShow = new ArrayList<>(selectedValuesStack.get(selectedValuesStack.size()-1)
                    .getChildNodes());
        }

        showCurrentOptionsAndUpdateView();

    }

    @Override
    public void onSelectedValueClicked(int position) {
        if(selectedValuesStack.size() == 0) return;

        selectedValuesStack = new ArrayList<>(selectedValuesStack.subList(0,
                position));

        if(position == 0){
            currentOptionsToShow = new ArrayList<>(valuesTree);
        }else {
            currentOptionsToShow = new ArrayList<>(selectedValuesStack.get(position-1).getChildNodes());
        }

        showCurrentOptionsAndUpdateView();
    }
    //endregion
}
