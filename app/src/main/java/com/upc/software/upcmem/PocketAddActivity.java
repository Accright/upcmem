package com.upc.software.upcmem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.upc.javabean.Pocket;
import com.upc.javabean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class PocketAddActivity extends AppCompatActivity {

    private EditText pocketEditName,pocketEditNum;
    private Button ok;
    private Spinner spinner;
    Pocket pocket;
    User user;
    List<String> list;
    String tempCoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket_add);
        user = BmobUser.getCurrentUser(User.class);
        initView();
        setFilter();
        final String[] list = {"人民币","欧元","美元"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempCoin = list[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tempCoin = list[0];
            }
        });
        /*******************/
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pocketEditName.getText().toString().isEmpty())
                {
                    pocketEditName.setError("不能为空");
                    pocketEditName.setFocusable(true);
                }else if (pocketEditNum.getText().toString().isEmpty())
                {
                    pocketEditNum.setError("不能为空");
                    pocketEditNum.setFocusable(true);
                }else
                {
                    Pocket nPocket = new Pocket();
                    nPocket.setNumber(Double.valueOf(pocketEditNum.getText().toString()));
                    nPocket.setCoinType(tempCoin);
                    nPocket.setKind(pocketEditName.getText().toString());
                    nPocket.setUserId(user.getObjectId());
                    nPocket.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e!=null)
                            {
                                Log.e("smile","保存Pocket++++++++++++++++"+e.getMessage());
                            }
                        }
                    });
                    Intent intent = new Intent();
                    intent.putExtra("nPocket",(Serializable)nPocket);
                    //intent.putExtra("position",position);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    private void initView() {
        pocketEditName = (EditText) findViewById(R.id.pocketaddname);
        pocketEditNum = (EditText) findViewById(R.id.pocketaddnum);
       // pocketEditCoin = (EditText) findViewById(R.id.pocketaddcoin);
        spinner = (Spinner) findViewById(R.id.pocketaddspinner);
        ok = (Button) findViewById(R.id.pocketaddok);
    }
    /*******
     * 设置字符过滤
     */
    private void setFilter() {
        pocketEditNum.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        pocketEditNum.setFilters(new InputFilter[]{new InputFilter() {
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
}
