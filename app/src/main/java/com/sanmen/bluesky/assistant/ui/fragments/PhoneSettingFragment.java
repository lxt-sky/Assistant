package com.sanmen.bluesky.assistant.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sanmen.bluesky.assistant.R;

import java.util.Objects;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class PhoneSettingFragment extends Fragment implements View.OnTouchListener {

    View rootView;

    EditText etPhoneValue;
    ConstraintLayout constrainLayout;

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
        constrainLayout = rootView.findViewById(R.id.constrainLayout);

        constrainLayout.setOnTouchListener(this);
        etPhoneValue.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId()!=R.id.etPhoneValue){

            constrainLayout.setFocusableInTouchMode(true);
            constrainLayout.requestFocus();
            InputMethodManager manager;
            manager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(etPhoneValue.getWindowToken(),0);
            }
        }

        return false;
    }
}
