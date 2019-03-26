package com.example.myapp;

public class UserInfo {
    private String userName;
    private String passWord;
    private String phoneNum;
    private String defAdr;
    public UserInfo(){
        userName="";
        passWord="";
        phoneNum="";
        defAdr="";
    }
    public UserInfo(String uN,String pW){
        userName=uN;
        passWord=pW;
    }
    public UserInfo(String uN,String pW,String pN,String dA){
        userName=uN;
        passWord=pW;
        phoneNum=pN;
        defAdr=dA;
    }
    public String getUserName(){
        return userName;
    }
    public String getPassWord(){
        return passWord;
    }
    public String getPhoneNum(){
        return phoneNum;
    }
    public String getDefAdr(){
        return defAdr;
    }
    public void setUserName(String uN){
        userName=uN;
    }
    public void setPassWord(String pW){
        passWord=pW;
    }
    public void setPhoneNum(String pN){
        phoneNum=pN;
    }
    public void setDefAdr(String dA){
        defAdr=dA;
    }


}