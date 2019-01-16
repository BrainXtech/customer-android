package com.kustomer.kustomersdk.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.ViewHolders.ImageAttachmentViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/19/2018.
 */

public class ImageAttachmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ImageAttachmentViewHolder.ImageAttachmentListener {

    //region Properties
    private List<String> imageURIs;
    private onItemClickListener mListener;
    //endregion

    //region LifeCycle
    public ImageAttachmentListAdapter(onItemClickListener listener) {
        imageURIs = new ArrayList<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageAttachmentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kus_item_image_attachment_view_holder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ImageAttachmentViewHolder) holder).onBind(imageURIs.get(position), this);
    }

    @Override
    public int getItemCount() {
        return imageURIs.size();
    }

    public void attachImage(String imageUri) {
        imageURIs.add(imageUri);
        notifyItemRangeInserted(imageURIs.indexOf(imageUri), 1);
    }

    public void removeAll() {
        imageURIs.clear();
        notifyDataSetChanged();
    }

    public List<String> getImageURIs() {
        return imageURIs;
    }
    //endreigon

    //region Callbacks
    @Override
    public void onImageCancelClicked(String imageUri) {
        int pos = imageURIs.indexOf(imageUri);

        imageURIs.remove(imageUri);
        notifyItemRemoved(pos);
        mListener.onAttachmentImageRemoved();
    }

    @Override
    public void onImageTapped(int position) {
        mListener.onAttachmentImageClicked(position, imageURIs);
    }
    //endregion

    //region Listener
    public interface onItemClickListener {
        void onAttachmentImageClicked(int position, List<String> imageURIs);

        void onAttachmentImageRemoved();
    }
    //endregion
}
