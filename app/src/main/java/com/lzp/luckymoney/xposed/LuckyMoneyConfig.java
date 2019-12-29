package com.lzp.luckymoney.xposed;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.lzp.luckymoney.xposed.util.Log;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;

public class LuckyMoneyConfig {
    public static boolean DEBUG = false;

    private static LuckyMoneyConfig sInstance;

    private boolean mEnabled = true;

    public static synchronized LuckyMoneyConfig getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LuckyMoneyConfig(context.getApplicationContext());
        }
        return sInstance;
    }

    private LuckyMoneyConfig(Context context) {
        Context applicationContext = context != null ? context.getApplicationContext() : null;
        if (applicationContext != null) {
            Log.e(TAG, "config");
            applicationContext.getContentResolver().registerContentObserver(Uri.parse("content://com.lzp.luckymoney.provider/grab"), true, new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange) {
                    mEnabled = isEnabled(applicationContext);
                }
            });
            mEnabled = isEnabled(applicationContext);
        } else {
            Log.e(TAG, "config failed on null context");
        }
    }

    private boolean isEnabled(Context context) {
        int result = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.lzp.luckymoney.provider/grab"), null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }

        Log.e(TAG, "get config enabled=" + (result == 1));
        return result == 1;
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}
