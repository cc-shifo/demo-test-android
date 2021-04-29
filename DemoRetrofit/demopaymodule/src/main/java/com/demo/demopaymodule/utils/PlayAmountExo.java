package com.demo.demopaymodule.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

public final class PlayAmountExo {
    private static final String TAG = "ExoPlayAmount";
    private ExoPlayer mPlayer;
    private static PlayAmountExo mInstance;
    private boolean mIsPlaying;
    private final Object lockRoundPlayer; // between stopping and playing thread lock

    public static synchronized PlayAmountExo getInstance() {
        if (mInstance == null) {
            mInstance = new PlayAmountExo();
        }

        return mInstance;
    }

    private PlayAmountExo() {
        //nothing
        lockRoundPlayer = new Object();
    }

    public boolean playAmount(@NonNull Context context, @RawRes int[] resIds) {
        synchronized (lockRoundPlayer) {
            if (mIsPlaying) {
                Log.d(TAG, "Num2Rmb mIsPlaying");
                return false;
            }
            mIsPlaying = true;
        }

        SimpleExoPlayer.Builder builder = new SimpleExoPlayer.Builder(context);
        builder.setLooper(context.getMainLooper());
        mPlayer = builder.build();
        for (int i : resIds) {
            String uriStr = "android.resource://" + context.getPackageName() + "/" + i;
            Uri uri = Uri.parse(uriStr);
            mPlayer.addMediaItem(MediaItem.fromUri(uri));
        }
        mPlayer.prepare();
        PlaybackParameters parameters = mPlayer.getPlaybackParameters();
        parameters = parameters.withSpeed(0.8f);
        mPlayer.setPlaybackParameters(parameters);
        mPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError: ", error.getCause());
                synchronized (lockRoundPlayer) {
                    if (mPlayer != null) {
                        mPlayer.release();
                        mPlayer = null;
                    }
                    mIsPlaying = false;
                }
            }

            @Override
            public void onPlaybackStateChanged(int state) {
                Log.d(TAG, "onPlaybackStateChanged thread id: "
                        + Thread.currentThread().getName());
                if (state == Player.STATE_ENDED) {
                    synchronized (lockRoundPlayer) {
                        mPlayer.release();
                        mPlayer = null;
                        mIsPlaying = false;
                    }
                }
            }
        });
        mPlayer.setPlayWhenReady(true);
        return true;
    }

    public void stopPlayAmount() {
        synchronized (lockRoundPlayer) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
                mPlayer = null;
            }
            mIsPlaying = false;
        }
    }
}
