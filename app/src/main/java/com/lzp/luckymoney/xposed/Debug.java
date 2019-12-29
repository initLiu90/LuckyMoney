package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

import com.lzp.luckymoney.xposed.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;
import static com.lzp.luckymoney.xposed.util.Constants.TAG_WX_LOG;

public final class Debug {
    public static void hookLuckyMoneyNotHookReceiveUI(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Activity activity = (Activity) param.thisObject;
                String key_username = activity.getIntent().getStringExtra("key_username");
                Log.e(TAG, "key_username=" + key_username);
            }
        });
    }

    public static void printReceivedMsg(String arg1, String arg2, ContentValues arg3) {
        Log.e(TAG, "arg1=" + arg1 + " ,arg2=" + arg2);
        for (String key : arg3.keySet()) {
            Log.e(TAG, "key=" + key + ",value=" + arg3.get(key));
        }
    }

    /**
     * hook weichat log method
     *
     * @param lpparam
     */
    public static void hookLogMethod(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "i", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj == null ? "" : obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "d", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "v", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "f", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "e", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "w", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "a", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.ab", lpparam.classLoader, "b", String.class, String.class, Object[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                Object[] objArr = (Object[]) param.args[2];
                StringBuilder stringBuilder = new StringBuilder();
                if (objArr != null) {
                    for (Object obj : objArr) {
                        stringBuilder.append(obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });
    }
}
