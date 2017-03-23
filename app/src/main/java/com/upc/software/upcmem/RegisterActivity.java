package com.upc.software.upcmem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.upc.javabean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private EditText vpassword;
    private Button signin;
    private EditText smsCodet;
    private Button smsButton;
    private View mProgressView;
    private View mRegisterFormView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();//初始化View
        //发送验证码监听
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!username.getText().toString().isEmpty())
                {
                    SmsVerify smsVerify = new SmsVerify(60000,1000);
                    smsVerify.start();
                    BmobSMS.requestSMSCode(username.getText().toString(), "UPCM", new QueryListener<Integer>() {
                        @Override
                        public void done(Integer integer, BmobException e) {
                            if(e!=null)
                            {
                                Log.e("smile",e.toString());
                            }
                        }
                    });
                }else {
                    username.setError("手机号不能为空");
                }
            }
        });
        //注册按钮监听
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!password.getText().toString().contentEquals(vpassword.getText().toString()))
                {
                    View focusView = null;
                    vpassword.setError("两次密码不一致");
                    focusView = vpassword;
                }else{
                    User user = new User();
                    List<String> outKinds,inKinds;
                    outKinds = new ArrayList<String>();
                    inKinds = new ArrayList<String>();
                    outKinds.add("早餐");
                    outKinds.add("午餐");
                    outKinds.add("晚餐");
                    outKinds.add("打车");
                    outKinds.add("网购");
                    outKinds.add("衣服");
                    outKinds.add("鞋子");
                    outKinds.add("其他");
                    inKinds.add("工资");
                    inKinds.add("奖金");
                    inKinds.add("红包");
                    String smsCode = smsCodet.getText().toString();
                    user.setMobilePhoneNumber(username.getText().toString());
                    user.setUsername(username.getText().toString());
                    //user.setMobilePhoneNumber(phone.getText().toString());
                    user.setPassword(password.getText().toString());
                    user.setAge(0);
                    user.setSchool("无");
                    user.setWork("无");
                    user.setAdress("无");
                    user.setNickName("无");
                    user.setOutKinds(outKinds);
                    user.setInKinds(inKinds);
                    //user.setEmail("无");
                    user.signOrLogin(smsCode,new SaveListener<User>() {

                        @Override
                        public void done(User user, BmobException e) {
                            if(e==null)
                            {
                                Log.e("smile","注册成功");
                            }else
                            {
                                Log.e("smile","注册遇到的问题是+++++++++++"+e.getMessage());
                            }
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }
    //初始化View函数
    private void initView()
    {
        username = (EditText) findViewById(R.id.remail);
        password = (EditText) findViewById(R.id.rpassword);
        vpassword = (EditText) findViewById(R.id.rvpassword);
        signin = (Button) findViewById(R.id.signin);
        smsCodet = (EditText) findViewById(R.id.smscode);
        smsButton = (Button) findViewById(R.id.smsButton);
        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }
    private class SmsVerify extends CountDownTimer
    {

        public SmsVerify(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            smsButton.setClickable(false);
            smsButton.setText(l/1000+"s后重新发送");
        }

        @Override
        public void onFinish() {
            smsButton.setText("发送验证码");
            smsButton.setClickable(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
