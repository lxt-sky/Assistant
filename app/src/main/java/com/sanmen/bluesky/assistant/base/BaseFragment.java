package com.sanmen.bluesky.assistant.base;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class BaseFragment extends Fragment {
    private KProgressHUD progressHUD = null;

    protected void showProgressDialog(@StringRes int res) {
        showProgressDialog(getString(res));
    }

    protected void showProgressDialog(String label) {
        if (progressHUD == null || getActivity() == null) {
            progressHUD = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(label)
                    .setCancellable(true)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        } else {
            progressHUD.setLabel(label);
            progressHUD.show();
        }
    }

    protected void dismissProgressDialog() {
        if (progressHUD != null) {
            progressHUD.dismiss();
        }
    }
}
