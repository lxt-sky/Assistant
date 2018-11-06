package com.sanmen.bluesky.assistant.ui.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.entity.LocationDataBean;

import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/11/6
 * @description
 */
public class HistoryAdapter extends BaseQuickAdapter<LocationDataBean,BaseViewHolder> {

    String[] alarmArray = new String[]{"电话","短信","电话+短信"};
    public HistoryAdapter(@Nullable List<LocationDataBean> data) {
        super(R.layout.item_history,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocationDataBean item) {
        helper.setText(R.id.tvRecordingTitle,alarmArray[item.getAlarmType()]);
        helper.setText(R.id.tvRecordingValue,"经度: "+item.getLongitude()+" 纬度: "+item.getLatitude());
        helper.setText(R.id.tvRecordingTime,item.getAlarmTime());

    }
}
