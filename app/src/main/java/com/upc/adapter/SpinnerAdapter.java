package com.upc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.upc.javabean.Pocket;
import com.upc.javabean.Record;
import com.upc.software.upcmem.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 稻dao草re人n on 2017/2/2.
 */

public class SpinnerAdapter extends BaseAdapter {
    //private List<String> nameList;
    //private List<Integer> numList;
    private List<HashMap<String,Pocket>> methodList;
    private Context context;
    LayoutInflater layoutInflater;
    TextView nameText;
    TextView numText;
    TextView coinText;
    public SpinnerAdapter(Context context, List<HashMap<String,Pocket>> list) {
        this.context = context;
        //this.nameList = nameList;
        //this.numList = numList;
        this.methodList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return methodList.size();
    }

    @Override
    public Object getItem(int i) {
        return methodList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

   /* public static int getPosition(String name)
    {
        int returnNum = 0;
        for (int i=0;i<methodList.size();i++)
        {
            if (name==methodList.get(i).get("mt").getKind())
            {
                returnNum = i;
            }
        }
        return returnNum;
    }*/

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.spinneritem,null);
        nameText = (TextView) view.findViewById(R.id.methodname);
        numText = (TextView) view.findViewById(R.id.methodnum);
        coinText = (TextView) view.findViewById(R.id.spinnercoin);
        nameText.setText(methodList.get(i).get("mt").getKind());
        numText.setText(methodList.get(i).get("mt").getNumber().toString());
        coinText.setText(methodList.get(i).get("mt").getCoinType());
        return view;
    }
}
