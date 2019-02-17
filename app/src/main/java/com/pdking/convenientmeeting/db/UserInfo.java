package com.pdking.convenientmeeting.db;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.GenderInfo;
import com.arcsoft.face.LivenessInfo;

import org.litepal.crud.LitePalSupport;

/**
 * @author liupeidong
 * Created on 2019/2/7 12:19
 */
public class UserInfo extends LitePalSupport implements Parcelable{

    private String name;

    private String phoneNumber;

    private String password;

    private String sex;

    private String email;

    private Bitmap icon;

    private FaceInfo faceInfo;

    private AgeInfo ageInfo;

    private GenderInfo genderInfo;

    private Face3DAngle face3DAngle;

    private LivenessInfo livenessInfo;

    private FaceFeature faceFeature;

    public UserInfo() {

    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                ", icon=" + icon +
                ", faceInfo=" + faceInfo +
                ", ageInfo=" + ageInfo +
                ", genderInfo=" + genderInfo +
                ", face3DAngle=" + face3DAngle +
                ", livenessInfo=" + livenessInfo +
                ", faceFeature=" + faceFeature +
                '}';
    }

    protected UserInfo(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
        password = in.readString();
        sex = in.readString();
        email = in.readString();
        icon = in.readParcelable(Bitmap.class.getClassLoader());
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

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }

    public void setAgeInfo(AgeInfo ageInfo) {
        this.ageInfo = ageInfo;
    }

    public void setGenderInfo(GenderInfo genderInfo) {
        this.genderInfo = genderInfo;
    }

    public void setFace3DAngle(Face3DAngle face3DAngle) {
        this.face3DAngle = face3DAngle;
    }

    public void setLivenessInfo(LivenessInfo livenessInfo) {
        this.livenessInfo = livenessInfo;
    }

    public void setFaceFeature(FaceFeature faceFeature) {
        this.faceFeature = faceFeature;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public AgeInfo getAgeInfo() {
        return ageInfo;
    }

    public GenderInfo getGenderInfo() {
        return genderInfo;
    }

    public Face3DAngle getFace3DAngle() {
        return face3DAngle;
    }

    public LivenessInfo getLivenessInfo() {
        return livenessInfo;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public Bitmap getIcon() {
        return icon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(password);
        dest.writeString(sex);
        dest.writeString(email);
        dest.writeParcelable(icon, flags);
    }
}
