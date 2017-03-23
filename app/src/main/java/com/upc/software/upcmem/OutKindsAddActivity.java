package com.upc.software.upcmem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class OutKindsAddActivity extends AppCompatActivity {

    private Button ok;
    private EditText kindsAdd;
    String name;
    int tempPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_out_kinds_add);
        /****************************************初始化控件*************************************/
        ok = (Button) findViewById(R.id.kindsaddok);
        kindsAdd = (EditText) findViewById(R.id.kindsadd);
        /**************************************************************************************/
        String tempName = getIntent().getStringExtra("name");
        tempPosition = getIntent().getIntExtra("position",0);
        Log.e("smile","tempPosition+++++++++++"+tempPosition);
        kindsAdd.setText(tempName);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = kindsAdd.getText().toString();
                if(name.isEmpty())
                {
                    kindsAdd.setError("不能为空");
                    kindsAdd.setFocusable(true);
                }else
                {
                    Log.e("smile","name是++++++++++++"+name);
                    Intent intent = new Intent();
                    intent.putExtra("position",tempPosition);
                    intent.putExtra("addname",name);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }
}
