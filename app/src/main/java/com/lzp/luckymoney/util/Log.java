package com.lzp.luckymoney.util;

public final class Log {
    public static void e(String tag, String msg) {
        android.util.Log.e(tag, "=================" + msg + "===================");
    }

    public static void e(String tag, String msg, Throwable throwable) {
        Log.e(tag, "===================" + msg + "====================", throwable);
    }
}