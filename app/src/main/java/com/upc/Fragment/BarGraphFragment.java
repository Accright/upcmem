package com.upc.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upc.javabean.Record;
import com.upc.javabean.User;
import com.upc.software.upcmem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class BarGraphFragment extends Fragment {

    private User user;
    private ColumnChartView columnChartView;
    Handler handler;
    List<Float> inList,outList;
    List<Float> handleInList,handleOutList;
    List<Column> columns = new ArrayList<>();
    private int numberOfPoints = 12;//columunNum
    /*========== 状态相关 ==========*/
    private boolean isHasAxes = true;                       //是否显示坐标轴
    private boolean isHasAxesNames = true;                  //是否显示坐标轴
    private boolean isHasColumnLabels = false;              //是否显示列标签
    private boolean isColumnsHasSelected = false;           //设置列点击后效果(消失/显示标签)

    /*========== 标志位相关 ==========*/
    private static final int DEFAULT_DATA = 0;              //默认数据标志位
    private static final int SUBCOLUMNS_DATA = 1;           //多子列数据标志位
    private static final int NEGATIVE_SUBCOLUMNS_DATA = 2;  //反向多子列标志位
    private static final int STACKED_DATA = 3;              //堆放数据标志位
    private static final int NEGATIVE_STACKED_DATA = 4;     //反向堆放数据标志位
    private static boolean IS_NEGATIVE = false;             //是否需要反向标志位

    /*========== 数据相关 ==========*/
    private ColumnChartData mColumnChartData;               //柱状图数据
    private int dataType = DEFAULT_DATA;                    //默认数据状态
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_bar_graph,container,false);
        user = BmobUser.getCurrentUser(User.class);
        columnChartView =(ColumnChartView) viewRoot.findViewById(R.id.columnchart);
        /*outBmobCount();
        inBmobCount();*/
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1:
                        Log.e("columntest","执行到case1");
                        handleOutList = (List<Float>) msg.obj;
                        List<SubcolumnValue> values = new ArrayList<>();
                        for (int j = 1; j <= numberOfPoints; ++j) {
                            values.add(new SubcolumnValue(handleOutList.get(j),Color.parseColor("#330033")));
                        }
                       /*===== 柱状图相关设置 =====*/
                        Column column = new Column(values);
                        column.setHasLabels(isHasColumnLabels);                    //没有标签
                        column.setHasLabelsOnlyForSelected(isColumnsHasSelected);  //点击只放大
                        columns.add(column);
                        break;
                    case 2:
                        Log.e("columntest","执行到case2");
                        handleInList = (List<Float>) msg.obj;
                        values = new ArrayList<>();
                        for (int j = 1; j <= numberOfPoints; ++j) {
                            values.add(new SubcolumnValue(handleInList.get(j),Color.parseColor("#330033")));
                        }
                       /*===== 柱状图相关设置 =====*/
                        Column column1 = new Column(values);
                        column1.setHasLabels(isHasColumnLabels);                    //没有标签
                        column1.setHasLabelsOnlyForSelected(isColumnsHasSelected);  //点击只放大
                        columns.add(column1);
                        break;
                }
                Log.e("columntest","columns 是+++++++++++++++"+columns.toString());
                mColumnChartData = new ColumnChartData(columns);               //设置数据
                mColumnChartData.setStacked(false);                          //设置是否堆叠
        /*===== 坐标轴相关设置 类似于Line Charts =====*/
                if (isHasAxes) {
                    Axis axisX = new Axis();
                    Axis axisY = new Axis().setHasLines(true);
                    if (isHasAxesNames) {
                        axisX.setName("月份");
                        axisY.setName("数值");
                    }
                    mColumnChartData.setAxisXBottom(axisX);
                    mColumnChartData.setAxisYLeft(axisY);
                } else {
                    mColumnChartData.setAxisXBottom(null);
                    mColumnChartData.setAxisYLeft(null);
                }
                columnChartView.setColumnChartData(mColumnChartData);
            }
        };
        return  viewRoot;
    }
    private void outBmobCount() {
        BmobQuery<Record> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("type","支出");
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.sum(new String[]{"number"});
        bmobQuery.groupby(new String[]{"month"});
        bmobQuery.order("-month");
        bmobQuery.findStatistics(Record.class, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e==null)
                {
                    if (jsonArray!=null)
                    {
                        int length = jsonArray.length();
                        outList = new ArrayList<Float>();
                        for(int temp = 0;temp<=numberOfPoints;temp++)
                        {
                            outList.add((float)0);
                        }
                        try {
                            for (int i=0;i<length;i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                float testNum = (float) jsonObject.getDouble("_sumNumber");
                                int testmonth = jsonObject.getInt("month");
                                outList.set(testmonth,testNum);
                                //String type = jsonObject.getString("type");
                                // String createdDate = jsonObject.getString("createdAt");
                                Log.e("columntest","测试Bmob统计数据  按照时间分类 对Number进行统计数值是"+testNum+"时间是"+testmonth+"类型是支出");
                            }
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = outList;
                            Log.e("columntest","查询出来的统计支出数据list是+++++"+outList.toString());
                            handler.sendMessage(msg);
                        } catch (JSONException e1) {
                            Log.e("columntest","JsonArrayError++++++++++++++++++");
                            e1.printStackTrace();
                        }
                    }else
                    {
                        Log.e("columntest","数据统计++++++++++++++查询无数据");
                    }
                }else
                {
                    Log.e("columntest","支出数据统计出错了+++++++++++++"+e.getMessage());
                }
            }
        });
    }

    private void inBmobCount() {
        BmobQuery<Record> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("type","收入");
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.sum(new String[]{"number"});
        bmobQuery.groupby(new String[]{"month"});
        bmobQuery.order("-month");
        bmobQuery.findStatistics(Record.class, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e==null)
                {
                    if (jsonArray!=null)
                    {
                        int length = jsonArray.length();
                        inList = new ArrayList<Float>();
                        for(int temp = 0;temp<=numberOfPoints;temp++)
                        {
                            inList.add((float)0);
                        }//初始化数据集
                        try {
                            for (int i=0;i<length;i++)
                            {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                float testNum = (float) jsonObject.getDouble("_sumNumber");
                                int testmonth = jsonObject.getInt("month");
                                inList.set(testmonth,testNum);
                                //String type = jsonObject.getString("type");
                                // String createdDate = jsonObject.getString("createdAt");
                                Log.e("columntest","测试Bmob统计数据  按照时间分类 对Number进行统计数值是"+testNum+"时间是"+testmonth+"类型是收入");
                            }
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = inList;
                            Log.e("columntest","查询出来的统计数据收入list是+++++"+inList.toString());
                            handler.sendMessage(msg);
                        } catch (JSONException e1) {
                            Log.e("columntest","JsonArrayError++++++++++++++++++");
                            e1.printStackTrace();
                        }
                    }else
                    {
                        Log.e("columntest","收入数据统计++++++++++++++查询无数据");
                    }
                }else
                {
                    Log.e("columntest","收入数据统计出错了+++++++++++++"+e.getMessage());
                }
            }
        });
    }
}
