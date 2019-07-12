package com.kustomer.kustomersdk.Adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.kustomer.kustomersdk.Interfaces.KUSBitmapListener;
import com.kustomer.kustomersdk.Models.KUSBitmap;
import com.kustomer.kustomersdk.R;
import com.kustomer.kustomersdk.ViewHolders.ImageAttachmentViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/19/2018.
 */

public class ImageAttachmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ImageAttachmentViewHolder.ImageAttachmentListener {

    //region Properties
    @NonNull
    private List<KUSBitmap> imageBitmaps;
    @NonNull
    private onItemClickListener mListener;
    //endregion

    //region LifeCycle
    public ImageAttachmentListAdapter(@NonNull onItemClickListener listener) {
        imageBitmaps = new ArrayList<>();
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
        ((ImageAttachmentViewHolder) holder).onBind(imageBitmaps.get(position), this);
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public void attachImage(@NonNull final KUSBitmap kusBitmap) {
        imageBitmaps.add(kusBitmap);

        kusBitmap.addListener(new KUSBitmapListener() {
            @Override
            public void onBitmapCreated() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(imageBitmaps.indexOf(kusBitmap));
                    }
                });
            }

            @Override
            public void onMemoryError(@NonNull OutOfMemoryError memoryError) {
                for (KUSBitmap kusBitmap : imageBitmaps) {
                    if (kusBitmap.getBitmap() != null) {
                        kusBitmap.getBitmap().recycle();
                        kusBitmap.setBitmap(null);
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        removeAll();
                    }
                });
            }
        });

        notifyItemRangeInserted(imageBitmaps.indexOf(kusBitmap), 1);
    }

    public void removeAll() {
        imageBitmaps.clear();
        notifyDataSetChanged();
    }

    @NonNull
    public List<KUSBitmap> getImageBitmaps() {
        return imageBitmaps;
    }

    @NonNull
    private List<String> getImageUris() {
        List<String> imageUris = new ArrayList<>();
        for (KUSBitmap kusBitmap : imageBitmaps) {
            if (kusBitmap.getBitmap() != null)
                imageUris.add(kusBitmap.getUri());
        }
        return imageUris;
    }

    //endreigon

    //region Callbacks
    @Override
    public void onImageCancelClicked(@Nullable KUSBitmap imageBitmap) {
        int pos = imageBitmaps.indexOf(imageBitmap);

        imageBitmaps.remove(imageBitmap);
        notifyItemRemoved(pos);
        mListener.onAttachmentImageRemoved();
    }

    @Override
    public void onImageTapped(int position) {
        mListener.onAttachmentImageClicked(position, getImageUris());
    }
    //endregion

    //region Listener
    public interface onItemClickListener {
        void onAttachmentImageClicked(int position, @NonNull List<String> imageURIs);

        void onAttachmentImageRemoved();
    }
    //endregion
}
