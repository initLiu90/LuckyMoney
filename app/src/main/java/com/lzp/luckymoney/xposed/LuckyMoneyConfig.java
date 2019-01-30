package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.lzp.luckymoney.xposed.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;

public class LuckyMoneyConfig {
    public interface ConfigChangeListener {
        void onConfigChange(boolean enable);
    }

    private ConfigChangeListener mListener;
    private boolean mIsConfiged = false;

    public LuckyMoneyConfig(ConfigChangeListener listener) {
        mListener = listener;
    }

    public void config(Activity activity) {
        Context context = activity != null ? activity.getApplicationContext() : null;
        if (!mIsConfiged && context != null) {
            Log.e(TAG, "config");
            context.getContentResolver().registerContentObserver(Uri.parse("content://com.lzp.luckymoney.provider/grab"), true, new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange) {
                    getConfig(context);
                }
            });
            getConfig(context);
            mIsConfiged = true;
        }
    }

    private void getConfig(Context context) {
        Observable.just(0)
                .map(v -> {
                    int result = 0;
                    Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.lzp.luckymoney.provider/grab"), null, null, null, null);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        result = cursor.getInt(0);
                    }
                    return result == 1 ? true : false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(enabled -> {
                    mListener.onConfigChange(enabled);
                });
    }
}
