package com.upc.software.upcmem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.javabean.CircleImageView;
import com.upc.javabean.User;
import com.upc.service.DetailChangeService;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class EditActivity extends AppCompatActivity implements View.OnClickListener{

    private CircleImageView ivHead;
    private Button editok;
    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private RelativeLayout layout_close;

    private RelativeLayout layout_all;
    protected int mScreenWidth;

    /*
    获取初始值的变量初始化
     */
    private EditText nickname;
    private EditText age;
    private EditText address;
    private TextView phone;
    private EditText email;
    private EditText work;
    private EditText school;

    User user;
    User nUser;

    /**
     * 定义三种状态
     */
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪

    private Bitmap mBitmap;
    private File mFile,upFile;
    /****************************************/
    ProgressDialog progressDialog;//查询等待框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        user = BmobUser.getCurrentUser(User.class);//获取本地缓存的user数据
        /********设置拍照弹出窗口************/
        editok = (Button) findViewById(R.id.editok);
        ivHead = (CircleImageView) findViewById(R.id.editcircleImageView);
        layout_all = (RelativeLayout) findViewById(R.id.activity_edit);
        initProgressDialog();//初始化progressDialog
        initHintView();//初始化控件
        setHintView();//设置控件HINT值

        editok.setOnClickListener(this);
        ivHead.setOnClickListener(this);
    }

    private void initProgressDialog() {
        //初始化查询等待框
        progressDialog = new ProgressDialog(this);//查询时的等待框
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在修改");
    }

    private void setHintView() {

        nickname.setHint(user.getNickName());
        age.setHint(user.getAge().toString());
        address.setHint(user.getAdress());
        work.setHint(user.getWork());
        school.setHint(user.getSchool());
        email.setHint(user.getEmail());

        nickname.setText(user.getNickName());
        age.setText(user.getAge().toString());
        address.setText(user.getAdress());
        work.setText(user.getWork());
        school.setText(user.getSchool());
        phone.setText(user.getMobilePhoneNumber());
        email.setText(user.getEmail());
        /********
         * 下载图片
         */
        if(user.getImageURL()!=null)
        {
            BmobFile bmobfile =new BmobFile(user.getUsername()+".png","",user.getImageURL());
            Log.e("smile","上传的路径是+++++++"+user.getImageURL());
            bmobfile.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Log.e("smile","下载成功,保存路径:"+s);
                        Bitmap bm = BitmapFactory.decodeFile(s);
                        ivHead.setImageBitmap(bm);
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

    private void initHintView() {
        ivHead = (CircleImageView) findViewById(R.id.editcircleImageView);
        nickname = (EditText) findViewById(R.id.editnickname);
        age = (EditText)findViewById(R.id.editage);
        address = (EditText)findViewById(R.id.editaddress);
        work = (EditText) findViewById(R.id.editwork);
        school = (EditText) findViewById(R.id.editschool);
        phone = (TextView) findViewById(R.id.editphone);
        email = (EditText) findViewById(R.id.editemail);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editcircleImageView:
                showMyDialog();
                break;
            case R.id.editok:
                uploadPic();
                changeUserData();
        }
    }

    private void changeUserData() {
        nUser = new User();
        if (nickname.getText().toString().equals("")) {
            Log.e("smile","nickname确实是为空的");
            nickname.setError("不能为空");
        }else if (age.getText().toString().equals(""))
            age.setError("不能为空");
        else if(address.getText().toString().equals(""))
            address.setError("不能为空");
        else if(work.getText().toString().equals(""))
            work.setError("不能为空");
        else if(school.getText().toString().equals(""))
            school.setError("不能为空");
        else if (email.getText().toString().equals(""))
            email.setError("不能为空");
        else {
            /**************
             * 设置list类型
             */
            /*List<String > testList = new ArrayList<>();
            testList.add("测试1");
            testList.add("测试2");
            nUser.setTest(testList);*/
            progressDialog.show();
            nUser.setNickName(nickname.getText().toString());
            nUser.setAge(Integer.parseInt(age.getText().toString()));
            nUser.setAdress(address.getText().toString());
            nUser.setWork(work.getText().toString());
            nUser.setSchool(school.getText().toString());
            nUser.setEmail(email.getText().toString());
            nUser.update(user.getObjectId(),new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null)
                    {
                        progressDialog.dismiss();
                        Log.e("smile","用户信息更新成功");
                       /* Intent intent = new Intent(EditActivity.this, DetailChangeService.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user",nUser);
                        intent.putExtras(bundle);
                        startService(intent);*/
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"更新失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.e("smile","更新用户遇到的问题是"+e.getMessage());
                    }
                }
            });
        }
    }

    private void uploadPic() {
        if(upFile!=null)
        {
            String url =upFile.getAbsolutePath();
            Log.e("smile","上传的URI是+++++++++++"+url);
            final BmobFile bmobFile = new BmobFile(new File(url));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null)
                    {
                        nUser = new User();
                        nUser.setImageURL(bmobFile.getFileUrl());
                        nUser.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e!=null)
                                {
                                    Log.e("smile","更新用户图片URI是"+bmobFile.getFileUrl());
                                }else
                                {
                                    Log.e("smile","更新头像时出现了问题"+e.getMessage());
                                }
                            }
                        });
                        Log.e("smile","上传成功"+bmobFile.getFileUrl());
                    }else
                    {
                        Log.e("smile","上传失败+++++++"+e.getMessage());
                    }
                }
            });
        }else
        {
            Log.e("smile","用户没有选择更改头像信息");
        }

    }

    PopupWindow avatorPop;//初始化照片选择的popwindow


    private void showMyDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_show_dialog,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_close = (RelativeLayout) view.findViewById(R.id.layout_close);

        layout_choose.setBackgroundColor(getResources().getColor(
                R.color.base_color_text_white));
        layout_photo.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.pop_bg_press));
        layout_close.setBackgroundColor(getResources().getColor(
                R.color.base_color_text_white));


        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_close.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));


                openCamera();

                // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //startActivityForResult(intent,);
            }
        });

        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_close.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                openPic();

            }
        });

        layout_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_close.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                avatorPop.dismiss();
            }
        });



        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        avatorPop = new PopupWindow(view, mScreenWidth, 200);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });
        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 打开相册
     */
    private void openPic() {
        Intent picIntent = new Intent(Intent.ACTION_PICK,null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(picIntent,REQUESTCODE_PIC);
    }

    /**
     * 调用相机
     */
    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()){
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
            startActivityForResult(intent,REQUESTCODE_CAM);
        } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_CAM:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case REQUESTCODE_PIC:

                    if (data == null || data.getData() == null){
                        return;
                    }
                    startPhotoZoom(data.getData());

                    break;
                case REQUESTCODE_CUT:

                    if (data!= null){
                        savaPicInLocation(data);
                        setPicToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*******************************
     * 保存图片至本地
     * @param data
     */
    private void savaPicInLocation(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null)
            mBitmap = bundle.getParcelable("data");//获得所需的bitmap
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        ByteArrayOutputStream baos = null; // 字节数组输出流
        baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();// 字节数组输出流转换成字节数组
        String picName = System.currentTimeMillis() + ".jpg";
        upFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),picName);//设置文件名以及路径
        // 将字节数组写入到刚创建的图片文件中
        try {
            fos = new FileOutputStream(upFile);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);
        }catch  (Exception e) {
            e.printStackTrace();
        }finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        Log.e("smile","文件名字是++++++++"+upFile.getAbsolutePath());
    }
/******************将图片显示出来****************/
    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            mBitmap = bundle.getParcelable("data");
            //Log.e("smile","data.getdata()的值是+++++++++"+uri.toString());//这里也可以做文件上传
            ivHead.setImageBitmap(mBitmap);
            /*************保存图片路径测试**************/
            /*if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED))
            {
                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                mFile = new File(file, System.currentTimeMillis() + ".jpg");
                if (file.exists())
                    file.delete();
                try {
                    //mFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("smile", "路径是+++++++++++"+mFile.getPath().toString());
            }*/
        }
    }

    /**
     * 打开系统图片裁剪功能
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop",true);
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("scale",true); //黑边
        intent.putExtra("scaleUpIfNeeded",true); //黑边
        intent.putExtra("return-data",true);
        intent.putExtra("noFaceDetection",true);
        startActivityForResult(intent,REQUESTCODE_CUT);
    }
}

