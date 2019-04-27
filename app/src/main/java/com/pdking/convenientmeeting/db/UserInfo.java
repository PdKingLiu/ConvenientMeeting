package com.pdking.convenientmeeting.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/2/7 12:19
 */
public class UserInfo extends LitePalSupport implements Parcelable {
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
    @SerializedName("id")
    public int userId;
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

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        userId = in.readInt();
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

    public static Creator<UserInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
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

    public void setId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getFaceData() {
        return faceData;
    }

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
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
