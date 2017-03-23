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
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.upc.adapter.SpinnerAdapter;
import com.upc.javabean.Pocket;
import com.upc.javabean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PocketEditActivity extends AppCompatActivity {

    private EditText pocketEditName,pocketEditNum;
    private Button ok;
    Pocket pocket;
    User user;
    String tempCoin;
    List<String> list;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket_edit);
        user = BmobUser.getCurrentUser(User.class);
        initView();
        setFilter();
        list = new ArrayList<>();
        list.add("人民币");
        list.add("欧元");
        list.add("美元");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        spinner.setAdapter(arrayAdapter);
        pocket = (Pocket) getIntent().getSerializableExtra("item");
        Log.e("smile","更新pocket+++++++++++"+pocket.getObjectId());
        final int position = getIntent().getIntExtra("position",0);
        if (pocket!=null)
        {
            pocketEditName.setText(pocket.getKind());
            pocketEditNum.setText(pocket.getNumber().toString());
            spinner.setSelection(arrayAdapter.getPosition(pocket.getCoinType()));
            //pocketEditCoin.setText(pocket.getCoinType());
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempCoin = list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                tempCoin = pocket.getCoinType();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pocket nPocket = new Pocket();
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
                    nPocket.setNumber(Double.valueOf(pocketEditNum.getText().toString()));
                    nPocket.setCoinType(tempCoin);
                    nPocket.setKind(pocketEditName.getText().toString());
                    //nPocket.setUserId(user.getObjectId());
                    nPocket.update(pocket.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e!=null)
                            {
                                Log.e("smile","更新Pocket++++++++++++++++"+e.getMessage());
                            }
                        }
                    });
                    Intent intent = new Intent();
                    intent.putExtra("nPocket",(Serializable)nPocket);
                    intent.putExtra("position",position);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    private void initView() {
        pocketEditName = (EditText) findViewById(R.id.pocketeditname);
        pocketEditNum = (EditText) findViewById(R.id.pocketeditnum);
        //pocketEditCoin = (EditText) findViewById(R.id.pocketeditcoin);
        spinner = (Spinner) findViewById(R.id.pocketeditspinner);
        ok = (Button) findViewById(R.id.pocketeditok);
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
