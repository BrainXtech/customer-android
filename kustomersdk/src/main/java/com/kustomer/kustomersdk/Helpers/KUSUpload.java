package com.kustomer.kustomersdk.Helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSImageUploadListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatAttachment;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUpload {

    //region Properties
    private boolean sendingComplete = false;
    private int uploadedCount = 0;
    @NonNull
    private List<KUSChatAttachment> attachments;
    //endregion

    //region LifeCycle

    public KUSUpload(){
        attachments = new ArrayList<>();
    }

    //endregion

    //region Public Methods
    public void uploadImages(@Nullable final List<Bitmap> images, @NonNull KUSUserSession userSession,
                             @NonNull final KUSImageUploadListener listener) {

        if (images == null || images.size() == 0) {
            listener.onCompletion(null, new ArrayList<KUSChatAttachment>());
            return;
        }

        attachments = new ArrayList<>(images.size());

        while (attachments.size() < images.size())
            attachments.add(null);

        for (int i = 0; i < images.size(); i++) {
            Bitmap bitmap = images.get(i);

            final int index = i;
            uploadImage(bitmap, userSession, new ImageUploadListener() {
                @Override
                public void onUploadComplete(Error error, KUSChatAttachment attachment) {
                    uploadCompleted(index, error, attachment, listener, images);
                }
            });
        }

    }
    //endregion

    //region Private Method
    private void uploadCompleted(int index,@Nullable Error error,@Nullable KUSChatAttachment attachment,
                                 @NonNull KUSImageUploadListener listener,@NonNull List<Bitmap> images) {
        if (error != null) {
            if (!sendingComplete) {
                sendingComplete = true;
                listener.onCompletion(error, null);
            }

            return;
        }

        uploadedCount++;
        attachments.set(index, attachment);

        if (uploadedCount == images.size() && !sendingComplete) {
            sendingComplete = true;
            listener.onCompletion(null, attachments);
        }
    }

    private void uploadImage(@Nullable Bitmap image, @NonNull final KUSUserSession userSession,
                             @NonNull final ImageUploadListener listener) {
        if (image == null) {
            return;
        }

        final byte[] imageBytes = KUSImage.getByteArrayFromBitmap(image);
        if (imageBytes == null)
            return;

        final String filename = String.format("%s.jpg", UUID.randomUUID().toString());

        userSession.getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.CHAT_ATTACHMENT_ENDPOINT,
                new HashMap<String, Object>() {{
                    put("name", filename);
                    put("contentLength", imageBytes.length);
                    put("contentType", "image/jpeg");
                }},
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {

                        if (error != null) {
                            listener.onUploadComplete(error, null);
                            return;
                        }

                        final KUSChatAttachment chatAttachment;
                        try {
                            chatAttachment = new KUSChatAttachment(JsonHelper.jsonObjectFromKeyPath(response, "data"));
                        } catch (KUSInvalidJsonException e) {
                            return;
                        }

                        try {
                            URL uploadURL = new URL(JsonHelper.stringFromKeyPath(response, "meta.upload.url"));
                            HashMap<String, String> uploadFields =
                                    JsonHelper.hashMapFromKeyPath(response, "meta.upload.fields");

                            userSession.getRequestManager().uploadImageOnS3(uploadURL,
                                    filename,
                                    imageBytes,
                                    uploadFields,
                                    new KUSRequestCompletionListener() {
                                        @Override
                                        public void onCompletion(Error error, JSONObject response) {

                                            if (error != null) {
                                                listener.onUploadComplete(error, null);
                                                return;
                                            }

                                            listener.onUploadComplete(null, chatAttachment);
                                        }
                                    });

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    //endregion

    //region Interface
    public interface ImageUploadListener {
        void onUploadComplete(@Nullable Error error, @Nullable KUSChatAttachment attachment);
    }
    //endregion

}
