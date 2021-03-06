package com.sanmen.bluesky.assistant.manager;

import android.content.Context;


import com.sanmen.bluesky.assistant.entity.LocationDataBean;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class PaperManager {

    //报警方式
    private static final String ALARM_TYPE="ALARM_TYPE";
    //报警电话
    private static final String ALARM_PHONE = "ALARM_PHONE";
    //引导
    private static final String NEED_GUIDE = "NEED_GUIDE";

    private static final String HISTORY_LIST = "HISTORY_LIST";

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
        return Paper.book().read(ALARM_PHONE,"");
    }

    public void setAlarmPhone(String val){
        Paper.book().write(ALARM_PHONE,val);
    }

    public boolean isNeedGuide(){
        return Paper.book().read(NEED_GUIDE,true);
    }

    public void setIsNeedGuide(boolean val){
        Paper.book().write(NEED_GUIDE,val);
    }

    public List<LocationDataBean> getHistoryList(){
        return Paper.book().read(HISTORY_LIST,new ArrayList<LocationDataBean>());
    }

    public void setHistoryList(List<LocationDataBean> list){
        Paper.book().write(HISTORY_LIST,list);
    }

}
