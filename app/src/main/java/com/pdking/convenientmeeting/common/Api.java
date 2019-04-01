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

    public static String GetUserInfoApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/user/getOneByPhone.do";

    public static String[] GetUserInfoBody = {"phone"};

    public static String UpDateUserInfoApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/user/update.do";

    public static String[] UpDateUserInfoHeader = {"Content-Type", "multipart/form-data"};

    public static String[] UpDateUserInfoBody = {"id", "phone", "email", "sex", "avatar"};

    public static String UpDateUserPasswordApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/user/updatePassword.do";

    public static String[] UpDateUserPasswordHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] UpDateUserPasswordBody = {"userId", "oldPassword", "newPassword"};

    public static String GetMeetingRoomApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/room/getAllRooms.do";

    public static String FindPasswordApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/user/forgetPassword.do";

    public static String[] FindPasswordHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] FindPasswordBody = {"code", "phoneNumber", "newPassword"};

    public static String GetOneMeetingRoomMessageApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/room/getRoomById.do";

    public static String[] GetOneMeetingRoomMessageBody = {"roomId"};

    public static String WhetherBookApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/whetherBook.do";

    public static String[] WhetherBookHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] WhetherBookBody = {"roomId", "startTime", "endTIme"};

    public static String RequestBookApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/bookMeeting.do";

    public static String[] RequestBookHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] RequestBookBody = {"meetingName", "meetingIntro", "roomId",
            "masterId", "startTime", "endTime"};

    public static String RequestUserMeetingListApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/getUserMeetings.do";

    public static String[] RequestUserMeetingListHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] RequestUserMeetingListBody = {"userId", "type"};

    public static String MeetingAddMemberApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/inviteMeetingMember.do";

    public static String[] MeetingAddMemberHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] MeetingAddMemberBody = {"userId", "meetingId"};

}
