package com.lzp.luckymoney;

import android.app.Activity;
import android.content.ContentValues;

import com.lzp.luckymoney.util.Log;
import com.lzp.luckymoney.util.XmlToJson;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.lzp.luckymoney.util.Constants.TAG;
import static com.lzp.luckymoney.util.Constants.TAG_WX_LOG;

public final class LuckyMoneyHelper {
    /**
     * decode xml to json
     *
     * @param contentValues
     * @return
     */
    public static LuckyMoneyMsg decodeLuckyMoneyMsg(ContentValues contentValues) {
        String talker = contentValues.getAsString("talker");
        String xml = (String) contentValues.get("content");
        if (xml == null || xml.isEmpty()) return null;

        JSONObject jsonObject = new XmlToJson.Builder(xml).build();
        return LuckyMoneyMsg.createLuckyMoneyMsg(jsonObject, talker);
    }


    /**
     * create a client to send network request
     *
     * @param topActivity
     * @param lpparam
     * @return
     */
    public static Object createNetReqClient(Activity topActivity, final XC_LoadPackage.LoadPackageParam lpparam) {
        Class clzJ = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.b.j", lpparam.classLoader);
        Object objJ = XposedHelpers.newInstance(clzJ, topActivity, null);
        Log.e(TAG, "createNetReqClient=" + objJ);
        return objJ;
    }

    /**
     * 1. Hook com.tencent.mm.plugin.luckymoney.b.j.a(int,int,string,l) method, to get response after send timestamp request.
     * 2. After get the server response,then get timestamp from the response data. Then send a request to get luckymoney.
     */
    public static void registeTimestampCallback(final XC_LoadPackage.LoadPackageParam lpparam, final TimestampCallback callback) {
        Log.e(TAG, "registeTimestampCallback");
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.b.j", lpparam.classLoader, "a",
                int.class, int.class, String.class, XposedHelpers.findClass("com.tencent.mm.ab.l", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object wxtimestamp = param.args[3];
                        if (wxtimestamp.getClass().getName().equals("com.tencent.mm.plugin.luckymoney.b.ag")) {
                            callback.onReceive(wxtimestamp);
                        }
                    }
                });
    }

    /**
     * Create com.tencent.mm.plugin.luckymoney.b.ag object and will be used while send timestamp request
     *
     * @param lpparam
     * @param luckyMoneyMsg
     * @return com.tencent.mm.plugin.luckymoney.b.ag
     */
    public static Object createTimestampReqParam1(final XC_LoadPackage.LoadPackageParam lpparam, LuckyMoneyMsg luckyMoneyMsg) {
        Class clzAG = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.b.ag", lpparam.classLoader);
        Object objAG = XposedHelpers.newInstance(clzAG, luckyMoneyMsg.channelid, luckyMoneyMsg.sendid, luckyMoneyMsg.nativeurl, luckyMoneyMsg.way, luckyMoneyMsg.version);
        Log.e(TAG, "createTimestampReqParam1=" + objAG.toString());
        return objAG;
    }

    /**
     * Send net request via client.Client object is created by the method of createNetReqClient
     *
     * @param client
     * @param param1
     * @param lpparam
     */
    private static void sendNetReq(Object client, Object param1, final XC_LoadPackage.LoadPackageParam lpparam) {
        Log.e(TAG, "sendNetReq");
        XposedHelpers.callMethod(client, "b", param1, false);
    }

    public static void sendTimestampReq(Object client, Object param1, final XC_LoadPackage.LoadPackageParam lpparam) {
        Log.e(TAG, "sendTimestampReq client=" + client.toString() + ",param1=" + param1.toString());
        sendNetReq(client, param1, lpparam);
    }

    public static void sendLuckyMoneyReq(Object client, Object param1, final XC_LoadPackage.LoadPackageParam lpparam) {
        Log.e(TAG, "sendLuckyMoneyReq client=" + client.toString() + ",param1=" + param1.toString());
        sendNetReq(client, param1, lpparam);
    }

    /**
     * The real request to get luckymony.
     *
     * @param wxTimestamp
     * @param talker
     * @param lpparam
     */
    public static Object creatLuckyMoneyReqParam1(final Object wxTimestamp, final String talker, final XC_LoadPackage.LoadPackageParam lpparam) {
        Class clzBO = XposedHelpers.findClass(" com.tencent.mm.plugin.luckymoney.b.o", lpparam.classLoader);
        String baX = (String) XposedHelpers.callStaticMethod(clzBO, "baX");

        Class clzQ = XposedHelpers.findClass("com.tencent.mm.model.q", lpparam.classLoader);
        String GH = (String) XposedHelpers.callStaticMethod(clzQ, "GH");

        LuckyMoneyReq req = new LuckyMoneyReq.Builder()
                .bxk(XposedHelpers.getIntField(wxTimestamp, "bxk"))
                .kLZ((String) XposedHelpers.getObjectField(wxTimestamp, "kLZ"))
                .ceR((String) XposedHelpers.getObjectField(wxTimestamp, "ceR"))
                .kRC((String) XposedHelpers.getObjectField(wxTimestamp, "kRC"))
                .baX(baX)
                .GH(GH)
                .username(talker)
                .build();

        Log.e(TAG, "LuckyMoneyReq=" + req.toString());

        Class clzAD = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.b.ad", lpparam.classLoader);
        Object ad = XposedHelpers.newInstance(clzAD, req.msgType, req.bxk, req.kLZ, req.ceR, req.baX, req.GH, req.username, req.version, req.kRC);
        return ad;
    }

    /**
     * hook weichat log method
     *
     * @param lpparam
     */
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
                        stringBuilder.append(obj == null ? "" : obj.toString() + ",");
                    }
                }
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
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
                Log.e(TAG_WX_LOG, "tag=" + tag + ",msg=" + msg + ",objArr=" + stringBuilder.toString());
            }
        });
    }
}
