package com.sanmen.bluesky.assistant.ui.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.manager.PaperManager;

import java.util.Objects;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class PhoneSettingFragment extends Fragment implements View.OnTouchListener, RadioGroup.OnCheckedChangeListener, TextWatcher {

    View rootView;

    EditText etPhoneValue;
    RelativeLayout relativeLayout;
    RadioGroup groupAlarmType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView==null){
            rootView=inflater.inflate(R.layout.fragment_guide_item3,container,false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayout();
    }

    private void initLayout() {
        etPhoneValue = rootView.findViewById(R.id.etPhoneValue);
        relativeLayout = rootView.findViewById(R.id.relativeLayout);
        groupAlarmType = rootView.findViewById(R.id.groupAlarmType);
        etPhoneValue.addTextChangedListener(this);

        relativeLayout.setOnTouchListener(this);
        etPhoneValue.setOnTouchListener(this);
        groupAlarmType.setOnCheckedChangeListener(this);
        groupAlarmType.check(R.id.rbItem1);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId()!=R.id.etPhoneValue){

            relativeLayout.setFocusableInTouchMode(true);
            relativeLayout.requestFocus();
            InputMethodManager manager;
            manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(etPhoneValue.getWindowToken(),0);
            }
        }

        return false;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i=0;i<group.getChildCount();i++){
            if (checkedId== group.getChildAt(i).getId()){
                PaperManager manager = PaperManager.getPaperManager();
                manager.setAlarmType(i);
                return;
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        PaperManager manager = PaperManager.getPaperManager();
        manager.setAlarmPhone(s.toString());
    }
}
