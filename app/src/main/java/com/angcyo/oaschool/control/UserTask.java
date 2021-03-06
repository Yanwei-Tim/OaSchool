package com.angcyo.oaschool.control;

import com.angcyo.oaschool.components.RConstant;
import com.angcyo.oaschool.event.UserEvent;
import com.angcyo.oaschool.mode.TaskRunnable;
import com.angcyo.oaschool.mode.bean.SchoolResult;
import com.angcyo.oaschool.mode.bean.UserResult;
import com.angcyo.oaschool.util.OkHttpClientManager;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

/**
 * Created by angcyo on 15-09-12-012.
 * <p/>
 * 用户登录 的任务;
 */
public class UserTask extends TaskRunnable {

    private String userName;
    private String userPw;

    public UserTask(String userName, String userPw) {
        this.userName = userName;
        this.userPw = userPw;
    }

    private static String getUrl(String userName, String passWord) {//用户登录信息
        String url = String.format("http://%s/APP/login.asp?userName=%s&password=%s", RConstant.SER_IP, userName, passWord);
        return url;
    }

    private static String getInfoUrl() {//学校信息
        String url = String.format("http://%s/APP/schoolname.asp", RConstant.SER_IP);
        return url;
    }

    @Override
    public void run() {
        UserEvent event = new UserEvent();
        try {
            byte[] bytes = OkHttpClientManager.getAsBytes(getInfoUrl());
            String response = new String(bytes, "gbk");
            response = response.substring(response.indexOf("<body>") + 6, response.indexOf("</body>")).trim();
            Gson gson = new Gson();
            SchoolResult schoolResult = gson.fromJson(response, SchoolResult.class);

            bytes = OkHttpClientManager.getAsBytes(getUrl(userName, userPw));
            response = new String(bytes, "gbk");
            UserResult userResult = gson.fromJson(response, UserResult.class);

            event.schoolResult = schoolResult;
            event.result = userResult;
            event.code = RConstant.CODE_OK;
        } catch (Exception e) {
            e.printStackTrace();
            event.code = RConstant.CODE_ER;
        }
        EventBus.getDefault().post(event);
    }
}
