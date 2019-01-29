package com.lzp.luckymoney;

import android.net.Uri;

import org.json.JSONObject;

public class LuckyMoneyMsg {
    public final String nativeurl;
    public final String sendid;
    public final int channelid;
    public final String talker;
    public final String version = "v1.0";
    public final int way;


    private LuckyMoneyMsg(String nativeurl, String sendid, int channelid, String talker, int way) {
        this.nativeurl = nativeurl;
        this.sendid = sendid;
        this.channelid = channelid;
        this.talker = talker;
        this.way = way;
    }

    @Override
    public String toString() {
        return "nativeurl:" + nativeurl + ","
                + "sendid:" + sendid + "," +
                "channelid:" + channelid + "," +
                "talker:" + talker +
                "way:" + way;
    }

    public static LuckyMoneyMsg createLuckyMoneyMsg(JSONObject jsonObject, String talker) {
        if (jsonObject != null) {
            try {
                JSONObject msg = jsonObject.getJSONObject("msg");
                JSONObject wcpayinfo = msg.getJSONObject("appmsg").getJSONObject("wcpayinfo");
                String nativeurl = wcpayinfo.getString("nativeurl");

                Uri parse = Uri.parse(nativeurl);
                String sendid = parse.getQueryParameter("sendid");
                int channelid = Integer.valueOf(parse.getQueryParameter("channelid"));

                int way = talker.endsWith("@chatroom") ? 0 : 1;
                return new LuckyMoneyMsg(nativeurl, sendid, channelid, talker, way);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
