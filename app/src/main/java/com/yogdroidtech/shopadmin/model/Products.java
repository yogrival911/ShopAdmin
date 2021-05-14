package com.yogdroidtech.shopadmin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Products implements Serializable {

    private String productName;
    private String category;
    private String subCategory;
    private int id;
    private int markPrice;
    private int sellPrice;
    private Boolean isWishList;
    private String unit ;
    private String imgUrl;
    private List<String> imgUrlList = new ArrayList<>();

    public Products(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Products(String productName, String category, String subCategory, int id, int markPrice, int sellPrice, Boolean isWishList, String unit, List<String> imgUrlList) {
        this.productName = productName;
        this.category = category;
        this.subCategory = subCategory;
        this.id = id;
        this.markPrice = markPrice;
        this.sellPrice = sellPrice;
        this.isWishList = isWishList;
        this.unit = unit;
        this.imgUrlList = imgUrlList;
    }

    public Products() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMarkPrice() {
        return markPrice;
    }

    public void setMarkPrice(int markPrice) {
        this.markPrice = markPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Boolean getWishList() {
        return isWishList;
    }

    public void setWishList(Boolean wishList) {
        isWishList = wishList;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
