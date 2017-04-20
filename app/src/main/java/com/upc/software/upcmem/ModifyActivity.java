package com.upc.software.upcmem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.citypick.CityPicker;
import com.upc.javabean.Pocket;
import com.upc.javabean.Record;
import com.upc.javabean.User;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.LoggingPermission;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ModifyActivity extends AppCompatActivity {

    private EditText modifyNum;
    private Spinner modifyMethod;
    private TextView modifyAddress;
    private EditText modifyRemark;
   // private EditText modifyCoin;
    private Spinner modifyKind;
    private Spinner modifyType;
    private ImageView modifyImg;
    private Button modifyOk;
    private ImageButton imgChange;

    private User user;
    Record nRecord = new Record();
    Pocket nPocket = new Pocket();
    Pocket newPocket = new Pocket();
    Double numAgo,numNew;
    private String tempId;
    private Double tempNum;
    Record item;
    private int index;
    List<String> outkinds,inkinds;
    List<HashMap<String,Pocket>> methodList;
    List<HashMap<String,Pocket>> handlerList;
    List<String> nameList;
    List<String> methodNameList;
 //   private Spinner modifyMethod;
    private SpinnerAdapter spinnerAdapter;
    android.os.Handler handler ;
    /*************图片上传组件初始化*********************/
    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private RelativeLayout layout_close;
    private RelativeLayout layout_all;
    protected int mScreenWidth;
    private Bitmap mBitmap;
    private File mFile,upFile;
    /**
     * 定义三种状态
     */
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪
    /****************************************/
    ProgressDialog progressDialog,imgdialog;//查询等待框,图片下载等待
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        user = BmobUser.getCurrentUser(User.class);
        Intent intent = getIntent();
        item = (Record) intent.getSerializableExtra("item");
        initProgressDialog();
        initView();
        initWidget();
        initData();
        modifyOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPic();//上传图片
                changeDatas();//更改数据
                finish();
            }
        });
    }

    private void initProgressDialog() {
        //初始化查询等待框
        progressDialog = new ProgressDialog(this);//查询时的等待框
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在修改");
        imgdialog = new ProgressDialog(this);
        imgdialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        imgdialog.setCancelable(true);
        imgdialog.setCanceledOnTouchOutside(false);
        imgdialog.setTitle("正在下载图片");
    }
    private void changeDatas() {
        if (modifyNum.getText().toString().equals(""))
        {
            modifyNum.setError("金额不能为空");
            modifyNum.setFocusable(true);
        }
        double numChange = Double.valueOf(modifyNum.getText().toString());//获取输入的值
        BmobQuery<Pocket> bmobQuery = new BmobQuery<Pocket>();//查询该记录以前所属的钱包
        bmobQuery.getObject(item.getPocketId(), new QueryListener<Pocket>() {
            @Override
            public void done(Pocket pocket, BmobException e) {
                if (e!=null)
                {
                    Log.e("smile","根据record查询Pocket失败"+e.getMessage());
                }else
                {
                    Log.e("smile","根据record查询Pocket成功！"+pocket.getKind());
                    numAgo = pocket.getNumber();//获取该记录以前所属钱包的金额
                    Log.e("smile","numago的值是+++++++absbdjhasbjhsa"+numAgo);
                    if (item.getType().equals("支出"))
                    {
                        Log.e("smile","原来的pocket进行了加法"+item.getType());
                        nPocket.setNumber(numAgo+item.getNumber());//该记录以前所属钱包的金额增加
                    }else
                    {
                        Log.e("smile","原来的pocket进行了减法"+item.getType());
                        nPocket.setNumber(numAgo-item.getNumber());//该记录以前所属钱包的金额减少
                    }
                    nPocket.update(item.getPocketId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null)
                            {
                                Log.e("smile","在更改record详情界面更改Pocketed数值失败了"+e.getMessage());
                            }else
                            {
                                Log.e("smile","在更改record详情界面更改Pocketed数值成功了"+item.getPocketId());
                            }
                        }
                    });
                }
            }
        });
        nRecord.setNumber(numChange);//新纪录的金额更改
        Log.e("smile","tempId是+++++++++++++"+tempId);
        nRecord.setPocketId(tempId);
        //Log.e("smile","numago的值是+++++++"+numAgo);
        if (nRecord.getType()=="支出")
        {
            Log.e("smile","测试nRecord的Type是什么+++++++++支出"+nRecord.getType());
            newPocket.setNumber(tempNum-numChange);//该记录新更改的钱包的金额减少
        }else
        {
            Log.e("smile","测试nRecord的Type是什么+++++++++else+"+nRecord.getType());
            newPocket.setNumber(tempNum+numChange);//该记录新更改的钱包的金额增加
        }
        newPocket.update(tempId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null)
                {
                    Log.e("smile","在更改record详情界面更改新的Pocketed数值失败了"+e.getMessage());
                }else
                {
                    Log.e("smile","在更改record详情界面更改新的Pocketed数值成功了");
                }
            }
        });
        nRecord.update(item.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e != null)
                {
                    Log.e("smile","在更改record详情界面更改新的Record数值失败了"+e.getMessage());
                }else
                {
                    Log.e("smile","在更改record详情界面更改新的Record数值成功了");
                }
            }
        });
    }

    private void initWidget() {
        /**********初始化金额*****************/
        setFilter();//设置小数点两位
        /**********************初始化账户和币种******************/
        /********************************获取方式*************************/
        BmobQuery<Pocket> bmobQuery = new BmobQuery<Pocket>();
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.findObjects(new FindListener<Pocket>() {
            @Override
            public void done(List<Pocket> list, BmobException e) {
                if(e==null)
                {
                    nameList = new ArrayList<String>();
                    methodList = new ArrayList<HashMap<String, Pocket>>();
                    //nameMethodList = new ArrayList<String>();
                    //numMethodList = new ArrayList<Integer>();
                    //Log.e("smile","list是+++++++++++++"+list.get(0).getKind().toString());
                    for (Pocket p : list)
                    {
                        nameList.add(p.getKind());
                        HashMap<String,Pocket> hashMap= new HashMap<String,Pocket>();
                        hashMap.put("mt",p);
                        methodList.add(hashMap);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = methodList;
                    Message msgMethodName = new Message();
                    msgMethodName.what = 2;
                    msgMethodName.obj = nameList;
                    Log.e("smile","nameLIst is +++++++++++"+nameList.toString());
                    handler.sendMessage(msgMethodName);
                    handler.sendMessage(msg);
                    spinnerAdapter = new com.upc.adapter.SpinnerAdapter(getApplicationContext(),methodList);
                    modifyMethod.setAdapter(spinnerAdapter);
                    modifyMethod.setSelection(index);
                }else
                {
                    //Log.e("smile","查询的list是++++++++"+list.get(0));
                    Log.e("smile","查询spinner错误"+e.getMessage()+e.getErrorCode());
                }
            }
        });
        handler = new android.os.Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1: handlerList = (List<HashMap<String,Pocket>>) msg.obj;
                        Log.e("smile","handler 成功");
                        break;
                    case 2: methodNameList = (List<String>) msg.obj;
                        index = methodNameList.indexOf(item.getMethod());
                        Log.e("smile","index是++++++++++"+index);
                        Log.e("smile","获取methodName  成功");
                    default:break;
                }
            }
        };
        modifyMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nRecord.setCoin(handlerList.get(i).get("mt").getCoinType());
                Log.e("smile","handler的record是"+handlerList.get(i).get("mt").getNumber());
                nRecord.setMethod(handlerList.get(i).get("mt").getKind());
                tempId = handlerList.get(i).get("mt").getObjectId();//新钱包数据的id
                tempNum = handlerList.get(i).get("mt").getNumber();//新钱包数据的金额
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /***************init kind *************************/
        outkinds = user.getOutKinds();
        inkinds = user.getInKinds();
        final ArrayAdapter<String> inkindsAdpter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,inkinds);
        final ArrayAdapter<String> outkindsAdpter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,outkinds);
        /*if (item.getType().equals("收入"))
        {
            modifyKind.setAdapter(inkindsAdpter);
            modifyKind.setSelection(inkindsAdpter.getPosition(item.getKind()));
            modifyKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    nRecord.setKind(inkinds.get(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    nRecord.setKind(item.getKind());

                }
            });
        }else {
            modifyKind.setAdapter(outkindsAdpter);
            modifyKind.setSelection(outkindsAdpter.getPosition(item.getKind()));
            modifyKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    nRecord.setKind(outkinds.get(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    nRecord.setKind(item.getKind());

                }
            });
        }*/
        /*************************************************/
        /*************Init type***************************/
        final String[] typeList = {"收入","支出"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,typeList);
        modifyType.setAdapter(typeAdapter);
        modifyType.setSelection(typeAdapter.getPosition(item.getType()));
        modifyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nRecord.setType(typeList[i]);
                if (nRecord.getType() == "收入")
                {
                    modifyKind.setAdapter(inkindsAdpter);
                    modifyKind.setSelection(inkindsAdpter.getPosition(item.getKind()));
                    modifyKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            nRecord.setKind(inkinds.get(i));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            nRecord.setKind(item.getKind());

                        }
                    });
                }else
                {
                    modifyKind.setAdapter(outkindsAdpter);
                    modifyKind.setSelection(outkindsAdpter.getPosition(item.getKind()));
                    modifyKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            nRecord.setKind(outkinds.get(i));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            nRecord.setKind(item.getKind());

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                nRecord.setType(item.getType());
            }
        });
        /****************************************************/
        /*******************************init address********************************/
        /****************************************************************************/
        /***************下载图片****************/
        if(item.getImageUrl()!=null)
        {
            imgdialog.show();
            BmobFile bmobfile =new BmobFile(item.getObjectId()+".png","",item.getImageUrl());
            bmobfile.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        imgdialog.dismiss();
                        Log.e("smile","下载成功,保存路径:"+s);
                        Bitmap bm = BitmapFactory.decodeFile(s);
                        modifyImg.setVisibility(View.VISIBLE);
                        modifyImg.setImageBitmap(bm);
                    }else{
                        imgdialog.dismiss();
                        Toast.makeText(getApplicationContext(),"图片下载失败",Toast.LENGTH_SHORT);
                        Log.e("smile","下载失败："+e.getErrorCode()+","+e.getMessage());
                    }
                }
                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }
        /*********************************更改图片***********************************/
        imgChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyDialog();
            }
        });


    }

    private void setFilter() {
        modifyNum.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        modifyNum.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});
    }

    private void initData() {
        modifyNum.setText(item.getNumber().toString());
        modifyRemark.setText(item.getRemark());
        modifyAddress.setText(item.getLocationDetail());
    }

    private void initView() {
        modifyNum = (EditText) findViewById(R.id.modifynum);
        modifyMethod = (Spinner) findViewById(R.id.modifymethod);
       modifyAddress = (TextView) findViewById(R.id.modifyaddress);
        modifyRemark = (EditText) findViewById(R.id.modifyremark);
        //modifyCoin = (EditText) findViewById(R.id.modifycoin);
        modifyImg = (ImageView) findViewById(R.id.modifyimg);
        modifyOk = (Button) findViewById(R.id.modifyok);
        modifyKind = (Spinner) findViewById(R.id.modifykind);
        modifyType = (Spinner) findViewById(R.id.modifytype);
        layout_all = (RelativeLayout) findViewById(R.id.activity_modify);
        imgChange = (ImageButton) findViewById(R.id.imgchange);
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
                        nRecord.setImageUrl(bmobFile.getFileUrl());
                        nRecord.update(item.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e!=null)
                                {
                                    Log.e("smile","更新详情图片URI是"+bmobFile.getFileUrl());
                                }else
                                {
                                    Log.e("smile","更新详情图片时出现了问题"+e.getMessage());
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
            Log.e("smile","用户没有选择更改详情图片信息");
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
    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            mBitmap = bundle.getParcelable("data");
            //Log.e("smile","data.getdata()的值是+++++++++"+uri.toString());//这里也可以做文件上传
            modifyImg.setVisibility(View.VISIBLE);
            modifyImg.setImageBitmap(mBitmap);
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
