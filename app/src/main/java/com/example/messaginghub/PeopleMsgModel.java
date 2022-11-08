package com.example.messaginghub;

public class PeopleMsgModel {
    String msg_imgid;
    String msg_username;
    String user_msg;
    String  lastmsg_time;
    String Receiverphoneno;
    String Roomid;
    Boolean Frommain;
    Long unreadcount;
    public Long getUnreadcount() {
        return unreadcount;
    }

    public void setUnreadcount(Long unreadcount) {
        this.unreadcount = unreadcount;
    }

    public Boolean getFrommain() {
        return Frommain;
    }

    public void setFrommain(Boolean frommain) {
        Frommain = frommain;
    }

    public String getReceiverphoneno() {
        return Receiverphoneno;
    }

    public void setReceiverphoneno(String receiverphoneno) {
        Receiverphoneno = receiverphoneno;
    }

    public void setLastmsg_time(String lastmsg_time) {
        this.lastmsg_time = lastmsg_time;
    }
    public String getRoomid() {
        return Roomid;
    }

    public void setRoomid(String roomid) {
        Roomid = roomid;
    }



    public String getMsg_imgid() {
        return msg_imgid;
    }

    public String getMsg_username() {
        return msg_username;
    }

    public String getUser_msg() {
        return user_msg;
    }

    public String getlastMsg_time() {
        return lastmsg_time;
    }


    public PeopleMsgModel(String msg_imgid, String msg_username,String Receiverphoneno,String Roomid,String lastmsg_time,Boolean Frommain,Long unreadcount) {
        this.msg_imgid = msg_imgid;
        this.msg_username = msg_username;
        this.Receiverphoneno=Receiverphoneno;
        this.Roomid=Roomid;
        this.lastmsg_time=lastmsg_time;
        this.Frommain=Frommain;
        this.unreadcount=unreadcount;
    }
    public PeopleMsgModel(String msg_username, String msg_imgid, String Receiverphoneno, String Roomid, Boolean Frommain) {
        this.msg_username = msg_username;
        this.msg_imgid = msg_imgid;
        this.Receiverphoneno = Receiverphoneno;
        this.Roomid=Roomid;
        this.Frommain=Frommain;
    }

}
