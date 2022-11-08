package com.example.messaginghub;

public class PeopleModel {
    String username;
    String imgid;

    public String getImgid(){
        return imgid;
    }
    public String getUsername() {
        return username;
    }

    public PeopleModel(String username,String imgid) {
        this.username = username;
        this.imgid = imgid;
    }


}
