package com.yogdroidtech.shopadmin.model;

import java.util.List;

public class Category {
    String catImgUrl;
    List<String> subCategories;

    public Category(String catImgUrl, List<String> subCategories) {
        this.catImgUrl = catImgUrl;
        this.subCategories = subCategories;
    }

    public String getCatImgUrl() {
        return catImgUrl;
    }

    public void setCatImgUrl(String catImgUrl) {
        this.catImgUrl = catImgUrl;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<String> subCategories) {
        this.subCategories = subCategories;
    }
}
