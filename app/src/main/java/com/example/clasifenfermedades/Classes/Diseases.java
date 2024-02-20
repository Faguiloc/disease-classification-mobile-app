package com.example.clasifenfermedades.Classes;

import android.content.res.Resources;

public class Diseases {

    String title, desc;
    String img;

    public Diseases(String title, String desc, String img){
        this.title = title;
        this.desc  = desc;
        this.img   = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
