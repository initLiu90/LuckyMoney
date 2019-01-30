package com.lzp.luckymoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SettingUtil {

    public static void updateSetting(Context context, String key, boolean enable) {
        ContentValues values = new ContentValues();
        values.put(key, enable);
        context.getContentResolver().update(LuckyMoneySettingProvider.URI_GRAB, values, null, null);
    }

    public static boolean getSetting(Context context, String key) {

        Cursor cursor = context.getContentResolver().query(LuckyMoneySettingProvider.URI_GRAB, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(0) == 1 ? true : false;
        }
        return false;
    }
}
