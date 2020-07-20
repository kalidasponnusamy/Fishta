package com.vedhafishfarm.fishtaapp.Notifications;

public class Data {
    private String Title;
    private String Message;
    private String ImageUrl;
    public Data(String title, String message, String imageURL) {
        Title = title;
        Message = message;
        ImageUrl = imageURL;
    }
    public Data(){}public String getTitle() {return Title;}
    public void setTitle(String title) {Title = title;}
    public String getMessage() {return Message;}
    public void setMessage(String message) {Message = message;}

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
