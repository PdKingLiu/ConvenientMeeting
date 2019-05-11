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

    public static String[] WhetherBookBody = {"roomId", "startTime", "endTime"};

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

    public static String GetMeetingByIdApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/getMeetingById.do";

    public static String[] GetMeetingByIdHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] GetMeetingByIdBody = {"meetingId"};

    public static String UpLoadFileApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/uploadFile" +
            ".do";

    public static String[] UpLoadFileHeader = {"Content-Type",
            "multipart/form-data"};

    public static String[] UpLoadFileBody = {"uploadFile", "meetingId", "userId"};

    public static String LoadMeetingFileApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/getMeetingFiles.do";

    public static String[] LoadMeetingFileHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] LoadMeetingFileBody = {"meetingId"};

    public static String GetMeetingNoteApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/getMeetingNote.do";

    public static String[] GetMeetingNoteHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] GetMeetingNoteBody = {"meetingId", "userId"};

    public static String SetMeetingNoteApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/editNote.do";

    public static String[] SetMeetingNoteHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] SetMeetingNoteBody = {"meetingId", "userId", "note"};

    public static String StartMeetingApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting//startMeeting.do";

    public static String[] StartMeetingHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] StartMeetingBody = {"meetingId"};

    public static String FinishMeetingApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting//endMeeting.do";

    public static String[] FinishMeetingHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] FinishMeetingBody = {"meetingId"};

    public static String GetRoomMeetingListApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/meeting/getPageMeetingInfo.do";

    public static String[] GetRoomMeetingListHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] GetRoomMeetingListBody = {"roomId"};

    public static String SetVoteApi = "http://www.shidongxuan.top/smartMeeting_Web/vote/add";

    public static String[] SetVoteHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] SetVoteBody = {"meetingId", "publisherId", "topic", "selectWay",
            "remindTime", "createTime", "endTime", "options"};

    public static String GetVoteListApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/vote/specificInfo";

    public static String[] GetVoteListBody = {"meetingId", "userId"};

    public static String VoteApi = "http://www.shidongxuan.top/smartMeeting_Web/vote/userOption";

    public static String[] VoteHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] VoteBody = {"userId", "voteId", "optionIds"};

    public static String LeaveApi = "http://www.shidongxuan.top/smartMeeting_Web/user/applyLeave" +
            ".do";

    public static String[] LeaveHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] LeaveBody = {"userId", "meetingId"};

    public static String AddVideoApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/live/addMeeting";

    public static String[] AddVideoHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] AddVideoBody = {"liveName", "livePwd", "createId"};

    public static String GetVideoListApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/live/getList";

    public static String[] GetVideoListHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] ExitLiveRoomBody = {"liveId", "userId"};

    public static String ExitLiveRoomApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/live/quitLive";

    public static String[] ExitLiveRoomHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] EnterLiveRoomBody = {"liveId", "userId", "password"};

    public static String EnterLiveRoomApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/live/joinLive";

    public static String[] EnterLiveRoomHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

    public static String[] GetUserFileListBody = {"userId"};

    public static String GetUserFileListApi = "http://www.shidongxuan" +
            ".top/smartMeeting_Web/user/getMyFiles.do";

    public static String[] GetUserFileListHeader = {"Content-Type",
            "application/x-www-form-urlencoded"};

}
