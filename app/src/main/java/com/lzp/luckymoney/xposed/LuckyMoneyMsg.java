package com.lzp.luckymoney.xposed;

import android.net.Uri;

import org.json.JSONObject;

public class LuckyMoneyMsg {
    public static final long LUCKY_MONEY_C2C_MSG_TYPE = 436207665;
    public static final long LUCKY_MONEY_GROUP_MSG_TYPE = 436207665;
    public static final int UNION_SCENID = 1005;

    public final String nativeurl;
    public final String sendid;
    public final int channelid;
    public final String talker;
    public final String version = "v1.0";
    public final int way;
    public final String paymsgid;
    public final int sceneid;

    private LuckyMoneyMsg(String nativeurl, String sendid, int channelid, String talker, int way, String paymsgid, int sceneid) {
        this.nativeurl = nativeurl;
        this.sendid = sendid;
        this.channelid = channelid;
        this.talker = talker;
        this.way = way;
        this.paymsgid = paymsgid;
        this.sceneid = sceneid;
    }

    public boolean isUnionSceneId() {
        return this.sceneid == UNION_SCENID;
    }

    @Override
    public String toString() {
        return "nativeurl:" + nativeurl + ","
                + "sendid:" + sendid + "," +
                "channelid:" + channelid + "," +
                "talker:" + talker +
                "sceneid:" + sceneid +
                "way:" + way;
    }

    public static LuckyMoneyMsg createLuckyMoneyMsg(JSONObject jsonObject, String talker) {
        if (jsonObject != null) {
            try {
                JSONObject msg = jsonObject.getJSONObject("msg");
                JSONObject wcpayinfo = msg.getJSONObject("appmsg").getJSONObject("wcpayinfo");
                String nativeurl = wcpayinfo.getString("nativeurl");
                String paymsgid = wcpayinfo.getString("paymsgid");
                int sceneid = wcpayinfo.getInt("sceneid");

                Uri parse = Uri.parse(nativeurl);
                String sendid = parse.getQueryParameter("sendid");
                int channelid = Integer.valueOf(parse.getQueryParameter("channelid"));

                int way = talker.endsWith("@chatroom") ? 0 : 1;
                return new LuckyMoneyMsg(nativeurl, sendid, channelid, talker, way, paymsgid, sceneid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
