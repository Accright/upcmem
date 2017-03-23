package com.upc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.upc.javabean.FilterHolder;
import com.upc.software.upcmem.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 稻dao草re人n on 2017/3/23.
 */

public class FilterMultAdapter extends BaseAdapter {
    // 填充数据的list
    private ArrayList<String> list;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer,Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    public FilterMultAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate(){
        for(int i=0; i<list.size();i++) {
            getIsSelected().put(i,false);
        }
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        FilterHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new FilterHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.filterkinditem, null);
            holder.kindname = (TextView) convertView.findViewById(R.id.item_tv);
            holder.check = (CheckBox) convertView.findViewById(R.id.item_cb);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (FilterHolder) convertView.getTag();
        }


        // 设置list中TextView的显示
        holder.kindname.setText(list.get(i));
        // 监听checkBox并根据原来的状态来设置新的状态
        holder.check.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (isSelected.get(i)) {
                    isSelected.put(i, false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(i, true);
                    setIsSelected(isSelected);
                }

            }
        });
        // 根据isSelected来设置checkbox的选中状况
        holder.check.setChecked(getIsSelected().get(i));
        return convertView;
    }
    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        FilterMultAdapter.isSelected = isSelected;
    }
}
