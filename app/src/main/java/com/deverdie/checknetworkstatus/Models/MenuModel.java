package com.deverdie.checknetworkstatus.Models;

public class MenuModel {
    private String header;
    private String subheader;
    private String title;
    private String desc;
    private String linkTo;

    public MenuModel(String header, String subheader, String title, String desc, String linkTo) {
        this.header = header;
        this.subheader = subheader;
        this.title = title;
        this.desc = desc;
        this.linkTo = linkTo;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
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

    public String getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(String linkTo) {
        this.linkTo = linkTo;
    }
}
