package com.pdking.convenientmeeting.db;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/3/16 11:38
 */
public class UserAccount extends LitePalSupport {

    private String phone;
    private String password;

    public UserAccount() {
    }

    public UserAccount(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
