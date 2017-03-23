package com.upc.javabean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 稻dao草re人n on 2017/1/30.
 */

public class Pocket extends BmobObject {
    private String userId;
    private String kind;
    private Double number;
    private String coinType;
    public boolean deleted;

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
