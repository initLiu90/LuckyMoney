package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.lzp.luckymoney.xposed.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;

public class LuckyMoney implements IXposedHookLoadPackage, LuckyMoneyConfig.ConfigChangeListener {
    private static final int LUCKY_MONEY_C2C_MSG_TYPE = 436207665;
    private static final int LUCKY_MONEY_GROUP_MSG_TYPE = 436207665;
    private Activity mTopActivity;
    private Object mClient;
    private boolean mEnabled = true;
    private LuckyMoneyConfig mConfig;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null) return;
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Log.e(TAG, "hook weichat");

//            mConfig = new LuckyMoneyConfig(this);

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
//                    mConfig.config(mTopActivity);
                    Log.e(TAG, "SQLiteDatabase insert");
                    String arg1 = (String) param.args[0];
                    String arg2 = (String) param.args[1];
                    ContentValues arg3 = (ContentValues) param.args[2];
                    if (arg3 == null) return;
                    Log.e(TAG, "arg1=" + arg1 + " ,arg2=" + arg2);
                    for (String key : arg3.keySet()) {
                        Log.e(TAG, "key=" + key + ",value=" + arg3.get(key));
                    }
                    int type = arg3.getAsInteger("type");
                    Log.e(TAG, "last  :arg1=" + arg1 + ",arg2=" + arg2 + ",type=" + type);
                    if (arg1.equals("message") && arg2.equals("msgId") && (type == LUCKY_MONEY_C2C_MSG_TYPE || type == LUCKY_MONEY_GROUP_MSG_TYPE)) {
                        final LuckyMoneyMsg luckyMoneyMsg = LuckyMoneyHelper.decodeLuckyMoneyMsg(arg3);
                        Log.e(TAG, "luckymoneymsg=" + luckyMoneyMsg.toString());
                        grabLuckmoney(luckyMoneyMsg, lpparam);
                    }
                }
            });

//            XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.luckymoney.b.ag", lpparam.classLoader,
//                    int.class, String.class, String.class, int.class, String.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Log.e(TAG, "ag.arg0=" + param.args[0] + ",ag.arg1=" + param.args[1] + ",ag.arg2=" + param.args[2] + ",ag.arg3=" + param.args[3] + ",ag.arg4=" + param.args[4]);
//                        }
//                    });
//            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI", lpparam.classLoader, "d",
//                    int.class, int.class, String.class, XposedHelpers.findClass("com.tencent.mm.ab.l", lpparam.classLoader),
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Log.e(TAG, "ReceiveUI.d.arg0=" + param.args[0] + ",ReceiveUI.d.arg1=" + param.args[1] + ",ReceiveUI.d.arg2=" + param.args[2] + ",ReceiveUI.d.arg3=" + param.args[3].getClass().getName());
//                        }
//                    });
//            XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.luckymoney.b.ad", lpparam.classLoader,
//                    int.class, int.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            Log.e(TAG, "ad.arg0=" + param.args[0] + ",ad.arg1=" + param.args[1] + ",ad.arg2=" + param.args[2] + ",ad.arg3=" + param.args[3] + ",ad.arg4=" + param.args[4] + ",ad.arg5=" + param.args[5] + ",ad.arg6=" + param.args[6] + ",ad.arg7=" + param.args[7]);
//                        }
//                    });
        }
    }

    private void grabLuckmoney(final LuckyMoneyMsg luckyMoneyMsg, final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!mEnabled) {
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
                    if (mEnabled) {
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

    @Override
    public void onConfigChange(boolean enable) {
        mEnabled = enable;
        Log.e(TAG, "enable=" + mEnabled);
    }
}