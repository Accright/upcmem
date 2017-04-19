package com.upc.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.upc.adapter.GridViewAdapter;
import com.upc.javabean.Pocket;
import com.upc.javabean.Record;
import com.upc.javabean.User;
import com.upc.permission.PermissionListener;
import com.upc.permission.PermissionManager;
import com.upc.software.upcmem.InKindsEditActivity;
import com.upc.software.upcmem.R;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;

public class InFragment extends Fragment {

    private static final int LIST_CHANGE = 10;
    GridView inGridView;
    User user;
    Record nRecord = new Record();
    Pocket nPocket = new Pocket();
    private String tempId;
    private Double tempNum;
    //String[] outKindsList;
    List<String> list;
    /*****************初始化pocket spinner******************/
    List<HashMap<String,Pocket>> methodList;
    List<HashMap<String,Pocket>> handlerList;
    private Spinner methodSpinner;
    private SpinnerAdapter spinnerAdapter;
    android.os.Handler handler ;
    /******************************************************/
    private TextView inKindsdispaly;
    private EditText number;
    private EditText remark;
    private TextView location;
    private Button inok;
    private ImageButton photo;
    private ImageView img;
    /***************************
     * 上传图片的初始化
     ********************/
    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private RelativeLayout layout_close;

    private RelativeLayout layout_all;
    protected int mScreenWidth;
    private Bitmap mBitmap;
    private File mFile, upFile;
    PopupWindow avatorPop;//初始化照片选择的popwindow
    /**
     * 定义三种状态
     */
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪
    /***********************
     * Baidu地图初始化
     ********************/
    public LocationClient mLocationClient;
    /****************************************/
    ProgressDialog progressDialog;//查询等待框
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = BmobUser.getCurrentUser(User.class);
        View viewRoot = inflater.inflate(R.layout.inpage,container,false);
        /*****************初始化百度地图********************/
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(bdLocation.getTime());
                sb.append("\nerror code : ");
                sb.append(bdLocation.getLocType());
                sb.append("\nlatitude : ");
                sb.append(bdLocation.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(bdLocation.getLongitude());
                sb.append("\nradius : ");
                sb.append(bdLocation.getRadius());
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(bdLocation.getSpeed());// 单位：公里每小时
                    sb.append("\nsatellite : ");
                    sb.append(bdLocation.getSatelliteNumber());
                    sb.append("\nheight : ");
                    sb.append(bdLocation.getAltitude());// 单位：米
                    sb.append("\ndirection : ");
                    sb.append(bdLocation.getDirection());// 单位度
                    sb.append("\naddr : ");
                    sb.append(bdLocation.getAddrStr());
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(bdLocation.getAddrStr());
                    //运营商信息
                    sb.append("\noperationers : ");
                    sb.append(bdLocation.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    nRecord.setLocationDetail("未知地点");
                    nRecord.setLocation("未知");
                    location.setText("无法获取地理位置，请打开相应权限");
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                sb.append("\nlocationdescribe : ");
                sb.append(bdLocation.getLocationDescribe());// 位置语义化信息
                List<Poi> list = bdLocation.getPoiList();// POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }
                if (bdLocation.getCity()!=null&&bdLocation.getAddrStr()!=null)
                {
                    nRecord.setLocation(bdLocation.getCity());
                    nRecord.setLocationDetail(bdLocation.getAddrStr());
                    location.setText(bdLocation.getAddrStr());
                    Log.e("smile","获取的地点打印+++"+bdLocation.getCity()+bdLocation.getAddrStr());
                    Log.e("baidu", "定位的结果++++++++" + sb.toString());
                }
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setScanSpan(10000);//定位请求时间间隔
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        /******
         * 初始化各个控件
         */
        inGridView = (GridView) viewRoot.findViewById(R.id.inkinds);
        number = (EditText) viewRoot.findViewById(R.id.innumber);
        remark = (EditText) viewRoot.findViewById(R.id.inremark);
        location = (TextView) viewRoot.findViewById(R.id.inlocation);
        inok = (Button) viewRoot.findViewById(R.id.inbutton);
        img = (ImageView) viewRoot.findViewById(R.id.inimg);
        photo = (ImageButton) viewRoot.findViewById(R.id.inphoto);
        methodSpinner = (Spinner) viewRoot.findViewById(R.id.inmethod);
        layout_all = (RelativeLayout) viewRoot.findViewById(R.id.inlayout);//显示popwindow的初始化
        inKindsdispaly = (TextView) viewRoot.findViewById(R.id.inkindsdisplay);//初始化类别显示TextView
        //初始化查询等待框
        progressDialog = new ProgressDialog(getActivity());//查询时的等待框
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在添加");
        /*************************************/
        /********************************获取方式*************************/
        BmobQuery<Pocket> bmobQuery = new BmobQuery<Pocket>();
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.findObjects(new FindListener<Pocket>() {
            @Override
            public void done(List<Pocket> list, BmobException e) {
                if(e==null)
                {
                    methodList = new ArrayList<HashMap<String, Pocket>>();
                    //nameMethodList = new ArrayList<String>();
                    //numMethodList = new ArrayList<Integer>();
                    //Log.e("smile","list是+++++++++++++"+list.get(0).getKind().toString());
                    for (Pocket p : list)
                    {
                        HashMap<String,Pocket> hashMap= new HashMap<String,Pocket>();
                        hashMap.put("mt",p);
                        methodList.add(hashMap);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = methodList;
                    handler.sendMessage(msg);
                    spinnerAdapter = new com.upc.adapter.SpinnerAdapter(getContext(),methodList);
                    methodSpinner.setAdapter(spinnerAdapter);
                }else
                {
                    //Log.e("smile","查询的list是++++++++"+list.get(0));
                    Log.e("smile","查询spinner错误"+e.getMessage()+e.getErrorCode());
                }
            }
        });
        setFilter();
        getInKinds();
        inGridView.setAdapter(new GridViewAdapter(getActivity(),list));
        inGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == adapterView.getCount()-1) {
                    Intent intent = new Intent(getActivity(),InKindsEditActivity.class);
                    startActivityForResult(intent,LIST_CHANGE);
                    Log.e("smile", "点击上了添加键+++++++进行跳转");
                }else
                {
                    inKindsdispaly.setText(list.get(i));
                }
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyDialog();
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
                    default:break;
                }
            }
        };
        methodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nRecord.setCoin(handlerList.get(i).get("mt").getCoinType());
                Log.e("smile","handler的record是"+handlerList.get(i).get("mt").getNumber());
                nRecord.setMethod(handlerList.get(i).get("mt").getKind());
                tempId = handlerList.get(i).get("mt").getObjectId();
                tempNum = handlerList.get(i).get("mt").getNumber();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(list.size()!=0)
        {
            inKindsdispaly.setText(list.get(0));
        }
        inok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("smile","点击确定键");
                nRecord.setType("收入");
                nRecord.setUserId(user.getObjectId());
                //nRecord.setMethod("无");
                //nRecord.setCoin("RMB");
                nRecord.setDeleted(false);
                if (remark.getText().toString().isEmpty()) {
                    nRecord.setRemark("无");
                } else {
                    nRecord.setRemark(remark.getText().toString());
                }
                nRecord.setKind(inKindsdispaly.getText().toString());
                if (number.getText().toString().isEmpty()) {
                    number.setError("数值不能为空！");
                } else {
                    progressDialog.show();
                    final Double temp = Double.valueOf(number.getText().toString());
                    nRecord.setNumber(temp);
                    nRecord.setPocketId(tempId);
                    /******************统计查询进行月份记录******************/
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH)+1;
                    nRecord.setMonth(month);
                    /****************pocket数值更改*****************/
                    nPocket.setNumber(tempNum + temp);
                    uploadPic();
                    nRecord.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e != null)
                                Log.e("smile", "新建记录保存错误信息是++++++++++" + e.getMessage());
                            //else
                                //getActivity().finish();
                        }
                    });
                    nPocket.update(tempId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null)
                            {
                                progressDialog.dismiss();
                                getActivity().finish();
                                Log.e("smile","Pocket更新成功"+(tempNum + temp));
                            }else {
                                Log.e("smile","Pocket更新失败"+e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        return viewRoot;
    }

    private void getInKinds() {
        list = new ArrayList<>();
        list = user.getInKinds();
        Log.e("smile","初始化list 是++++++++++++"+list.toString());
    }
    /*******
     * 设置字符过滤
     */
    private void setFilter() {
        number.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        number.setFilters(new InputFilter[]{new InputFilter() {
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
    private void showMyDialog() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_show_dialog,
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
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        //Log.e("smile", "mScreenWidth 是+++++++++++++++" + mScreenWidth);
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
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, REQUESTCODE_PIC);
    }

    /**
     * 调用相机
     */
    private void openCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUESTCODE_CAM);
        } else {
            Toast.makeText(getActivity(), "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_CAM:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case REQUESTCODE_PIC:

                    if (data == null || data.getData() == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());

                    break;
                case REQUESTCODE_CUT:

                    if (data != null) {
                        savaPicInLocation(data);
                        setPicToView(data);
                    }
                    break;
                case LIST_CHANGE:
                    list = (List<String>) data.getSerializableExtra("inke");
                   // user.setInKinds(list);
                   // Log.e("smile","user.getinkinds++++++++"+user.getInKinds().toString());
                   // Log.e("smile",list.toString());
                    inGridView.setAdapter(new GridViewAdapter(getContext(),list));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 打开系统图片裁剪功能
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUT);

    }

    /*******************************
     * 保存图片至本地
     *
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
        upFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), picName);//设置文件名以及路径
        // 将字节数组写入到刚创建的图片文件中
        try {
            fos = new FileOutputStream(upFile);
            bos = new BufferedOutputStream(fos);
            bos.write(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        Log.e("smile", "文件名字是++++++++" + upFile.getAbsolutePath());
    }

    private void setPicToView(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            mBitmap = bundle.getParcelable("data");
            //Log.e("smile","data.getdata()的值是+++++++++"+uri.toString());//这里也可以做文件上传
            img.setVisibility(View.VISIBLE);
            img.setImageBitmap(mBitmap);
        }
    }

    /***************************
     * 上传备注照片
     ************************/
    private void uploadPic() {
        if (upFile != null) {
            String url = upFile.getAbsolutePath();
            Log.e("smile", "上传的URI是+++++++++++" + url);
            final BmobFile bmobFile = new BmobFile(new File(url));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        nRecord.setImageUrl(bmobFile.getFileUrl());
                        nRecord.update(nRecord.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e!=null)
                                {
                                    Log.e("smile","更新记录图片的错误信息是+++++++++"+e.getMessage());
                                }else
                                {
                                    Log.e("smile","更新记录备注图片成功");
                                }
                            }
                        });
                    } else {
                        Log.e("smile", "用户没有选择更改备注照片信息");
                    }
                }
            });
        }
    }
/*****************************************/
}
