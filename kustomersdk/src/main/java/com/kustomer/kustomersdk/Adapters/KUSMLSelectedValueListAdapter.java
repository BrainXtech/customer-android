package com.kustomer.kustomersdk.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kustomer.kustomersdk.Models.KUSMLNode;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.ViewHolders.KUSSelectedValueViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/19/2018.
 */

public class KUSMLSelectedValueListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //region Properties
    private List<KUSMLNode> selectedValuesStack;
    private KUSSelectedValueViewHolder.onItemClickListener mListener;
    private Context context;
    //endregion

    //region LifeCycle
    public KUSMLSelectedValueListAdapter(Context context, KUSSelectedValueViewHolder.onItemClickListener listener) {
        selectedValuesStack = new ArrayList<>();
        mListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KUSSelectedValueViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kus_ml_selected_value_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(selectedValuesStack.size() == 0){
            ((KUSSelectedValueViewHolder) holder).onBind( position,
                    context.getResources().getString(R.string.com_kustomer_please_select_an_item),
                    true,false, mListener);
        }else{
            boolean isFirst = position == 0;
            boolean isLast = position == selectedValuesStack.size();

            if(isFirst){
                ((KUSSelectedValueViewHolder) holder).onBind(position,
                        context.getString(R.string.com_kustomer_home),
                        true,false, mListener);
            }else{
                ((KUSSelectedValueViewHolder) holder).onBind(position,
                        selectedValuesStack.get(position-1).getDisplayName(),
                        false,isLast, mListener);
            }
        }
    }

    @Override
    public int getItemCount() {
        return selectedValuesStack.size() > 0 ? selectedValuesStack.size() + 1 : 1;
    }
    //endregion

    //region Public methods
    public void setSelectedValuesStack(List<KUSMLNode> selectedValuesStack){
        this.selectedValuesStack = selectedValuesStack;
    }
    //endregion
}
