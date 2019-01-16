package com.kustomer.kustomersdk.ViewHolders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KUSSelectedValueViewHolder extends RecyclerView.ViewHolder {

    //region Properties
    @BindView(R2.id.tvSelectedValue)
    TextView tvSelectedValue;
    @BindView(R2.id.verticalSeparator)
    View verticalSeparator;

    boolean isSelected = false;
    //endregion

    //region Lifecycle
    public KUSSelectedValueViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
    //endregion

    //region Public Methods
    public void onBind(final int position, String value, boolean isFirst, boolean isLast, final onItemClickListener listener){
        tvSelectedValue.setText(value);
        isSelected = isLast;

        if(isLast) {
            tvSelectedValue.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    R.color.kusInputBarHighlightedTextColor));
        }
        else{
            tvSelectedValue.setTextColor(ContextCompat.getColor(itemView.getContext(),
                    R.color.kusInputBarTextColor));
        }

        verticalSeparator.setVisibility(isFirst ? View.INVISIBLE : View.VISIBLE);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectedValueClicked(position);
            }
        });
    }
    //endregion

    //region Listener
    public interface onItemClickListener {
        void onSelectedValueClicked(int position);
    }
    //endregion
}
