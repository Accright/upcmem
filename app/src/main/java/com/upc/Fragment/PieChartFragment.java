package com.upc.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.upc.javabean.Record;
import com.upc.javabean.User;
import com.upc.software.upcmem.AnalyseActivity;
import com.upc.software.upcmem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartFragment extends Fragment {


    private PieChartView pieChartView;
    private Button changeData;
    private int tag = 0;
    List<Integer> listCountOut,listCountIn;
    User user;
    List<String> outkinds,inkinds;
    List<SliceValue> outvalues,invalues,handleValues;
    Handler handler;
    int i,j;//init i of  cycle
    /*========= 数据相关 =========*/
    private PieChartData mPieChartData;                 //饼状图数据
    /*========= 状态相关 =========*/
    private boolean isExploded = false;                 //每块之间是否分离
    private boolean isHasLabelsInside = true;          //标签在内部
    private boolean isHasLabelsOutside = false;         //标签在外部
    private boolean isHasCenterCircle = true;          //空心圆环
    private boolean isPiesHasSelected = false;          //块选中标签样式
    private boolean isHasCenterSingleText = true;      //圆环中心单行文字
    private boolean isHasCenterDoubleText = true;      //圆环中心双行文字
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot = inflater.inflate(R.layout.fragment_pie_chart,container,false);
        user = BmobUser.getCurrentUser(User.class);
        pieChartView = (PieChartView) viewRoot.findViewById(R.id.analysepie);
        changeData = (Button) viewRoot.findViewById(R.id.changedatepie);
        setDatas();
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 0:handleValues = new ArrayList<>();
                        handleValues = (List<SliceValue>) msg.obj;
                        //mPieChartData = new PieChartData(handleValues);
                        /*===== 设置相关属性 类似Line Chart =====*/
                        Log.e("smile","支出handle出来的Value是++++++"+handleValues.toString());
                        mPieChartData = new PieChartData(handleValues);
                        mPieChartData.setHasLabels(isHasLabelsInside);
                        mPieChartData.setHasLabelsOnlyForSelected(isPiesHasSelected);
                        mPieChartData.setHasLabelsOutside(isHasLabelsOutside);
                        mPieChartData.setHasCenterCircle(isHasCenterCircle);
                        if (isExploded) {
                            mPieChartData.setSlicesSpacing(18);                 //分离间距为18
                        }

                        //是否显示单行文本
                        if (isHasCenterSingleText) {
                            mPieChartData.setCenterText1("支出分类");             //文本内容
                        }

                        //是否显示双行文本
                        if (isHasCenterDoubleText) {
                            mPieChartData.setCenterText2("饼状图");             //文本内容
                        }
                        pieChartView.setPieChartData(mPieChartData);
                        break;
                    case 1:handleValues = new ArrayList<>();
                        handleValues = (List<SliceValue>) msg.obj;
                        /*===== 设置相关属性 类似Line Chart =====*/
                        Log.e("smile","收入handle出来的Value是++++++"+handleValues.toString());
                        mPieChartData = new PieChartData(handleValues);
                        mPieChartData.setHasLabels(isHasLabelsInside);
                        mPieChartData.setHasLabelsOnlyForSelected(isPiesHasSelected);
                        mPieChartData.setHasLabelsOutside(isHasLabelsOutside);
                        mPieChartData.setHasCenterCircle(isHasCenterCircle);
                        if (isExploded) {
                            mPieChartData.setSlicesSpacing(18);                 //分离间距为18
                        }
                        //是否显示单行文本
                        if (isHasCenterSingleText) {
                            mPieChartData.setCenterText1("收入分类");             //文本内容
                        }
                        //是否显示双行文本
                        if (isHasCenterDoubleText) {
                            mPieChartData.setCenterText2("饼状图");             //文本内容
                        }
                        pieChartView.setPieChartData(mPieChartData);
                        break;
                    default:break;
                }
            }
        };
        changeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tag == 0)
                {
                    tag = 1;
                    setDatas();
                    pieChartView.startDataAnimation();
                }else
                {
                    tag = 0;
                    setDatas();
                    pieChartView.startDataAnimation();
                }
            }
        });
        return viewRoot;
    }

    private void setDatas() {


        /***************获取不同支出分类的计数*******************/
        if(tag == 0)
        {
            getOutDatas();
        }else
        {
            getInDatas();
        }
        /************处理handler的message****************/
        pieChartView.setOnValueTouchListener(new ValueTouchListener());
    }
    /**
     * 每部分点击监听
     */
    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Toast.makeText(getActivity(), String.valueOf(value.getLabelAsChars()) +"类别有: " +(int) value.getValue() + "条数据", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onValueDeselected() {
        }
    }
    private  void getOutDatas()
    {
        outvalues = new ArrayList<>();
        Log.e("smile","piechart执行到支出筛选");
        BmobQuery<Record> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("type","支出");
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.groupby(new String[]{"kind"});
        bmobQuery.order("-created");
        bmobQuery.setHasGroupCount(true);
        bmobQuery.findStatistics(Record.class,new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null)
                {
                    if (jsonArray != null)
                    {
                        try {
                        int length = jsonArray.length();
                        for (int i=0;i<length;i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String lable = jsonObject.getString("kind");
                            int num = jsonObject.getInt("_count");
                            SliceValue sliceValue = new SliceValue((float)num,ChartUtils.pickColor());
                            sliceValue.setLabel(lable);
                            outvalues.add(sliceValue);
                        }
                            Message msg = new Message();
                            msg.what = tag;
                            msg.obj = outvalues;
                            handler.sendMessage(msg);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }else
                    {
                        Log.e("smile","查询无数据 数组是空的");
                    }
                }else
                {
                    Log.e("smile","查询出错了++"+e.getMessage());
                }
            }
        });
    }
    private void getInDatas() {
        invalues = new ArrayList<>();
        Log.e("smile","piechart执行到收入筛选");
        BmobQuery<Record> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("type", "收入");
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.groupby(new String[]{"kind"});
        bmobQuery.order("-created");
        bmobQuery.setHasGroupCount(true);
        bmobQuery.findStatistics(Record.class, new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
                    if (jsonArray != null) {
                        try {
                            int length = jsonArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String lable = jsonObject.getString("kind");
                                int num = jsonObject.getInt("_count");
                                SliceValue sliceValue = new SliceValue((float) num, ChartUtils.pickColor());
                                sliceValue.setLabel(lable);
                                invalues.add(sliceValue);
                            }
                            Message msg = new Message();
                            msg.what = tag;
                            msg.obj = invalues;
                            handler.sendMessage(msg);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else
                    {
                        Log.e("smile","查询无数据 数组是空的");
                    }
                }else
                {
                    Log.e("smile","查询出错了++"+e.getMessage());
                }
            }
        });
    }
}
