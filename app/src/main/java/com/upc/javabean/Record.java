package com.upc.javabean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 稻dao草re人n on 2017/1/15.
 */

public class Record extends BmobObject {
    private String userId;//用户id
    private String type;//收入或支出
    private String location;//地理位置信息
    private String method;//方式
    private String kind;//明细和类别
    private String imageUrl;//图片
    private Double number;//数值
    private Integer typeImage;//设置收入支出图片
    private boolean deleted;//是否已经删除
    private String coin;//货币类型
    private String remark;//备注
    private String locationDetail;//定位详细信息
    private String pocketId;//该记录所属钱包的id
    private Integer month;//该记录的数据产生月份 用于统计查询

    public String getPocketId() {
        return pocketId;
    }

    public void setPocketId(String pocketId) {
        this.pocketId = pocketId;
    }

    public String getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(String locationDetail) {
        this.locationDetail = locationDetail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getTypeImage() {
        return typeImage;
    }

    public void setTypeImage(Integer typeImage) {
        this.typeImage = typeImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
