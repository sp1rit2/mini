package com.example.cc;

public class Shopkeeper {
    private String sname,shopno;
    private String pin,cpin;
    private String mn,email;

    public Shopkeeper() {

    }

    public String getStatusOfVerification() {
        return statusOfVerification;
    }

    public void setStatusOfVerification(String statusOfVerification) {
        this.statusOfVerification = statusOfVerification;
    }

    String statusOfVerification   ;                     //if 0 not verifies if 1 verified

    public Shopkeeper(String statusOfVerification) {
        this.statusOfVerification = statusOfVerification;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    private int flag=0;

    public String getSname() {
        return sname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getShopno() {
        return shopno;
    }

    public void setShopno(String shopno) {
        this.shopno = shopno;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCpin() {
        return cpin;
    }

    public void setCpin(String cpin) {
        this.cpin = cpin;
    }

    public String getMn() {
        return mn;
    }

    public void setMn(String mn) {
        this.mn = mn;
    }
}
