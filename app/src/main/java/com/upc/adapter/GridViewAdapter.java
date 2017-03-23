package com.upc.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.upc.software.upcmem.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by 稻dao草re人n on 2017/2/1.
 */

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    LayoutInflater layoutInflater;
    TextView itemText;

    public GridViewAdapter(Context context,  List<String> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size()+1;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.kindsitem,null);
        itemText = (TextView) view.findViewById(R.id.kindsitem);
        if (i < list.size())
        {
            itemText.setText(list.get(i));
        }else
        {
            /*itemText.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            itemText.setGravity(Gravity.CENTER);*/
            itemText.setText("编辑");
        }
        return view;
    }
}
