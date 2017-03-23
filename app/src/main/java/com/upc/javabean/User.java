package com.upc.javabean;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by 稻dao草re人n on 2017/1/14.
 */

public class User extends BmobUser{
    private String imageURL;
    private String nickName;
    private Integer age;
    private String adress;
    private String school;
    private String work;
    private List<String> outKinds;

    public List<String> getInKinds() {
        return inKinds;
    }

    public void setInKinds(List<String> inKinds) {
        this.inKinds = inKinds;
    }

    public List<String> getOutKinds() {
        return outKinds;
    }

    public void setOutKinds(List<String> outKinds) {
        this.outKinds = outKinds;
    }

    private List<String> inKinds;

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getNickName() {
        return nickName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
