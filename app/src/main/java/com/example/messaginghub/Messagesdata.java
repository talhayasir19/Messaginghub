package com.example.messaginghub;

import android.provider.ContactsContract;

public class Messagesdata{
    private String Message;
    private String Senderid;
    private String Phoneno;
    private String Timestamp;
    private String Date;
    private Boolean Isselected;
    Boolean Isseen;
    public Boolean getIsselected() {
        return Isselected;
    }

    public void setIsselected(Boolean isselected) {
        Isselected = isselected;
    }
    public Boolean getIsseen() {
        return Isseen;
    }

    public void setIsseen(Boolean isseen) {
        Isseen = isseen;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getPhoneno() {
        return Phoneno;
    }

    public void setPhoneno(String phoneno) {
        Phoneno = phoneno;
    }


    public Messagesdata(String message, String senderid,String timestamp,String date,Boolean isseen,Boolean Isselected) {
        Message = message;
        Senderid = senderid;
        Timestamp=timestamp;
        Date=date;
        Isseen=isseen;
        this.Isselected=Isselected;

    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSenderid() {
        return Senderid;
    }

    public void setSenderid(String senderid) {
        Senderid = senderid;
    }


}
