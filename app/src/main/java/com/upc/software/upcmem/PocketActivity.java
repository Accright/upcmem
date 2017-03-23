package com.upc.software.upcmem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.upc.adapter.PocketAdapter;
import com.upc.javabean.Pocket;
import com.upc.javabean.Record;
import com.upc.javabean.User;
import com.upc.swipemenulistView.IXListViewListener;
import com.upc.swipemenulistView.OnMenuItemClickListener;
import com.upc.swipemenulistView.RefreshTime;
import com.upc.swipemenulistView.SwipeMenu;
import com.upc.swipemenulistView.SwipeMenuCreator;
import com.upc.swipemenulistView.SwipeMenuItem;
import com.upc.swipemenulistView.SwipeMenuListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class PocketActivity extends AppCompatActivity implements IXListViewListener {

    private static final int POCKET_EDIT = 1;
    private static final int POCKET_ADD = 0;
    SwipeMenuListView pocketListView;
    List<Pocket> list;
    User user;
    PocketAdapter pocketAdapter;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket);
        user = BmobUser.getCurrentUser(User.class);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pockettoolbar);
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(R.drawable.back);

        /***********************
         * 初始化并设置pocketlistview不可下拉刷新
         */
        pocketListView = (SwipeMenuListView) findViewById(R.id.pocketlistview);
        pocketListView.setPullRefreshEnable(true);
        pocketListView.setPullLoadEnable(false);
        pocketListView.setXListViewListener(this);

        onRefresh();
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
       pocketListView.setMenuCreator(creator);

        // step 2. listener item click event
        pocketListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {

                if (list==null) {
                    //nodata.setVisibility(View.VISIBLE);
                    //nodata.setText("您还没有任何记录，赶紧添加一下吧");
                    Log.e("smile","mappList是空的");
                }else {
                    Pocket item = list.get(position);
                    switch (index) {
                        case 0:
                            // open
                            edit(item,position);
                            break;
                        case 1:
                            // delete
//					delete(item);
                            list.remove(position);
                            pocketAdapter.notifyDataSetChanged();
                            delete(item);
                            break;
                    }
                }
            }
        });
        /*************************查询pocket数据**********************************************/
        BmobQuery<Pocket> bmobQuery = new BmobQuery<Pocket>();
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.findObjects(new FindListener<Pocket>() {
            @Override
            public void done(List<Pocket> list, BmobException e) {
                if (e!=null)
                {
                    Log.e("smile","查询pocket失败++++++++++"+e.getMessage());
                }else
                {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = list;
                    handler.sendMessage(message);
                }
            }
        });
        /************************handler数据*********************/
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case 1:
                        list = new ArrayList<Pocket>();
                        list = (List<Pocket>) msg.obj;
                        Log.e("smile","list在handler里是+++++++++"+list.toString());
                        break;
                    default:break;
                }
                pocketAdapter = new PocketAdapter(getApplicationContext(),list);
                pocketListView.setAdapter(pocketAdapter);
            }
        };
        //Log.e("smile","list在handler外是+++++++++"+list.toString());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.pocketadd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PocketActivity.this,PocketAddActivity.class);
                startActivityForResult(intent,POCKET_ADD);
            }
        });
    }

    private void edit(Pocket item, int position) {
        Intent intent = new Intent(PocketActivity.this,PocketEditActivity.class);
        intent.putExtra("item",(Serializable)item);
        intent.putExtra("position",position);
        startActivityForResult(intent,POCKET_EDIT);
    }

    private void delete(Pocket item) {
        item.deleted = true;
        item.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null)
                {
                    Log.e("smile","删除pocket失败+++++++++"+e.getMessage());
                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case POCKET_ADD:
                    Pocket pocket = (Pocket) data.getSerializableExtra("nPocket");
                    list.add(pocket);
                    pocketAdapter.notifyDataSetChanged();
                    break;
                case POCKET_EDIT:
                    Pocket pockete = (Pocket) data.getSerializableExtra("nPocket");
                    int position = data.getIntExtra("position",0);
                    list.set(position,pockete);
                    pocketAdapter.notifyDataSetChanged();
                    break;
                default:break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        /*************************刷新pocket数据**********************************************/
        BmobQuery<Pocket> bmobQuery = new BmobQuery<Pocket>();
        bmobQuery.addWhereEqualTo("userId",user.getObjectId());
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        bmobQuery.addWhereEqualTo("deleted",false);
        bmobQuery.findObjects(new FindListener<Pocket>() {
            @Override
            public void done(List<Pocket> list, BmobException e) {
                if (e!=null)
                {
                    Log.e("smile","查询pocket失败++++++++++"+e.getMessage());
                }else
                {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = list;
                    handler.sendMessage(message);
                    Log.e("smile","查询成功了！~");
                }
            }
        });
        onLoad();
    }

    @Override
    public void onLoadMore() {
        onLoad();
    }
    /*下拉刷新的三个函数，分别为停止刷新，开始刷新，加载更多****************/
    private void onLoad() {
        pocketListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
        pocketListView.stopRefresh();
        pocketListView.stopLoadMore();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pocket, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.transfer)
        {
            Intent intent = new Intent(PocketActivity.this,TransferActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
