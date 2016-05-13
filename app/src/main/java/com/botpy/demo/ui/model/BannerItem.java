package com.botpy.demo.ui.model;

/**
 * Created by weiTeng on 2016/4/13.
 */
public class BannerItem extends BaseModel {

    public String imgUrl;
    public String title;

    @Override
    public String toString() {
        return "BannerItem{" +
                "imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
