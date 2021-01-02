package com.lzp.luckymoney.xposed;

import android.content.ContentValues;
import android.content.Context;

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
     * @param context
     * @param lpparam
     * @return
     */
    public static Object createNetReqClient(Context context, final XC_LoadPackage.LoadPackageParam lpparam, final PreGrabReqCallback callback) {
        if (context == null) return null;
        Class clzW = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.w", lpparam.classLoader);
        Object objW = XposedHelpers.newInstance(clzW, context, null);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1554);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1575);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1668);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1581);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1685);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1585);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1514);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1682);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1612);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1643);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 1558);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 2715);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 4605);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 4915);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 4536);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 5148);
        XposedHelpers.callMethod(objW, "addSceneEndListener", 4395);
//        Log.e(TAG, "createNetReqClient=" + objW);

        registerPreGrabReqCallback(lpparam, callback);

        return objW;
    }

    /**
     * 1. Hook com.tencent.mm.plugin.luckymoney.b.j.a(int,int,string,l) method, to get response after send timestamp request.
     * 2. After get the server response,then get timestamp from the response data. Then send a request to get luckymoney.
     */
    private static void registerPreGrabReqCallback(final XC_LoadPackage.LoadPackageParam lpparam, final PreGrabReqCallback callback) {
        Log.e(TAG, "registerPreGrabReqCallback");
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.model.w", lpparam.classLoader, "onSceneEnd",
                int.class, int.class, String.class, XposedHelpers.findClass("com.tencent.mm.aj.q", lpparam.classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int arg1 = (int) param.args[0];
                        int arg2 = (int) param.args[1];
                        Object preGrabRsp = param.args[3];
                        if (arg1 == 0 &&
                                arg2 == 0
                                && preGrabRsp.getClass().getName().equals("com.tencent.mm.plugin.luckymoney.model.ba")) {
                            callback.onReceive(preGrabRsp);
                        }
                    }
                });
    }

    /**
     * Create com.tencent.mm.plugin.luckymoney.model.bb or com.tencent.mm.plugin.luckymoney.model.ba object that will be used while send pre luckmonkey request
     *
     * @param lpparam
     * @param luckyMoneyMsg
     * @return com.tencent.mm.plugin.luckymoney.model.aq
     */
    public static Object createPreLuckyMoneyParam(final XC_LoadPackage.LoadPackageParam lpparam, LuckyMoneyMsg luckyMoneyMsg) {
        Class clz;
        if (luckyMoneyMsg.isUnionSceneId()) {
            clz = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.bb", lpparam.classLoader);
        } else {
            clz = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.ba", lpparam.classLoader);
        }
        Object objAQ = XposedHelpers.newInstance(clz, luckyMoneyMsg.channelid, luckyMoneyMsg.sendid, luckyMoneyMsg.nativeurl, luckyMoneyMsg.way, luckyMoneyMsg.version);
        if (LuckyMoneyConfig.DEBUG) {
            Log.e(TAG, "createPreLuckyMoneyParam=" + objAQ.toString());
        }
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
    public static Object createLuckyMoneyReqParam(final Object preLuckMoneyRsp, final LuckyMoneyMsg luckyMoneyMsg, final XC_LoadPackage.LoadPackageParam lpparam) {
        int msgType = (int) XposedHelpers.getObjectField(preLuckMoneyRsp, "msgType");
        int channelId = XposedHelpers.getIntField(preLuckMoneyRsp, "channelId");
        String sendId = (String) XposedHelpers.getObjectField(preLuckMoneyRsp, "wVk");
        String nativeUrl = (String) XposedHelpers.getObjectField(preLuckMoneyRsp, "dEZ");

        Class clzAC = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.ac", lpparam.classLoader);
        String headImg = (String) XposedHelpers.callStaticMethod(clzAC, "dEb");

        Class clzU = XposedHelpers.findClass("com.tencent.mm.model.x", lpparam.classLoader);
        String nickName = (String) XposedHelpers.callStaticMethod(clzU, "aEs");

        String userName = luckyMoneyMsg.talker;

        String ver = "v1.0";

        String timingIdentifier = (String) XposedHelpers.getObjectField(preLuckMoneyRsp, "xcw");

        Class clz;
        Object obj;
        if (luckyMoneyMsg.isUnionSceneId()) {
            clz = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.aw", lpparam.classLoader);
            obj = XposedHelpers.newInstance(clz, msgType, channelId, sendId, nativeUrl, headImg, nickName, userName, ver, timingIdentifier);
        } else {
            clz = XposedHelpers.findClass("com.tencent.mm.plugin.luckymoney.model.av", lpparam.classLoader);
            obj = XposedHelpers.newInstance(clz, msgType, channelId, sendId, nativeUrl, headImg, nickName, userName, ver, timingIdentifier, "");
        }
        return obj;
    }
}
