package com.lzp.luckymoney;

import android.content.ContentValues;
import android.content.Intent;

import com.lzp.luckymoney.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LuckyMoney implements IXposedHookLoadPackage {
    public static final String TAG = "LuckyMoney";
    private static final int LUCKY_MONEY_MSG_TYPE = 2001;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null) return;
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Log.e(TAG, "hook weichat");

            LuckyMoneyHelper.hookLogMethod(lpparam);

            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                public void beforeHookedMethod(MethodHookParam param) {
                    String arg1 = (String) param.args[0];
                    String arg2 = (String) param.args[1];
                    ContentValues arg3 = (ContentValues) param.args[2];
                    Log.e(TAG, "arg1=" + arg1 + " ,arg2=" + arg2);
                    for (String key : arg3.keySet()) {
                        Log.e(TAG, "key=" + key + ",value=" + arg3.get(key));
                    }
                    int type = arg3.getAsInteger("type");
                    if (arg1.equals("AppMessage") && arg2.equals("msgId") && type == LUCKY_MONEY_MSG_TYPE) {
                        LuckyMoneyHelper.decodeLuckyMoneyMsg(arg3);
                    }
                }
            });

//            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.MMFragmentActivity", lpparam.classLoader, "startActivity", Intent.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    Log.e("Test", "start activity=" + param.thisObject.getClass().getSimpleName());
//                    StackTraceElement[] elements = new Throwable().getStackTrace();
//                    for (StackTraceElement element : elements) {
//                        Log.e("Test", element.getClassName().toString() + "." + element.getMethodName().toString() + " " + element.getLineNumber());
//                    }
//                }
//            });

            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.b.j", lpparam.classLoader, "b", XposedHelpers.findClass("com.tencent.mm.ab.l", lpparam.classLoader), boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            Log.e("Test", "luckymoney.b was called");
                            StackTraceElement[] elements = new Throwable().getStackTrace();
                            for (StackTraceElement element : elements) {
                                Log.e("Test", element.getClassName().toString() + "." + element.getMethodName().toString() + " " + element.getLineNumber());
                            }
                        }
                    });
        }
    }


}