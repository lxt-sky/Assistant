package com.sanmen.bluesky.assistant.manager;

import android.content.Context;


import io.paperdb.Paper;

public class PaperManager {

    //报警方式
    private static final String ALARM_TYPE="ALARM_TYPE";
    //报警电话
    private static final String ALARM_PHONE = "ALARM_PHONE";

    private static PaperManager paperManager;

    private PaperManager() {

    }

    public static PaperManager getPaperManager() {
        if(paperManager == null){
            paperManager = new PaperManager();
        }
        return paperManager;
    }

    public void init(Context context) {
        Paper.init(context);
    }


    public int getAlarmType(){
        return Paper.book().read(ALARM_TYPE,0);
    }

    public void setAlarmType(int val){

        Paper.book().write(ALARM_TYPE,val);
    }

    public String getAlarmPhone(){
        return Paper.book().read(ALARM_PHONE);
    }

    public void setAlarmPhone(String val){
        Paper.book().write(ALARM_PHONE,val);
    }



}
