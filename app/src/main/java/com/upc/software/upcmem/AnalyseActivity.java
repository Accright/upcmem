package com.upc.software.upcmem;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.upc.javabean.Record;
import com.upc.javabean.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LoggingPermission;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class AnalyseActivity extends AppCompatActivity {

    private PieChartView pieChartView;
    User user;
    List<String> outkinds,inkinds;
    List<SliceValue> values,handleValues;
    Handler handler;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        user = BmobUser.getCurrentUser(User.class);
        initView();
        setDatas();
    }

    private void setDatas() {
        outkinds = user.getOutKinds();
        inkinds = user.getInKinds();
        final List<Integer> listCountOut = new ArrayList<Integer>();
        final List<Integer> listCountIn = new ArrayList<Integer>();
        final int inValue = inkinds.size();
        final int outValue = outkinds.size();
        /***************获取不同支出分类的计数*******************/
        for(int i = 0;i<outkinds.size();i++)
        {
            BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
            bmobQuery.addWhereEqualTo("userId",user.getObjectId());
            bmobQuery.addWhereEqualTo("kind",outkinds.get(i));
            bmobQuery.addWhereEqualTo("deleted",false);
            bmobQuery.count(Record.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e!=null)
                    {
                        Log.e("smile","分析计数失败了支出"+e.getMessage());
                    }else
                    {
                        Log.e("smile","分析计数成功了支出"+integer);
                        listCountOut.add(integer);
                        values = new ArrayList<>();
                        for (int i = 0; i < outValue; ++i) {
                            SliceValue sliceValue = new SliceValue((float) listCountOut.get(i), ChartUtils.pickColor());
                            sliceValue.setLabel(outkinds.get(i));
                            values.add(sliceValue);
                            Message msg = new Message();
                            msg.what=1;
                            msg.obj = values;
                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        }
        /***************获取不同收入分类的计数*******************/
        for(int i = 0;i<inkinds.size();i++)
        {
            BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
            bmobQuery.addWhereEqualTo("deleted",false);
            bmobQuery.addWhereEqualTo("userId",user.getObjectId());
            bmobQuery.addWhereEqualTo("kind",inkinds.get(i));
            bmobQuery.count(Record.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e!=null)
                    {
                        Log.e("smile","分析计数失败了收入"+e.getMessage());
                    }else
                    {
                        Log.e("smile","分析计数成功了收入"+integer);
                        listCountIn.add(integer);
                        values = new ArrayList<SliceValue>();
                        for(int j = 0;j<= inValue;j++)
                        {
                            SliceValue sliceValue = new SliceValue((float)listCountIn.get(j),ChartUtils.pickColor());
                            sliceValue.setLabel(inkinds.get(j));
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = values;
                            handler.sendMessage(msg);
                        }
                    }
                }
            });
        }
        /************处理handler的message****************/
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1:handleValues = new ArrayList<>();
                        handleValues = (List<SliceValue>) msg.obj;
                        //mPieChartData = new PieChartData(handleValues);
                        /*===== 设置相关属性 类似Line Chart =====*/
                        Log.e("smile","handle出来的Value是++++++"+handleValues.toString());
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
                    case 2:handleValues = new ArrayList<>();
                        handleValues = (List<SliceValue>) msg.obj;
                        /*===== 设置相关属性 类似Line Chart =====*/
                        Log.e("smile","handle出来的Value是++++++"+handleValues.toString());
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
                    default:break;
                }
            }
        };
        /*===== 设置相关属性 类似Line Chart =====*//*
        Log.e("smile","handle出来的Value是++++++"+handleValues.toString());
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
        pieChartView.setPieChartData(mPieChartData);*/
        pieChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    private void initView() {
        pieChartView = (PieChartView) findViewById(R.id.analysepie);//初始化饼状图控件
    }
    /**
     * 每部分点击监听
     */
    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Toast.makeText(AnalyseActivity.this, outkinds.get (arcIndex)+"类别有: " +(int) value.getValue() + "条数据", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
        }
    }
}
