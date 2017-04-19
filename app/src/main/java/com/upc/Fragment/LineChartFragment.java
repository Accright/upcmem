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
import android.widget.Toast;

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
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChartFragment extends Fragment {

    List<Float> inList,outList;
    List<Float> handleInList,handleOutList;
    List<Line> inLineList,outLineList,LineList;
    private LineChartView lineChart;
    private LineChartData mLineData;                    //图表数据
    private int numberOfLines = 2;                      //图上折线/曲线的显示条数
    private int maxNumberOfLines = 2;                   //图上折线/曲线的最多条数
    private int numberOfPoints = 12;                    //图上的节点数
    /*=========== 状态相关 ==========*/
    private boolean isHasAxes = true;                   //是否显示坐标轴
    private boolean isHasAxesNames = true;              //是否显示坐标轴名称
    private boolean isHasLines = true;                  //是否显示折线/曲线
    private boolean isHasPoints = true;                 //是否显示线上的节点
    private boolean isFilled = false;                   //是否填充线下方区域
    private boolean isHasPointsLabels = false;          //是否显示节点上的标签信息
    private boolean isCubic = true;                    //是否是曲线
    private boolean isPointsHasSelected = false;        //设置节点点击后效果(消失/显示标签)
    private boolean isPointsHaveDifferentColor = true;         //节点是否有不同的颜色
    /*=========== 其他相关 ==========*/
    private ValueShape pointsShape = ValueShape.CIRCLE; //点的形状(圆/方/菱形)
    User user;
    Handler hander;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_line_chart,container,false);
        user = BmobUser.getCurrentUser(User.class);
        lineChart =(LineChartView) viewRoot.findViewById(R.id.linechart);
        lineChart.setViewportCalculationEnabled(false);//禁用重新计算功能
        lineChart.setZoomType(ZoomType.VERTICAL);       //水平垂直缩放
        lineChart.setOnValueTouchListener(new ValueTouchListener());
        LineList = new ArrayList<Line>();//初始化LineList
        outBmobCount();
        inBmobCount();
        hander = new android.os.Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1:
                        handleOutList = (List<Float>) msg.obj;
                        List<PointValue> values = new ArrayList<>();
                        for (int j = 1; j <= numberOfPoints; ++j) {
                            values.add(new PointValue(j, handleOutList.get(j)));
                        }
                             /*========== 设置线的一些属性 ==========*/
                        Line line = new Line(values);               //根据值来创建一条线
                        line.setColor(ChartUtils.COLORS[3]);        //设置线的颜色
                        line.setShape(ValueShape.DIAMOND);                 //设置点的形状
                        line.setHasLines(isHasLines);               //设置是否显示线
                        line.setHasPoints(isHasPoints);             //设置是否显示节点
                        line.setCubic(isCubic);                     //设置线是否立体或其他效果
                        line.setFilled(isFilled);                   //设置是否填充线下方区域
                        line.setHasLabels(isHasPointsLabels);       //设置是否显示节点标签
                        //设置节点点击的效果
                        line.setHasLabelsOnlyForSelected(isPointsHasSelected);
                        //如果节点与线有不同颜色 则设置不同颜色
                        if (isPointsHaveDifferentColor) {
                            line.setPointColor(ChartUtils.COLORS[3]);
                        }
                        LineList.add(line);
                        break;
                    case 2:
                        handleInList = (List<Float>) msg.obj;
                        //LineList = new ArrayList<Line>();
                        values = new ArrayList<>();
                        for (int j = 1; j <= numberOfPoints; ++j) {
                            values.add(new PointValue(j, handleInList.get(j)));
                        }
                             /*========== 设置线的一些属性 ==========*/
                        Line line1 = new Line(values);               //根据值来创建一条线
                        line1.setColor(Color.parseColor("#33B5E5"));        //设置线的颜色
                        line1.setShape(pointsShape);                 //设置点的形状
                        line1.setHasLines(isHasLines);               //设置是否显示线
                        line1.setHasPoints(isHasPoints);             //设置是否显示节点
                        line1.setCubic(isCubic);                     //设置线是否立体或其他效果
                        line1.setFilled(isFilled);                   //设置是否填充线下方区域
                        line1.setHasLabels(isHasPointsLabels);       //设置是否显示节点标签
                        //设置节点点击的效果
                        line1.setHasLabelsOnlyForSelected(isPointsHasSelected);
                        //如果节点与线有不同颜色 则设置不同颜色
                        if (isPointsHaveDifferentColor) {
                            line1.setPointColor(Color.parseColor("#33B5E5"));
                        }
                        LineList.add(line1);
                        break;
                }
            }
        };
        Log.e("testcount","list的值是+"+LineList.size());
        mLineData = new LineChartData(LineList);                      //将所有的线加入线数据类中
        mLineData.setBaseValue(Float.NEGATIVE_INFINITY);           //设置基准数(大概是数据范围)
        //如果显示坐标轴
        if (isHasAxes) {
            Axis axisX = new Axis();                    //X轴
            Axis axisY = new Axis().setHasLines(true);  //Y轴
            axisX.setTextColor(Color.GRAY);             //X轴灰色
            axisY.setTextColor(Color.GRAY);             //Y轴灰色
            //setLineColor()：此方法是设置图表的网格线颜色 并不是轴本身颜色
            //如果显示名称
            if (isHasAxesNames) {
                axisX.setName("日期");                //设置名称
                axisY.setName("数额");
            }
            mLineData.setAxisXBottom(axisX);            //设置X轴位置 下方
            mLineData.setAxisYLeft(axisY);              //设置Y轴位置 左边
        } else {
            mLineData.setAxisXBottom(null);
            mLineData.setAxisYLeft(null);
        }
        lineChart.setLineChartData(mLineData);    //设置图表控件
        resetViewport();
        return viewRoot;
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
                                Log.e("testcount","测试Bmob统计数据  按照时间分类 对Number进行统计数值是"+testNum+"时间是"+testmonth+"类型是支出");
                            }
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = outList;
                            Log.e("testcount","查询出来的统计支出数据list是+++++"+outList.toString());
                            hander.sendMessage(msg);
                        } catch (JSONException e1) {
                            Log.e("testcount","JsonArrayError++++++++++++++++++");
                            e1.printStackTrace();
                        }
                    }else
                    {
                        Log.e("testcount","数据统计++++++++++++++查询无数据");
                    }
                }else
                {
                    Log.e("testcount","支出数据统计出错了+++++++++++++"+e.getMessage());
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
                                Log.e("testcount","测试Bmob统计数据  按照时间分类 对Number进行统计数值是"+testNum+"时间是"+testmonth+"类型是收入");
                            }
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = inList;
                            Log.e("testcount","查询出来的统计数据收入list是+++++"+inList.toString());
                            hander.sendMessage(msg);
                        } catch (JSONException e1) {
                            Log.e("testcount","JsonArrayError++++++++++++++++++");
                            e1.printStackTrace();
                        }
                    }else
                    {
                        Log.e("testcount","收入数据统计++++++++++++++查询无数据");
                    }
                }else
                {
                    Log.e("testcount","收入数据统计出错了+++++++++++++"+e.getMessage());
                }
            }
        });
    }
    /**
     * 重点方法，计算绘制图表
     */
    private void resetViewport() {
        //创建一个图标视图 大小为控件的最大大小
        final Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 1;                             //坐标原点在左下
        v.bottom = 1;
        v.top = 10000;                            //最高点为10000
        v.right = numberOfPoints ;           //右边为点 坐标从0开始 点号从1 需要 -1
        lineChart.setMaximumViewport(v);   //给最大的视图设置 相当于原图
        lineChart.setCurrentViewport(v);   //给当前的视图设置 相当于当前展示的图
    }
    /**
     * 节点触摸监听
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(getActivity(), "第 " + ((int) value.getX()) + " 月的数值是"+(int)value.getY(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }
    }
}
