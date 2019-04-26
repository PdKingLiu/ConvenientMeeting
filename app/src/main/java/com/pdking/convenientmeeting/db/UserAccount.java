package com.pdking.convenientmeeting.db;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/3/16 11:38
 */
public class UserAccount extends LitePalSupport {

    public UserAccount() {
    }

    public UserAccount(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    private String phone;

    private String password;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}
