package com.upc.software.upcmem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upc.javabean.CircleImageView;
import com.upc.javabean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class PersonalActivity extends AppCompatActivity {

    private CircleImageView personalimg;
    private TextView nickname;
    private TextView age;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView work;
    private TextView school;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.personaledit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });//设置编辑选项

        /*************显示详细信息*****************/
        User user = BmobUser.getCurrentUser(User.class);
        nickname.setText(user.getNickName());
        age.setText(user.getAge().toString()+"岁");
        address.setText(user.getAdress());
        phone.setText(user.getMobilePhoneNumber());
        email.setText(user.getEmail());
        school.setText(user.getSchool());
        work.setText(user.getWork());
        /***************下载图片****************/
        if(user.getImageURL()!=null)
        {
            BmobFile bmobfile =new BmobFile(user.getUsername()+".png","",user.getImageURL());
            bmobfile.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Log.e("smile","下载成功,保存路径:"+s);
                        Bitmap bm = BitmapFactory.decodeFile(s);
                        personalimg.setImageBitmap(bm);
                    }else{
                        Log.e("smile","下载失败："+e.getErrorCode()+","+e.getMessage());
                    }
                }
                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }
    }

    private void initView() {
        personalimg = (CircleImageView) findViewById(R.id.personalimg);
        nickname = (TextView) findViewById(R.id.pernalnickname);
        age = (TextView)findViewById(R.id.personalage);
        address = (TextView)findViewById(R.id.personaladress);
        phone = (TextView) findViewById(R.id.personalphone);
        email = (TextView) findViewById(R.id.personalemail);
        school = (TextView) findViewById(R.id.personalschool);
        work = (TextView) findViewById(R.id.personalwork);
    }

}
