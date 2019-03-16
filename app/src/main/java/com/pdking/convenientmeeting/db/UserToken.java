package com.pdking.convenientmeeting.db;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/3/16 11:28
 */
public class UserToken extends LitePalSupport {

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
