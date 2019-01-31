package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;

import com.lzp.luckymoney.xposed.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;
import static com.lzp.luckymoney.xposed.util.Constants.TAG_ERROR;

public class LuckyMoney implements IXposedHookLoadPackage {
    private static final int LUCKY_MONEY_C2C_MSG_TYPE = 436207665;
    private static final int LUCKY_MONEY_GROUP_MSG_TYPE = 436207665;
    private Activity mTopActivity;
    private Object mClient;
    private LuckyMoneyConfig mConfig = new LuckyMoneyConfig();

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null) return;
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Log.e(TAG, "hook weichat");

            XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newActivity", ClassLoader.class, String.class, Intent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mTopActivity = (Activity) param.getResult();
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                public void beforeHookedMethod(MethodHookParam param) {
                    try {
                        mConfig.config(mTopActivity);
                        decodeMsg(param.args, lpparam);
                    } catch (Exception e) {
                        Log.e(TAG_ERROR, "error:", e);
                    }
                }
            });

//            Debug.debug(lpparam);
        }
    }

    private void decodeMsg(final Object[] args, final XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        String arg1 = (String) args[0];
        String arg2 = (String) args[1];
        ContentValues arg3 = (ContentValues) args[2];
        if (arg3 == null) return;
//                    Debug.printReceivedMsg(arg1, arg2, arg3);

        int type = arg3.getAsInteger("type");
        if (arg1.equals("message") && arg2.equals("msgId") && (type == LUCKY_MONEY_C2C_MSG_TYPE || type == LUCKY_MONEY_GROUP_MSG_TYPE)) {
            final LuckyMoneyMsg luckyMoneyMsg = LuckyMoneyHelper.decodeLuckyMoneyMsg(arg3);
            Log.e(TAG, "luckymoneymsg=" + luckyMoneyMsg.toString());
            grabLuckmoney(luckyMoneyMsg, lpparam);
        }
    }

    private void grabLuckmoney(final LuckyMoneyMsg luckyMoneyMsg, final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!mConfig.isEnabled()) {
            Log.e(TAG, "grab luckymoney is disable");
            return;
        }

        if (mClient == null) {
            //step1: create net request client
            mClient = LuckyMoneyHelper.createNetReqClient(mTopActivity, lpparam);
            if (mClient == null) return;
            //step2: add a listener on timestamp request
            LuckyMoneyHelper.registeTimestampCallback(lpparam, new TimestampCallback() {
                @Override
                public void onReceive(Object wxTimestamp) {
                    //step3: send luckmoney request
                    if (mConfig.isEnabled()) {
                        Log.e(TAG, "receive timestamp=" + wxTimestamp.toString());
                        Object param1 = LuckyMoneyHelper.creatLuckyMoneyReqParam1(wxTimestamp, luckyMoneyMsg.talker, lpparam);
                        LuckyMoneyHelper.sendLuckyMoneyReq(mClient, param1, lpparam);
                    } else {
                        Log.e(TAG, "receive timestamp but not send luckymoney request due to is disabled");
                    }
                }
            });
        }

        //step4: send timestamp request
        Object param1 = LuckyMoneyHelper.createTimestampReqParam1(lpparam, luckyMoneyMsg);
        LuckyMoneyHelper.sendTimestampReq(mClient, param1, lpparam);
    }
}