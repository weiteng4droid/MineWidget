package com.weiteng.cache.model;

/**
 * Created by weiTeng on 2016/5/3.
 */
public class BannerItem extends BaseModel {

    public String url;
    public String name;
    public String descript;

    public double amount;


    @Override
    public String toString() {
        return "BannerItem{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", descript='" + descript + '\'' +
                ", amount=" + amount +
                '}';
    }
}
