package com.lzp.luckymoney.xposed;

public final class LuckyMoneyReq {
    public final int msgType = 1;
    public final int bxk;
    public final String kLZ;
    public final String ceR;
    public final String kRC;
    public final String baX;
    public final String GH;
    public final String username;
    public final String version = "v1.0";

    private LuckyMoneyReq(Builder builder) {
        this.bxk = builder.bxk;
        this.kLZ = builder.kLZ;
        this.ceR = builder.ceR;
        this.kRC = builder.kRC;
        this.baX = builder.baX;
        this.GH = builder.GH;
        this.username = builder.username;
    }

    @Override
    public String toString() {
        return "LuckyMoneyReq{" +
                "msgType=" + msgType +
                ", bxk=" + bxk +
                ", kLZ='" + kLZ + '\'' +
                ", ceR='" + ceR + '\'' +
                ", kRC='" + kRC + '\'' +
                ", baX='" + baX + '\'' +
                ", GH='" + GH + '\'' +
                ", username='" + username + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public static class Builder {
        private int bxk;
        private String kLZ;
        private String ceR;
        private String kRC;
        private String baX;
        private String GH;
        private String username;

        public Builder bxk(int bxk) {
            this.bxk = bxk;
            return this;
        }

        public Builder kLZ(String kLZ) {
            this.kLZ = kLZ;
            return this;
        }

        public Builder ceR(String ceR) {
            this.ceR = ceR;
            return this;
        }

        public Builder kRC(String kRC) {
            this.kRC = kRC;
            return this;
        }

        public Builder baX(String baX) {
            this.baX = baX;
            return this;
        }

        public Builder GH(String GH) {
            this.GH = GH;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public LuckyMoneyReq build() {
            return new LuckyMoneyReq(this);
        }
    }
}
