package com.lzp.luckymoney;

import android.content.ContentValues;

import com.lzp.luckymoney.util.Log;
import com.lzp.luckymoney.util.XmlToJson;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class LuckyMoneyHelper {
    public static LuckyMoneyMsg decodeLuckyMoneyMsg(ContentValues contentValues) {
        String xml = (String) contentValues.get("xml");
        if (xml == null || xml.isEmpty()) return null;

        JSONObject jsonObject = new XmlToJson.Builder(xml).build();
        return null;
    }

    public static void hookLogMethod(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "i", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "d", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "v", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "k", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "l", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "w", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "e", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.x", lpparam.classLoader, "f", String.class, String.class, Object[].class, new XC_MethodHook() {
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
                Log.e(LuckyMoney.TAG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });
    }
}
