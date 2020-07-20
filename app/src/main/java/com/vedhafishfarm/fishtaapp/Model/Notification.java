package com.vedhafishfarm.fishtaapp.Model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private boolean ispost;
    private String ntoken;

    public Notification(String userid, String text, String postid, boolean ispost, String ntoken) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ispost = ispost;
        this.ntoken = ntoken;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }

    public String getNtoken() {
        return ntoken;
    }

    public void setNtoken(String ntoken) {
        this.ntoken = ntoken;
    }
}
