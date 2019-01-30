package com.lzp.luckymoney;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class LuckyMoneySettingProvider extends ContentProvider {
    private static final String SP_NAME = "LucyMonkeySetting";
    public static String AUTHORITY = "com.lzp.luckymoney.provider";
    public static final String GRAB = "grab";

    private static final String URI_GRAB_STR = "content://" + AUTHORITY + "/" + GRAB;
    public static final Uri URI_GRAB = Uri.parse(URI_GRAB_STR);


    private SharedPreferences mSp;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, GRAB, 1);
    }

    @Override
    public boolean onCreate() {
        mSp = getContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor cursor = null;
        switch (sUriMatcher.match(uri)) {
            case 1:
                boolean rest = mSp.getBoolean(GRAB, true);
                int value = rest ? 1 : 0;
                cursor = new MatrixCursor(new String[]{GRAB});
                cursor.addRow(new Object[]{value});
                break;
        }
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                mSp.edit().putBoolean(GRAB, values.getAsBoolean(GRAB)).commit();
                getContext().getContentResolver().notifyChange(URI_GRAB, null);
                break;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                if (mSp.contains(GRAB)) {
                    mSp.edit().remove(GRAB).commit();
                    getContext().getContentResolver().notifyChange(URI_GRAB, null);
                }
                break;
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case 1:
                mSp.edit().putBoolean(GRAB, values.getAsBoolean(GRAB)).commit();
                getContext().getContentResolver().notifyChange(URI_GRAB, null);
                break;
        }
        return 0;
    }
}
