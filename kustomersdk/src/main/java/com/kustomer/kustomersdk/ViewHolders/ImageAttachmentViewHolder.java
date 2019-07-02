package com.kustomer.kustomersdk.ViewHolders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.kustomer.kustomersdk.Models.KUSBitmap;
import com.kustomer.kustomersdk.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Junaid on 1/19/2018.
 */

public class ImageAttachmentViewHolder extends RecyclerView.ViewHolder {
    //region Properties
    @BindView(R2.id.ivAttachment)
    ImageView ivAttachment;
    @BindView(R2.id.ivRemoveImage)
    ImageView ivRemoveImage;
    //endregion

    //region Methods
    public ImageAttachmentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void onBind(@Nullable final KUSBitmap kusBitmap,
                       @NonNull final ImageAttachmentListener listener) {

        if (kusBitmap != null)
            ivAttachment.setImageBitmap(kusBitmap.getBitmap());
        else
            ivAttachment.setImageBitmap(null);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = getAdapterPosition();
                if (index >= 0)
                    listener.onImageTapped(index);
            }
        });

        ivRemoveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onImageCancelClicked(kusBitmap);
            }
        });
    }
    //endregion

    //region Interface
    public interface ImageAttachmentListener {
        void onImageCancelClicked(@Nullable KUSBitmap imageBitmap);

        void onImageTapped(int index);
    }
    //endregion

}
