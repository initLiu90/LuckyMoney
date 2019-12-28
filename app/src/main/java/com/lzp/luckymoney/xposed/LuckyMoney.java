package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;

import com.lzp.luckymoney.xposed.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.lzp.luckymoney.xposed.LuckyMoneyConfig.DEBUG;
import static com.lzp.luckymoney.xposed.util.Constants.TAG;
import static com.lzp.luckymoney.xposed.util.Constants.TAG_ERROR;

public class LuckyMoney implements IXposedHookLoadPackage {
    private static final long LUCKY_MONEY_C2C_MSG_TYPE = 436207665;
    private static final long LUCKY_MONEY_GROUP_MSG_TYPE = 436207665;
    private Activity mTopActivity;
    private Object mClient;
    private Object mObj = new Object();
    private LuckyMoneyConfig mConfig;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null) return;
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Log.e(TAG, "hook weichat");

            XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newActivity", ClassLoader.class, String.class, Intent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    mTopActivity = (Activity) param.getResult();
                    mConfig = LuckyMoneyConfig.getInstance(mTopActivity);
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                public void beforeHookedMethod(MethodHookParam param) {
                    Log.e(TAG, "hook weichat insert");
                    try {
                        Observable.just(decodeMsg(param.args))
                                .map(luckyMoneyMsg -> {
                                    initNetReqClient(lpparam);
                                    return luckyMoneyMsg;
                                })
                                .map(luckyMoneyMsg -> {
                                    sendPreLuckyMoneyReq(luckyMoneyMsg, lpparam);
                                    return luckyMoneyMsg;
                                })
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                    } catch (Exception e) {
                        Log.e(TAG_ERROR, "error:", e);
                    }
                }
            });
        }
    }

    //step1:decode msg
    private LuckyMoneyMsg decodeMsg(final Object[] args) {
        if (!mConfig.isEnabled()) {
            return null;
        }

        String arg1 = (String) args[0];
        String arg2 = (String) args[1];
        ContentValues contentValues = (ContentValues) args[2];

        if (DEBUG) {
            Log.e(TAG, "arg1=" + arg1);
            Log.e(TAG, "arg2=" + arg2);
            if (contentValues != null) {
                for (String key : contentValues.keySet()) {
                    Object value = contentValues.get(key);
                    Log.e(TAG, "key=" + key + ", value=" + value);
                }
            }
        }

        if (contentValues == null) return null;

        LuckyMoneyMsg luckyMoneyMsg = null;
        long type = -1L;
        if (contentValues.containsKey("type")) {
            type = contentValues.getAsLong("type");
        }
        if (arg1.equals("message") && arg2.equals("msgId") && (type == LUCKY_MONEY_C2C_MSG_TYPE || type == LUCKY_MONEY_GROUP_MSG_TYPE)) {
            //step1:decode message
            luckyMoneyMsg = LuckyMoneyHelper.decodeLuckyMoneyMsg(contentValues);
            if (DEBUG) {
                Log.e(TAG, "luckymoneymsg=" + luckyMoneyMsg.toString());
            }
        }
        return luckyMoneyMsg;
    }

    //step2:init net request client
    private void initNetReqClient(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!mConfig.isEnabled()) {
            return;
        }

        if (mClient == null) {
            synchronized (mObj) {
                if (mClient == null) {
                    mClient = LuckyMoneyHelper.createNetReqClient(mTopActivity, lpparam, (preGrabRsp -> {
                        grabLuckyMoney(preGrabRsp, lpparam);
                    }));
                    if (mClient == null) {
                        return;
                    }
                }
            }
        }
    }

    //step3:send pre luckyMoney request
    private void sendPreLuckyMoneyReq(LuckyMoneyMsg luckyMoneyMsg, final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!mConfig.isEnabled()) {
            return;
        }

        Object param1 = LuckyMoneyHelper.createPreLuckyMoneyParam(lpparam, luckyMoneyMsg);
        LuckyMoneyHelper.sendPreLuckyMoneyReq(mClient, param1, lpparam);
    }

    //step4:grab lucky money
    private void grabLuckyMoney(final Object preGrabRsp, final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!mConfig.isEnabled()) {
            return;
        }

        String sendUserName = (String) XposedHelpers.getObjectField(preGrabRsp, "pYP");
        if (DEBUG) {
            Log.e(TAG, "sendUserName=" + sendUserName);
        }
        Object param1 = LuckyMoneyHelper.createLuckyMoneyReqParam(preGrabRsp, sendUserName, lpparam);
        LuckyMoneyHelper.sendLuckyMoneyReq(mClient, param1, lpparam);
    }
}