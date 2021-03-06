package com.upc.software.upcmem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.javabean.CircleImageView;
import com.upc.javabean.Record;
import com.upc.receiver.StateChangeReceiver;
import com.upc.service.DetailChangeService;

import org.w3c.dom.Text;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class DetailActivity extends AppCompatActivity {

    private TextView kind;
    private TextView locationdetail;
    private TextView method;
    private TextView num;
    private TextView remark;
    //private TextView type;
    private TextView updateAt;
    private ImageView detailImg;
    private CircleImageView typeImg;
    private TextView cion;
    Record item;
    /**************************/
    ProgressDialog progressDialog;//查询等待框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.recordtoolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.recordedit);
        setSupportActionBar(toolbar);
        initView();
        initProgressDislog();
        Intent intent = getIntent();
        item = (Record) intent.getSerializableExtra("item");//传递点击数据
        displayDetail();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,ModifyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item",item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });//设置编辑选项
    }

    private void initProgressDislog() {
        //初始化查询等待框
        progressDialog = new ProgressDialog(this);//查询时的等待框
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在下载图片");
    }

    private void displayDetail() {
        kind.setText(item.getKind());
        locationdetail.setText(item.getLocationDetail());
        method.setText(item.getMethod());
        num.setText(item.getNumber().toString());
        remark.setText(item.getRemark());
        updateAt.setText(item.getUpdatedAt());
        if (item.getType().equals("收入"))
        {
            typeImg.setImageResource(R.drawable.in);
        }else {
            typeImg.setImageResource(R.drawable.out);
        }
        cion.setText(item.getCoin());
        setDetailImg();
    }

    private void setDetailImg() {
        if(item.getImageUrl()!=null)
        {
            progressDialog.show();
            BmobFile bmobfile =new BmobFile(item.getUserId()+".png","",item.getImageUrl());
            bmobfile.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Log.e("smile","下载成功,保存路径:"+s);
                        progressDialog.dismiss();
                        Bitmap bm = BitmapFactory.decodeFile(s);
                        detailImg.setVisibility(View.VISIBLE);
                        detailImg.setImageBitmap(bm);
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"图片下载失败",Toast.LENGTH_SHORT).show();
                        Log.e("smile","下载失败："+e.getErrorCode()+","+e.getMessage());
                    }
                }
                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }else
        {
            Log.e("smile","没有图片信息");
        }
    }

    /*****
     * 初始化控件
     */
    private void initView() {
        kind = (TextView) findViewById(R.id.detailkind);
        locationdetail = (TextView) findViewById(R.id.detaillocationdetail);
        method = (TextView) findViewById(R.id.detailmethod);
        num = (TextView) findViewById(R.id.detailnum);
        remark = (TextView) findViewById(R.id.detailremark);
        typeImg = (CircleImageView) findViewById(R.id.detailtypeimg);
        updateAt = (TextView) findViewById(R.id.detailupdateat);
        detailImg = (ImageView) findViewById(R.id.detailimg);
        cion = (TextView)findViewById(R.id.detailcoin);
    }
}
