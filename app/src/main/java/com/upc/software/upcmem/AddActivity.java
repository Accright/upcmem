package com.upc.software.upcmem;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.upc.Fragment.InFragment;
import com.upc.Fragment.OutFragment;
import com.upc.adapter.AddFragmentPageAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private TextView cursorbar;
    private TextView inView,outView;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);
        initViews();
        initCursorBar();
        initViewPager();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.addviewpager);
        fragmentList = new ArrayList<Fragment>();
        Fragment inFragment = new InFragment();
        Fragment outFragment = new OutFragment();
        fragmentList.add(outFragment);
        fragmentList.add(inFragment);

        viewPager.setAdapter(new AddFragmentPageAdapter(getSupportFragmentManager(),fragmentList));
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initViews() {
        inView = (TextView) findViewById(R.id.addin);
        outView = (TextView) findViewById(R.id.addout);

        outView.setOnClickListener(new txListener(0));
        inView.setOnClickListener(new txListener(1));
    }
    public class txListener implements View.OnClickListener{
        private int index=0;

        public txListener(int i) {
            index =i;
        }
        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    public void initCursorBar(){
        cursorbar = (TextView) super.findViewById(R.id.cursor);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int  tabLineLength = metrics.widthPixels / 2;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)cursorbar.getLayoutParams();
        lp.width = tabLineLength;
        cursorbar.setLayoutParams(lp);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            RelativeLayout.LayoutParams ll = (android.widget.RelativeLayout.LayoutParams) cursorbar
                    .getLayoutParams();

            if(index == arg0){
                ll.leftMargin = (int) (index * cursorbar.getWidth() + arg1
                        * cursorbar.getWidth());
            }else if(index > arg0){
                ll.leftMargin = (int) (index * cursorbar.getWidth() - (1 - arg1)* cursorbar.getWidth());
            }
            cursorbar.setLayoutParams(ll);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            index = arg0;
        }
    }


}
