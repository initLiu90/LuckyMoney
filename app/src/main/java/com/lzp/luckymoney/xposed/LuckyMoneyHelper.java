package com.lzp.luckymoney.xposed;

import android.app.Activity;
import android.content.ContentValues;

import com.lzp.luckymoney.xposed.util.Log;
import com.lzp.luckymoney.xposed.util.XmlToJson;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.lzp.luckymoney.xposed.util.Constants.TAG;

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
        int start = xml.indexOf("<msg>");
        xml = xml.substring(start, xml.length());

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
    public static Object createNetReqClient(Activity topActivity, final XC_LoadPackage.LoadPackageParam lpparam, final PreGrabReqCallback callback) {
        if (topActivity == null) return null;
        Class clzS = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.s", lpparam.classLoader);
        Object objS = XposedHelpers.newInstance(clzS, topActivity, null);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1554);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1575);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1668);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1581);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1685);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1585);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1514);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1682);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1612);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1643);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 1558);
        XposedHelpers.callMethod(objS, "addSceneEndListener", 2715);
        Log.e(TAG, "createNetReqClient=" + objS);

        registerPreGrabReqCallback(lpparam, callback);

        return objS;
    }

    /**
     * 1. Hook com.tencent.mm.plugin.luckymoney.b.j.a(int,int,string,l) method, to get response after send timestamp request.
     * 2. After get the server response,then get timestamp from the response data. Then send a request to get luckymoney.
     */
    private static void registerPreGrabReqCallback(final XC_LoadPackage.LoadPackageParam lpparam, final PreGrabReqCallback callback) {
        Log.e(TAG, "registerPreGrabReqCallback");
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.model.s", lpparam.classLoader, "onSceneEnd",
                int.class, int.class, String.class, XposedHelpers.findClass("com.tencent.mm.aj.m", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int arg1 = (int) param.args[0];
                        int arg2 = (int) param.args[1];
                        Object preGrabRsp = param.args[3];
                        if (arg1 == 0 &&
                                arg2 == 0
                                && preGrabRsp.getClass().getName().equals("com.tencent.mm.plugin.luckymoney.model.aq")) {
                            callback.onReceive(preGrabRsp);
                        }
                    }
                });
    }

    /**
     * Create com.tencent.mm.plugin.luckymoney.model.aq object and will be used while send pre luckmonkey request
     *
     * @param lpparam
     * @param luckyMoneyMsg
     * @return com.tencent.mm.plugin.luckymoney.model.aq
     */
    public static Object createPreLuckyMoneyParam(final XC_LoadPackage.LoadPackageParam lpparam, LuckyMoneyMsg luckyMoneyMsg) {
        Class clzAQ = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.aq", lpparam.classLoader);
        Object objAQ = XposedHelpers.newInstance(clzAQ, luckyMoneyMsg.channelid, luckyMoneyMsg.sendid, luckyMoneyMsg.nativeurl, luckyMoneyMsg.way, luckyMoneyMsg.version);
        Log.e(TAG, "createPreLuckyMoneyParam=" + objAQ.toString());
        return objAQ;
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
        XposedHelpers.callMethod(client, "doSceneProgress", param1, false);
    }

    public static void sendPreLuckyMoneyReq(Object client, Object param1, final XC_LoadPackage.LoadPackageParam lpparam) {
        Log.e(TAG, "sendPreLuckyMoneyReq client=" + client.toString() + ",param1=" + param1.toString());
        sendNetReq(client, param1, lpparam);
    }

    public static void sendLuckyMoneyReq(Object client, Object param1, final XC_LoadPackage.LoadPackageParam lpparam) {
        Log.e(TAG, "sendLuckyMoneyReq client=" + client.toString() + ",param1=" + param1.toString());
        sendNetReq(client, param1, lpparam);
    }

    /**
     * The real request to get luckymony.
     *
     * @param preLuckMoneyRsp
     * @param lpparam
     */
    public static Object createLuckyMoneyReqParam(final Object preLuckMoneyRsp, final String talker, final XC_LoadPackage.LoadPackageParam lpparam) {
        int msgType = (int) XposedHelpers.getObjectField(preLuckMoneyRsp, "msgType");
        int channelId = (int) XposedHelpers.getObjectField(preLuckMoneyRsp, "cAL");
        String sendId = (String) XposedHelpers.getObjectField(preLuckMoneyRsp, "pUC");
        String nativeUrl = (String) XposedHelpers.getObjectField(preLuckMoneyRsp, "dss");

        Class clzX = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.x", lpparam.classLoader);
        String headImg = (String) XposedHelpers.callStaticMethod(clzX, "cfr");

        Class clzU = XposedHelpers.findClass("com.tencent.mm.model.u", lpparam.classLoader);
        String nickName = (String) XposedHelpers.callStaticMethod(clzU, "akp");

        String ver = "v1.0";

        String timingIdentifier = (String) XposedHelpers.getObjectField(preLuckMoneyRsp, "qaN");

        Class clzAN = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.an", lpparam.classLoader);
        Object objAn = XposedHelpers.newInstance(clzAN, msgType, channelId, sendId, nativeUrl, headImg, nickName, talker, ver, timingIdentifier);
        return objAn;
    }
}
