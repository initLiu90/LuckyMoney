package com.lzp.luckymoney.xposed;

import android.content.ContentValues;

import com.lzp.luckymoney.xposed.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;
import static com.lzp.luckymoney.xposed.util.Constants.TAG_WX_LOG;

public final class Debug {
    public static void debug(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.luckymoney.b.ag", lpparam.classLoader,
                int.class, String.class, String.class, int.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e(TAG, "ag.arg0=" + param.args[0] + ",ag.arg1=" + param.args[1] + ",ag.arg2=" + param.args[2] + ",ag.arg3=" + param.args[3] + ",ag.arg4=" + param.args[4]);
                    }
                });
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI", lpparam.classLoader, "d",
                int.class, int.class, String.class, XposedHelpers.findClass("com.tencent.mm.ab.l", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e(TAG, "ReceiveUI.d.arg0=" + param.args[0] + ",ReceiveUI.d.arg1=" + param.args[1] + ",ReceiveUI.d.arg2=" + param.args[2] + ",ReceiveUI.d.arg3=" + param.args[3].getClass().getName());
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.luckymoney.b.ad", lpparam.classLoader,
                int.class, int.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e(TAG, "ad.arg0=" + param.args[0] + ",ad.arg1=" + param.args[1] + ",ad.arg2=" + param.args[2] + ",ad.arg3=" + param.args[3] + ",ad.arg4=" + param.args[4] + ",ad.arg5=" + param.args[5] + ",ad.arg6=" + param.args[6] + ",ad.arg7=" + param.args[7]);
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
