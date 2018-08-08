package cn.yanss.m.kitchen.cws.entity.response;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse implements Serializable{

    private String pid;
    private String productName;
    private double price;
    private double specialPrice;
    private Integer status;
    private Integer count;
    private double boxPrice;
    private Integer weight;
    private JSONArray gifts;
    private Double facePrice;


    public Double getFacePrice() {
        return facePrice;
    }

    public void setFacePrice(Double facePrice) {
        this.facePrice = facePrice;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public double getBoxPrice() {
        return boxPrice;
    }

    public void setBoxPrice(double boxPrice) {
        this.boxPrice = boxPrice;
    }


}