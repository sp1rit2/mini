package com.example.cc;

public class shops {

    private String name,title,desc;
    private int img;
    private String img1;


    public shops(String name, String title, String desc, int img) {
        this.name = name;
        this.title = title;
        this.desc = desc;
        this.img = img;
    }
    public shops(String name, String img1) {
        this.name = name;
        this.img1 = img1;
    }


    public shops(String name) {
        this.name = name;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }



    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
