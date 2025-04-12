package com.xuexiang.template.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.xuexiang.template.core.BaseActivity;
import com.xuexiang.template.fragment.other.LoginFragment;
import com.xuexiang.template.fragment.other.LoginPwdFragment;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xutil.display.Colors;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openPage(LoginFragment.class, getIntent().getExtras());

    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    protected void initStatusBarStyle() {
        StatusBarUtils.initStatusBarStyle(this, false, Colors.WHITE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }

}
