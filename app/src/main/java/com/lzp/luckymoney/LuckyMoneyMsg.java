package com.lzp.luckymoney;

import org.json.JSONObject;

public class LuckyMoneyMsg {
    public final String nativeurl;

    private LuckyMoneyMsg(Builder builder) {
        this.nativeurl = builder.nativeurl;
    }

    public static class Builder {
        private String nativeurl;
        private String fromusername;

        public LuckyMoneyMsg build(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    JSONObject msg = jsonObject.getJSONObject("msg");
                    fromusername = msg.getString("fromusername");
                    JSONObject wcpayinfo = msg.getJSONObject("appmsg").getJSONObject("wcpayinfo");
                    nativeurl = wcpayinfo.getString("nativeurl");
                    return new LuckyMoneyMsg(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
