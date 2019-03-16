package com.pdking.convenientmeeting.common;

/**
 * @author liupeidong
 * Created on 2019/3/10 20:18
 */
public class Api {

    public static String SMSSendApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/phone/getVerificationCode.do";

    public static String[] SMSSendHeader = {"Content-Type", "application/x-www-form-urlencoded"};

    public static String[] SMSSendBody = {"phoneNumber"};

    public static String SMSVerificationApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/phone/judgeCode.do";

    public static String[] SMSVerificationHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] SMSVerificationBody = {"phoneNumber", "code"};

    public static String RegisterApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/user/register.do";

    public static String[] RegisterHeader = {"Content-Type", "multipart/form-data"};

    public static String[] RegisterBody = {"username", "password", "sex", "phone", "faceData",
            "email", "avatar", "face"};

    public static String[] LoginHeader = {"Content-Type", "application/x-www-form-urlencoded"};

    public static String[] LoginBody = {"phone", "password"};

    public static String LoginApi = "http://www.shidongxuan.top/smartMeeting_Web/user/login.do";

}
