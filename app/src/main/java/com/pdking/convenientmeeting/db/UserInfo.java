package com.pdking.convenientmeeting.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/2/7 12:19
 */
public class UserInfo extends LitePalSupport implements Parcelable{
    @SerializedName("id")
    public int id;
    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;
    @SerializedName("sex")
    public String sex;
    @SerializedName("role")
    public int role;
    @SerializedName("phone")
    public String phone;
    @SerializedName("email")
    public String email;
    @SerializedName("avatarUrl")
    public String avatarUrl;
    @SerializedName("faceUrl")
    public String faceUrl;
    @SerializedName("createTime")
    public long createTime;
    @SerializedName("updateTime")
    public long updateTime;
    @SerializedName("faceData")
    public String faceData;

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", role=" + role +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", faceUrl='" + faceUrl + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", faceData='" + faceData + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSex() {
        return sex;
    }

    public int getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public String getFaceData() {
        return faceData;
    }

    public static Creator<UserInfo> getCREATOR() {
        return CREATOR;
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        id = in.readInt();
        username = in.readString();
        password = in.readString();
        sex = in.readString();
        role = in.readInt();
        phone = in.readString();
        email = in.readString();
        avatarUrl = in.readString();
        faceUrl = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        faceData = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(sex);
        dest.writeInt(role);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(avatarUrl);
        dest.writeString(faceUrl);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeString(faceData);
    }
}
