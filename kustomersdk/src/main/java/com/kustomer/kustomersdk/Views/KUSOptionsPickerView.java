package com.kustomer.kustomersdk.Views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kustomer.kustomersdk.Interfaces.KUSOptionPickerViewListener;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;
import com.kustomer.kustomersdk.Utils.KUSUtils;
import com.nex3z.flowlayout.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Junaid on 2/27/2018.
 */

public class KUSOptionsPickerView extends LinearLayout implements View.OnClickListener {

    //region Properties
    private static int MIN_BUTTON_WIDTH_IN_DP = 100;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.optionsFlowLayout)
    FlowLayout flowLayout;

    List<String> options;
    List<TextView> optionButtons;
    KUSOptionPickerViewListener listener;

    private int maxHeight = -1;
    //endregion

    //region LifeCycle
    public KUSOptionsPickerView(Context context) {
        super(context);
    }

    public KUSOptionsPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KUSOptionsPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public KUSOptionsPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(maxHeight != -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //endregion

    //region Public Methods
    public void setMaxHeight(int px){
        maxHeight = px;
    }

    public void setOptions(List<String> options){
        this.options = options;
        if(options.size() > 0)
            progressBar.setVisibility(INVISIBLE);
        else
            progressBar.setVisibility(VISIBLE);

        rebuildOptionButtons();
    }

    public void setListener(KUSOptionPickerViewListener listener){
        this.listener = listener;
    }
    public List<String> getOptions(){
        return options;
    }
    //endregion

    //region Private Methods
    private void rebuildOptionButtons(){
        flowLayout.removeAllViews();
        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        ArrayList<TextView> optionButtons = new ArrayList<>();
        if(options != null) {
            for (String option : options){
                AppCompatTextView textView = new AppCompatTextView(getContext());
                TextViewCompat.setTextAppearance(textView,R.style.KUSOptionPickerTextAppearance);
                textView.setLayoutParams(vlp);
                textView.setText(option);
                textView.setBackgroundResource(R.drawable.kus_shape_option_view_background);
                textView.setTextColor(ContextCompat.getColor(getContext(),R.color.kusOptionPickerButtonTextColor));
                textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                textView.setSingleLine(true);
                textView.setGravity(Gravity.CENTER);
                textView.setMinWidth((int) KUSUtils.dipToPixels(getContext(),MIN_BUTTON_WIDTH_IN_DP));
                textView.setOnClickListener(this);

                optionButtons.add(textView);
                flowLayout.addView(textView);
            }

        }

        this.optionButtons = optionButtons;


    }

    @Override
    public void onClick(View view) {
        int indexOfButton = optionButtons.indexOf(view);
        if(indexOfButton >= 0){
            String option = options.get(indexOfButton);
            if(listener != null)
                listener.optionPickerOnOptionSelected(option);
        }
    }
    //endregion
}
