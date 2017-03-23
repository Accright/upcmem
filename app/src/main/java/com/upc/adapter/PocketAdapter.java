package com.upc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.upc.javabean.Pocket;
import com.upc.javabean.PocketHolder;
import com.upc.software.upcmem.R;
import com.upc.swipemenulistView.BaseSwipListAdapter;

import java.util.List;

/**
 * Created by 稻dao草re人n on 2017/2/8.
 */

public class PocketAdapter extends BaseSwipListAdapter {
    private Context context;
    private List<Pocket> list;
    LayoutInflater layoutInflater;
    //private TextView pocketName,pocketNum,pocketCoin;//pocketitem控件初始化
    public PocketAdapter(Context context, List<Pocket> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
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
        if (view == null)
        {
            view =layoutInflater.inflate(R.layout.pocketitem,null);
            new PocketHolder(view);
        }
        PocketHolder pocketHolder = (PocketHolder) view.getTag();
        Pocket item = (Pocket) getItem(i);
        pocketHolder.pocketName.setText(item.getKind());
        pocketHolder.pocketNum.setText(item.getNumber().toString());
        pocketHolder.pocketCoin.setText(item.getCoinType());
        return view;
    }
}
