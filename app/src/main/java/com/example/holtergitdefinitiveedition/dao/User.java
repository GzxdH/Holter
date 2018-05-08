package com.example.holtergitdefinitiveedition.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * 用户
 */

@Entity
public class User implements Serializable {

    static final long serialVersionUID = 42L;
    @Id
    @Unique
    private Long uid;
    @Property(nameInDb = "uname")
    private String userName;
    @Property(nameInDb = "unum")
    private int userNum;
    @Property(nameInDb = "usex")
    private String userSex;
    @Property(nameInDb = "udate")
    private String userDate;
    @Property(nameInDb = "uheight")
    private String userHeight;
    @Property(nameInDb = "uweight")
    private String userWeight;
    @Property(nameInDb = "udocnum")
    private String userDocNum;
    @Property(nameInDb = "uresult")
    private String userResult;

    @Keep
    public User(Long uid, String userName, int userNum, String userSex, String userDate,
                String userHeight, String userWeight, String userDocNum, String userResult) {
        this.uid = uid;
        this.userName = userName;
        this.userNum = userNum;
        this.userSex = userSex;
        this.userDate = userDate;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.userDocNum = userDocNum;
        this.userResult = userResult;
    }

    @Keep
    public User() {
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(String userHeight) {
        this.userHeight = userHeight;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }

    public String getUserDocNum() {
        return userDocNum;
    }

    public void setUserDocNum(String userDocNum) {
        this.userDocNum = userDocNum;
    }

    public String getUserResult() {
        return userResult;
    }

    public void setUserResult(String userResult) {
        this.userResult = userResult;
    }
}
