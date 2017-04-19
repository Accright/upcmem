package com.upc.software.upcmem;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.Fragment.BarGraphFragment;
import com.upc.Fragment.LineChartFragment;
import com.upc.Fragment.PieChartFragment;
import com.upc.adapter.AddFragmentPageAdapter;
import com.upc.javabean.Record;
import com.upc.javabean.User;

import org.w3c.dom.Text;

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

    User user;
    private ArrayList<Fragment> fragmentList;
    private ViewPager viewPager;
    private TextView cursor;
    private TextView pieText,lineText,barText;
    private int index;//the index of tap
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        user = BmobUser.getCurrentUser(User.class);
        initViews();
        initCursorBar();
        initViewPager();
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        Fragment pieChartFragment = new PieChartFragment();
        Fragment lineChartFragment = new LineChartFragment();
        Fragment barGraphFragment = new BarGraphFragment();
        fragmentList.add(pieChartFragment);
        fragmentList.add(lineChartFragment);
        fragmentList.add(barGraphFragment);

        AddFragmentPageAdapter fragmentAdapter = new AddFragmentPageAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams ll = (android.widget.LinearLayout.LayoutParams) cursor
                        .getLayoutParams();

                if(index == position){
                    ll.leftMargin = (int) (index * cursor.getWidth() + positionOffset
                            * cursor.getWidth());
                }else if(index > position){
                    ll.leftMargin = (int) (index * cursor.getWidth() - (1 - positionOffset)* cursor.getWidth());
                }
                cursor.setLayoutParams(ll);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                index = state;
            }
        });
    }

    private void initViews() {
        viewPager = (ViewPager)findViewById(R.id.analyseviewpager);
        cursor = (TextView) findViewById(R.id.analysecursor);
        pieText = (TextView) findViewById(R.id.pie);
        lineText = (TextView) findViewById(R.id.line);
        barText = (TextView) findViewById(R.id.bar);

        pieText.setOnClickListener(new txListener(0));
        lineText.setOnClickListener(new txListener(1));
        barText.setOnClickListener(new txListener(2));
    }
    public class txListener implements View.OnClickListener{
        private int currentIndex=0;

        public txListener(int i) {
            currentIndex =i;
        }
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(currentIndex);
        }
    }
    public void initCursorBar(){
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int  tabLineLength = metrics.widthPixels / 3;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)cursor.getLayoutParams();
        lp.width = tabLineLength;
        cursor.setLayoutParams(lp);
    }

}
