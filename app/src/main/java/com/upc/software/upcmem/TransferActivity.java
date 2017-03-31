package com.upc.software.upcmem;

import android.app.ProgressDialog;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.upc.adapter.SpinnerAdapter;
import com.upc.javabean.Pocket;
import com.upc.javabean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class TransferActivity extends AppCompatActivity {

    private Spinner before,after;
    private Button ok;
    private EditText num;
    Pocket nPocket,newPocket;
    SpinnerAdapter spinnerAdapter;
    List<HashMap<String,Pocket>> methodList;
    List<HashMap<String,Pocket>> handlerList;
    android.os.Handler handler;
    String tempIdBe,tempIdAf;
    Double tempNumBe,tempNumAf;
    String coinBe,coinAf;
    User user;
    /**************************/
    ProgressDialog progressDialog;//查询等待框
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        user = BmobUser.getCurrentUser(User.class);
        initView();
        setFilter();
        //初始化查询等待框
        progressDialog = new ProgressDialog(this);//查询时的等待框
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setTitle("正在获取钱包数据");
        /****************************************************************************/
        initWidget();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!coinBe.equals(coinAf))
                {
                    Toast.makeText(getApplicationContext(),"币种不同不能转账",Toast.LENGTH_SHORT).show();
                }else if (tempIdBe.equals(tempIdAf))
                {
                    Toast.makeText(getApplicationContext(),"同一账户不能转账",Toast.LENGTH_SHORT).show();
                }else
                {
                    if (num.getText().toString().equals("")||num.getText().toString().equals(null))
                    {
                        num.setError("转账金额不符合规则");
                        num.setFocusable(true);
                    }else
                    {
                        nPocket = new Pocket();
                        newPocket = new Pocket();
                        nPocket.setNumber(tempNumBe-Double.valueOf(num.getText().toString()));
                        newPocket.setNumber(tempNumAf+Double.valueOf(num.getText().toString()));
                        nPocket.update(tempIdBe, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e!=null)
                                {
                                    Log.e("smile","转账前账户更新失败");
                                }else
                                {
                                    Log.e("smile","转账前账户更新成功");
                                }
                            }
                        });
                        newPocket.update(tempIdAf, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e!=null)
                                {
                                    Log.e("smile","转账前账户更新失败");
                                }else
                                {
                                    Log.e("smile","转账前账户更新成功");
                                }
                            }
                        });
                        finish();
                    }
                }
            }
        });
    }

    private void initWidget() {
        /********************************获取方式*************************/
        progressDialog.show();//Display the dialog
        BmobQuery<Pocket> bmobQuery = new BmobQuery<Pocket>();
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.findObjects(new FindListener<Pocket>() {
            @Override
            public void done(List<Pocket> list, BmobException e) {
                if(e==null)
                {
                    progressDialog.dismiss();//Dismiss the dialog
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
                    spinnerAdapter = new com.upc.adapter.SpinnerAdapter(getApplicationContext(),methodList);
                    before.setAdapter(spinnerAdapter);
                    after.setAdapter(spinnerAdapter);
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
                    default:break;
                }
            }
        };
        before.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("smile","handler的record是"+handlerList.get(i).get("mt").getNumber());
                tempIdBe = handlerList.get(i).get("mt").getObjectId();
                tempNumBe = handlerList.get(i).get("mt").getNumber();
                coinBe = handlerList.get(i).get("mt").getCoinType();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        after.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("smile","handler的record是"+handlerList.get(i).get("mt").getNumber());
                tempIdAf = handlerList.get(i).get("mt").getObjectId();
                tempNumAf = handlerList.get(i).get("mt").getNumber();
                coinAf = handlerList.get(i).get("mt").getCoinType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    /*******
     * 设置字符过滤
     */
    private void setFilter() {
        num.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        num.setFilters(new InputFilter[]{new InputFilter() {
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

    private void initView() {
        before = (Spinner) findViewById(R.id.transferbefore);
        after = (Spinner) findViewById(R.id.transferafter);
        ok = (Button) findViewById(R.id.transferbutton);
        num = (EditText) findViewById(R.id.transfernum);
    }
}
