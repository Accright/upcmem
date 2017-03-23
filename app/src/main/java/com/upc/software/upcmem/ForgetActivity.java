package com.upc.software.upcmem;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetActivity extends AppCompatActivity {

    EditText fpphone;
    EditText fpsmscode;
    Button fpsmsButton;
    Button findps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initView();
        fpsmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!fpphone.getText().toString().isEmpty())
                {
                    SmsVerify smsVerify = new SmsVerify(60000,1000);
                    smsVerify.start();
                    BmobSMS.requestSMSCode(fpphone.getText().toString(),"UPCM",null);
                }else {
                    fpphone.setError("手机号不能为空");
                }
            }
        });
        findps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BmobUser.resetPasswordBySMSCode(fpsmscode.getText().toString(), fpphone.getText().toString(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e!=null)
                        {
                            Log.i("smile",e.toString());
                        }else
                        {
                            Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });
    }
        private void initView()
    {
        fpphone = (EditText) findViewById(R.id.fpphone);
        fpsmscode = (EditText) findViewById(R.id.fpsmscode);
        fpsmsButton = (Button) findViewById(R.id.fpsmsButton);
        findps= (Button) findViewById(R.id.findpw);
    }
    private class SmsVerify extends CountDownTimer
    {

        public SmsVerify(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            fpsmsButton.setClickable(false);
            fpsmsButton.setText(l/1000+"s后重新发送");
        }

        @Override
        public void onFinish() {
            fpsmsButton.setText("发送验证码");
            fpsmsButton.setClickable(true);
        }
    }
}
