package com.kustomer.kustomersdk.Helpers;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.R;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSAudio implements MediaPlayer.OnCompletionListener {

    //region Properties
    private static KUSAudio kusAudio;
    //endregion

    //region LifeCycle
    private static KUSAudio getSharedInstance() {
        if (kusAudio == null)
            kusAudio = new KUSAudio();

        return kusAudio;
    }
    //endregion

    //region Public Methods
    public static void playMessageReceivedSound() {
        getSharedInstance().playMsgReceivedSound();
    }
    //endregion

    //region Private Methods
    private void playMsgReceivedSound() {
        try {
            MediaPlayer mPlayer = MediaPlayer.create(Kustomer.getContext(), R.raw.kus_message_received);

            if (mPlayer != null) {
                mPlayer.setOnCompletionListener(this);
                mPlayer.start();
            }
        } catch (Exception e) {
            KUSLog.kusLogError(e.getMessage());
        }
    }

    @Override
    public void onCompletion(@NonNull MediaPlayer mp) {
        mp.stop();
        mp.release();
        mp.setOnCompletionListener(null);
    }
    //endregion

}
