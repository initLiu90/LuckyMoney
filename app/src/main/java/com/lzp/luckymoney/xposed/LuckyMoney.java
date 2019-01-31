package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;

import com.lzp.luckymoney.xposed.util.Log;

import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;
import static com.lzp.luckymoney.xposed.util.Constants.TAG_ERROR;

public class LuckyMoney implements IXposedHookLoadPackage {
    private static final int LUCKY_MONEY_C2C_MSG_TYPE = 436207665;
    private static final int LUCKY_MONEY_GROUP_MSG_TYPE = 436207665;
    private Activity mTopActivity;
    private Object mClient;
    private Object mObj = new Object();
    private LuckyMoneyConfig mConfig = new LuckyMoneyConfig();
    /**
     * paymsgid和talker的映射表。
     * client只创建一个，如果直接把talker作为参数传给initNetReqClient方法的话，
     * 在initNetReqClient方法中创建的TimestampCallback匿名内部类中会拷贝一份talker对象，这样在发送luckymoney请求时，永远都是第一次创建client时，调用initNetReqClient方法传的参数talker。这样就会造成抢红包失败。
     * 由于在timestamp中有paymsgid这个可以唯一标识一个支付信息，所以用paymsgid和talker做一个映射。
     */
    private ConcurrentHashMap<String, String> mTalkers = new ConcurrentHashMap<>();

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
        }
    }

    private void decodeMsg(final Object[] args, final XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        String arg1 = (String) args[0];
        String arg2 = (String) args[1];
        ContentValues arg3 = (ContentValues) args[2];
        if (arg3 == null) return;

        int type = arg3.getAsInteger("type");
        if (arg1.equals("message") && arg2.equals("msgId") && (type == LUCKY_MONEY_C2C_MSG_TYPE || type == LUCKY_MONEY_GROUP_MSG_TYPE)) {
            Observable.just(new Object[]{arg3, lpparam})
                    .map(v -> {//step1:decode msg
                        final LuckyMoneyMsg luckyMoneyMsg = LuckyMoneyHelper.decodeLuckyMoneyMsg((ContentValues) v[0]);
                        mTalkers.put(luckyMoneyMsg.paymsgid, luckyMoneyMsg.talker);
                        Log.e(TAG, "luckymoneymsg=" + luckyMoneyMsg.toString());
                        return new Object[]{luckyMoneyMsg, v[1]};
                    })
                    .map(v -> {//step2:check config and init client.  0-->luckymoneymsg 1-->lpparam
                        boolean interrupt = false;
                        if (!mConfig.isEnabled()) {
                            Log.e(TAG, "grab luckymoney is disable");
                            interrupt = true;
                        } else {
                            initNetReqClient((XC_LoadPackage.LoadPackageParam) v[1]);
                        }
                        return new Object[]{interrupt, v[0], v[1]};
                    })
                    .map(v -> {//step3:send timestamp request.   0-->interrupt 1-->luckymoneymsg 2-->lpparam
                        boolean interrupt = (Boolean) v[0];
                        if (!interrupt) {
                            Object param1 = LuckyMoneyHelper.createTimestampReqParam1((XC_LoadPackage.LoadPackageParam) v[2], (LuckyMoneyMsg) v[1]);
                            LuckyMoneyHelper.sendTimestampReq(mClient, param1, lpparam);
                        }
                        return interrupt;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    private void initNetReqClient(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (mClient == null) {
            synchronized (mObj) {
                if (mClient == null) {
                    //step2-1: create net request client
                    mClient = LuckyMoneyHelper.createNetReqClient(mTopActivity, lpparam);
                    if (mClient == null) return;
                    //step2-2: add a listener on timestamp request
                    LuckyMoneyHelper.registeTimestampCallback(lpparam, new TimestampCallback() {
                        @Override
                        public void onReceive(Object wxTimestamp) {
                            //step4: when receive timestamp then send luckmoney request immediately.
                            if (mConfig.isEnabled()) {
                                grabLuckMoney(wxTimestamp, lpparam);
                            } else {
                                Log.e(TAG, "receive timestamp but not send luckymoney request due to is disabled");
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * step4: when receive timestamp then send luckmoney request immediately.
     *
     * @param wxTimestamp
     * @param lpparam
     */
    private void grabLuckMoney(Object wxTimestamp, XC_LoadPackage.LoadPackageParam lpparam) {
        String paymsgid = (String) XposedHelpers.getObjectField(wxTimestamp, "kLZ");
        String talker = mTalkers.get(paymsgid);
        Schedulers.computation().createWorker().schedule(() -> {
            Log.e(TAG, "receive timestamp=" + wxTimestamp.toString() + ",talker=" + talker);
            Object param1 = LuckyMoneyHelper.creatLuckyMoneyReqParam1(wxTimestamp, talker, lpparam);
            LuckyMoneyHelper.sendLuckyMoneyReq(mClient, param1, lpparam);
        });
    }
}