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
    private boolean mIsConfiged = false;
    private boolean mEnabled = false;

    public void config(Activity activity) {
        if (!mIsConfiged) {
            Context context = activity != null ? activity.getApplicationContext() : null;
            if (context != null) {
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
    }

    private void getConfig(Context context) {
        Log.e(TAG, "get config");
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
                    mEnabled = enabled;
                    Log.e(TAG, "enabled=" + mEnabled);
                });
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}
