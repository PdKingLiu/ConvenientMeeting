package com.pdking.convenientmeeting.weight;

/**
 * @author liupeidong
 * Created on 2019/2/25 13:47
 */
public class PopMenuItem {

    public int id; //标识
    public int resId; //资源图标
    public String text;//文字

    public PopMenuItem(int id, String text) {
        this.id = id;
        this.resId = 0;
        this.text = text;
    }

    public PopMenuItem(int id, int resId, String text) {
        this.id = id;
        this.resId = resId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
