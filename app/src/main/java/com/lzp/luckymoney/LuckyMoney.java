package com.lzp.luckymoney;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;

import com.lzp.luckymoney.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.lzp.luckymoney.util.Constants.TAG;

public class LuckyMoney implements IXposedHookLoadPackage {
    private static final int LUCKY_MONEY_C2C_MSG_TYPE = 436207665;
    private static final int LUCKY_MONEY_GROUP_MSG_TYPE = 436207665;
    private Activity mTopActivity;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null) return;
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Log.e(TAG, "hook weichat");

//            LuckyMoneyHelper.hookLogMethod(lpparam);
            XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newActivity", ClassLoader.class, String.class, Intent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mTopActivity = (Activity) param.getResult();
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                public void beforeHookedMethod(MethodHookParam param) {
                    String arg1 = (String) param.args[0];
                    String arg2 = (String) param.args[1];
                    ContentValues arg3 = (ContentValues) param.args[2];
//                    Log.e(TAG, "arg1=" + arg1 + " ,arg2=" + arg2);
//                    for (String key : arg3.keySet()) {
//                        Log.e(TAG, "key=" + key + ",value=" + arg3.get(key));
//                    }
                    int type = arg3.getAsInteger("type");
                    if (arg1.equals("message") && arg2.equals("msgId") && (type == LUCKY_MONEY_C2C_MSG_TYPE || type == LUCKY_MONEY_GROUP_MSG_TYPE)) {
                        LuckyMoneyMsg luckyMoneyMsg = LuckyMoneyHelper.decodeLuckyMoneyMsg(arg3);
                        Log.e(TAG, "luckymoneymsg=" + luckyMoneyMsg.toString());

                        Object client = LuckyMoneyHelper.createNetReqClient(mTopActivity, lpparam);
                        LuckyMoneyHelper.registeTimestampCallback(lpparam, luckyMoneyMsg.talker, client);
                        Object param1 = LuckyMoneyHelper.createTimestampReqParam1(lpparam, luckyMoneyMsg);
                        LuckyMoneyHelper.sendNetReq(client, param1, lpparam);
                    }
                }
            });
        }
    }
}