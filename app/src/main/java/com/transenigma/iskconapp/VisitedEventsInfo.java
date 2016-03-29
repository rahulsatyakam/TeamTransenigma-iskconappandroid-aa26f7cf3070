package com.transenigma.iskconapp;

import com.parse.ParseFile;

public class VisitedEventsInfo {
    ParseFile imageFile;
    String myEventDate, myEventCity, myEventTitle , eventObjectId;
    Boolean like;

    public String getEventObjectId() {
        return eventObjectId;
    }

    public void setEventObjectId(String eventObjectId) {
        this.eventObjectId = eventObjectId;
    }

    public Boolean getLike() {
        return like;
    }

    public VisitedEventsInfo(ParseFile imageFile, String eventObjectId, String myEventTitle, String myEventCity, String myEventDate, Boolean like) {
        this.imageFile = imageFile;
        this.myEventCity = myEventCity;
        this.myEventDate = myEventDate;
        this.myEventTitle = myEventTitle;
        this.eventObjectId = eventObjectId;
        this.like = like;

    }

    public void setImageFile(ParseFile imageFile) {
        this.imageFile = imageFile;
    }

    public ParseFile getImageFile() {

        return imageFile;
    }

    public String getMyEventDate() {
        return myEventDate;
    }

    public void setMyEventDate(String myEventDate) {
        this.myEventDate = myEventDate;
    }

    public String getMyEventCity() {
        return myEventCity;
    }

    public void setMyEventCity(String myEventCity) {
        this.myEventCity = myEventCity;
    }

    public String getMyEventTitle() {
        return myEventTitle;
    }

    public void setMyEventTitle(String myEventTitle) {
        this.myEventTitle = myEventTitle;
    }
}


