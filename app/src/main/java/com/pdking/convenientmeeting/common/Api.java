package com.pdking.convenientmeeting.common;

/**
 * @author liupeidong
 * Created on 2019/3/10 20:18
 */
public class Api {

    public static String SMSSendApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/phone/getVerificationCode.do";

    public static String[] SMSSendHeader = {"Content-Type", "application/x-www-form-urlencoded"};

    public static String []SMSSendBody = {"phoneNumber"};

    public static String SMSVerificationApi = "http://www.shidongxuan.top/smartMeeting_Web/phone/judgeCode.do";

    public static String[] SMSVerificationHeader = {"Content-Type", "application/x-www-form-urlencoded"};

    public static String []SMSVerificationBody = {"phoneNumber","code"};

}
