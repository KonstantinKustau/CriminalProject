package com.example.ibanez_xiphos.criminalproject.model;

import android.text.format.DateFormat;

import java.util.Date;
import java.util.UUID;

public class Record {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mContact;

    public Record(){
        this(UUID.randomUUID());
    }

    public Record(UUID id){
        mId = id;
        mDate = new Date();
    }
    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate(){
        return mDate;
    }

    public String getContact() {
        return mContact;
    }

    public void setContact(String Contact) {
        this.mContact = Contact;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getDateString() {
        return DateFormat.format("EEEE, MMMM dd, yyyy", mDate).toString();
    }

    public String getTimeString(){
        return DateFormat.format("kk:mm", mDate).toString();
    }

    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

}
