package com.lzp.luckymoney.xposed;

import android.app.Application;
import android.content.ContentValues;

import com.lzp.luckymoney.xposed.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private Object mClient;
    private Object mObj = new Object();
    private Map<String, LuckyMoneyMsg> luckyMoneyMsgMap = new ConcurrentHashMap();
    private Application mApplication;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null) return;
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Log.e(TAG, "hook weichat");

            Class clzInstrumentation = XposedHelpers.findClass("android.app.Instrumentation", lpparam.classLoader);
            XposedHelpers.findAndHookMethod("android.app.LoadedApk", lpparam.classLoader, "makeApplication", boolean.class, clzInstrumentation, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    mApplication = (Application) param.getResult();
                    Log.e(TAG, "hook makeApplicaiton mApplication is null " + (mApplication == null));
                }
            });
            if (DEBUG) {
                Debug.hookLogMethod(lpparam);
                Debug.hookLuckyMoneyNotHookReceiveUI(lpparam);
            }

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
        if (!isEnable()) {
            return null;
        }

        String arg1 = (String) args[0];
        String arg2 = (String) args[1];
        ContentValues contentValues = (ContentValues) args[2];

        if (DEBUG) {
            Debug.printReceivedMsg(arg1, arg2, contentValues);
        }

        if (contentValues == null) return null;

        LuckyMoneyMsg luckyMoneyMsg = null;
        long type = -1L;
        if (contentValues.containsKey("type")) {
            type = contentValues.getAsLong("type");
        }
        if (arg1.equals("message") && arg2.equals("msgId") && (type == LuckyMoneyMsg.LUCKY_MONEY_C2C_MSG_TYPE || type == LuckyMoneyMsg.LUCKY_MONEY_GROUP_MSG_TYPE)) {
            //step1:decode message
            luckyMoneyMsg = LuckyMoneyHelper.decodeLuckyMoneyMsg(contentValues);
            luckyMoneyMsgMap.put(luckyMoneyMsg.nativeurl, luckyMoneyMsg);

            if (DEBUG) {
                Log.e(TAG, "luckymoneymsg=" + luckyMoneyMsg.toString());
            }
        }
        return luckyMoneyMsg;
    }

    //step2:init net request client
    private void initNetReqClient(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!isEnable()) {
            return;
        }

        if (mClient == null) {
            synchronized (mObj) {
                if (mClient == null) {
                    mClient = LuckyMoneyHelper.createNetReqClient(mApplication, lpparam, (preGrabRsp -> {
                        if (DEBUG) {
                            int type = (int) XposedHelpers.callMethod(preGrabRsp, "getType");
                            int dFc = XposedHelpers.getIntField(preGrabRsp, "dFc");
                            String dEZ = (String) XposedHelpers.getObjectField(preGrabRsp, "dEZ");
                            String wVk = (String) XposedHelpers.getObjectField(preGrabRsp, "wVk");
                            int msgType = XposedHelpers.getIntField(preGrabRsp, "msgType");
                            String xcw = (String) XposedHelpers.getObjectField(preGrabRsp, "xcw");
                            Log.e(TAG, "receive pre grab response type=" + type + ", dFc=" + dFc + ", dEZ=" + dEZ + ", wVk=" + wVk + ", msgType=" + msgType + ", xcw=" + xcw);
                        }
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
        if (!isEnable()) {
            return;
        }

        Object param1 = LuckyMoneyHelper.createPreLuckyMoneyParam(lpparam, luckyMoneyMsg);
        LuckyMoneyHelper.sendPreLuckyMoneyReq(mClient, param1, lpparam);
    }

    //step4:grab lucky money
    private void grabLuckyMoney(final Object preGrabRsp, final XC_LoadPackage.LoadPackageParam lpparam) {
        if (!isEnable()) {
            return;
        }

        Schedulers.io().scheduleDirect(() -> {
            String nativeUrl = (String) XposedHelpers.getObjectField(preGrabRsp, "dEZ");
            LuckyMoneyMsg luckyMoneyMsg = luckyMoneyMsgMap.remove(nativeUrl);

            if (DEBUG) {
                Log.e(TAG, "nativeUrl=" + nativeUrl + ", luckyMoneyMsg=" + (luckyMoneyMsg == null ? "null" : luckyMoneyMsg.toString()));
            }

            if (luckyMoneyMsg != null) {
                Object param = LuckyMoneyHelper.createLuckyMoneyReqParam(preGrabRsp, luckyMoneyMsg, lpparam);
                LuckyMoneyHelper.sendLuckyMoneyReq(mClient, param, lpparam);
            }
        });
    }

    private boolean isEnable() {
        if (mApplication == null) {
            Log.e(TAG, "mApplication is null disable lucky");
            return false;
        }

        if (!LuckyMoneyConfig.getInstance(mApplication).isEnabled()) {
            Log.e(TAG, "lucky is disabled");
            return false;
        }
        return true;
    }
}