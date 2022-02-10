package com.luckynum.model;

public class Notif {

    private String appPckName;
    private String notifTitle;
    private String notifText;


    public Notif() {
    }

    public Notif(String appPckName, String notifTitle, String notifText) {
        this.appPckName = appPckName;
        this.notifTitle = notifTitle;
        this.notifText = notifText;
    }
    public String getAppPckName() {
        return appPckName;
    }

    public void setAppPckName(String appPckName) {
        this.appPckName = appPckName;
    }

    public String getNotifTitle() {
        return notifTitle;
    }

    public void setNotifTitle(String notifTitle) {
        this.notifTitle = notifTitle;
    }

    public String getNotifText() {
        return notifText;
    }

    public void setNotifText(String notifText) {
        this.notifText = notifText;
    }


}
