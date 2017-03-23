package com.upc.software.upcmem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.upc.javabean.Record;
import com.upc.javabean.User;
import com.upc.swipemenulistView.IXListViewListener;
import com.upc.swipemenulistView.OnMenuItemClickListener;
import com.upc.swipemenulistView.SwipeMenu;
import com.upc.swipemenulistView.SwipeMenuCreator;
import com.upc.swipemenulistView.SwipeMenuItem;
import com.upc.swipemenulistView.SwipeMenuListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class OutKindsEditActivity extends AppCompatActivity implements IXListViewListener {


    private static final int KINDS_ADD = 1;
    private static final int KINDS_EDIT = 0;
    List<String> kindsList;
    ArrayAdapter<String> adapter;
    SwipeMenuListView smlv;
    FloatingActionButton fb ;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_out_kinds_edit);
        user = BmobUser.getCurrentUser(User.class);
        /************************************初始化控件**************************/
        smlv = (SwipeMenuListView) findViewById(R.id.kindsedit);
        fb = (FloatingActionButton) findViewById(R.id.kindseditaddnew);
        /******************************************************************/
        kindsList = user.getOutKinds();
        adapter = new ArrayAdapter<String>(this,R.layout.kindsedititem,R.id.kindsedititem,kindsList);
        smlv.setAdapter(adapter);
        smlv.setPullRefreshEnable(false);
        smlv.setPullLoadEnable(false);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OutKindsEditActivity.this,OutKindsAddActivity.class);
                startActivityForResult(intent,KINDS_ADD);
            }
        });
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("编辑");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        smlv.setMenuCreator(creator);

        // step 2. listener item click event
        smlv.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {

                if (kindsList==null) {
                    //nodata.setVisibility(View.VISIBLE);
                    //nodata.setText("您还没有任何记录，赶紧添加一下吧");
                    Log.e("smile","mappList是空的");
                }else {
                    String item = kindsList.get(position);
                    switch (index) {
                        case 0:
                            // open
                            edit(item,position);
                            break;
                        case 1:
                            // delete
//					delete(item);
                            kindsList.remove(position);
                            adapter.notifyDataSetChanged();
                            delete(item);
                            break;
                    }
                }
            }
        });

        // set MenuStateChangeListener

    }

    private void delete(String item) {
        kindsList.remove(item);
        User nUser = new User();
        nUser.setOutKinds(kindsList);
        nUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null)
                {
                    Log.e("smile","更新outkindsList的问题是"+e.getMessage());
                }
            }
        });
    }

    private void edit(String item,int position) {
        Intent intent = new Intent(OutKindsEditActivity.this,OutKindsAddActivity.class);
        intent.putExtra("name",item);
        intent.putExtra("position",position);
        startActivityForResult(intent,KINDS_EDIT);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case KINDS_EDIT:
                    String nameE = data.getStringExtra("addname");
                    int position  = data.getIntExtra("position",0);
                    Log.e("smile","outKindsEditActivity的position是++++++++++"+position);
                    kindsList.set(position,nameE);
                    User nUsere = new User();
                    nUsere.setOutKinds(kindsList);
                    nUsere.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e!=null)
                            {
                                Log.e("smile","添加用户outkinds遇到了错误"+e.getMessage());
                            }
                        }
                    });
                    Log.e("smile","kindsList是+++++++++++++++"+kindsList.toString());
                    adapter.notifyDataSetChanged();
                    break;
                case KINDS_ADD:
                    String name = data.getStringExtra("addname");
                    kindsList.add(name);
                    User nUser = new User();
                    nUser.setOutKinds(kindsList);
                    nUser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e!=null)
                            {
                                Log.e("smile","添加用户outkinds遇到了错误"+e.getMessage());
                            }
                        }
                    });
                    Log.e("smile","kindsList是+++++++++++++++"+kindsList.toString());
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("outke",(Serializable)kindsList);
        setResult(RESULT_OK,intent);
        finish();
        //super.onBackPressed();
    }

}
