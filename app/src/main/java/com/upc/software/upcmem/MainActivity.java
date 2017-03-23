package com.upc.software.upcmem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.upc.adapter.FilterMultAdapter;
import com.upc.adapter.SpinnerAdapter;
import com.upc.dateSelect.CalendarPickerView;
import com.upc.javabean.CircleImageView;
import com.upc.javabean.Record;
import com.upc.javabean.RecordHolder;
import com.upc.javabean.User;
import com.upc.swipemenulistView.BaseSwipListAdapter;
import com.upc.swipemenulistView.IXListViewListener;
import com.upc.swipemenulistView.OnMenuItemClickListener;
import com.upc.swipemenulistView.RefreshTime;
import com.upc.swipemenulistView.SwipeMenu;
import com.upc.swipemenulistView.SwipeMenuCreator;
import com.upc.swipemenulistView.SwipeMenuItem;
import com.upc.swipemenulistView.SwipeMenuListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,IXListViewListener {

    private List<Record> mAppList;
    private AppAdapter mAdapter;
    private SwipeMenuListView mListView;
    private User user;
    //初始化侧边栏控件
    CircleImageView slideImg;
    TextView slideName;
    //初始化日期选择窗格
    private AlertDialog theDialog;
    private CalendarPickerView dialogView;
    Date startDate,endDate;
    //将查询数据传递出来的handler
    private Handler handler ;
    //private TextView nodata;//无数据时显示
    //***********数据筛选**************/
    List<String> outkinds,inkinds;//获取用户所拥有的支出和收入类别
    ArrayAdapter<String> typeAdapter;//对支出和收入的type筛选创建适配器
    FilterMultAdapter filterMultAdapterIn,filterMultAdapterOut;//对支出和收入的listView创建适配器
    ListView listView;//this is the alert list for kinds
    final static int STATE_OUT = 0;
    final static int STATE_IN = 1;
    final static int STATE_NONE = -1;//三种状态 分别为支出筛选 收入筛选 无筛选
    private int state;//三种状态的标识符
    private List<String> filterOut = new ArrayList<>();//bmobQuery的支出筛选查询条件
    private List<String> filterIn = new ArrayList<>();//bmobQuery的收入筛选查询条件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this,"665d96f8c74b324adb8b1340e13e1591");
        Log.i("smile","初始化bmob成功");
        setContentView(R.layout.activity_main);
        user = BmobUser.getCurrentUser(User.class);

        if(user==null)
        {
            Log.e("smile","主界面的user确实是空的");
            android.support.v7.app.AlertDialog alert =new AlertDialog.Builder(this).setCancelable(false).setTitle("重新登录").setMessage("登录失效，请重新登录").create();
            alert.show();
            Log.e("smile","即将跳转到登录界面");
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            Log.e("smile","main程序即将进行结束");
            MainActivity.this.finish();
        }else
        {
            //swipemenu相关
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
            navigationView.setNavigationItemSelectedListener(this);
            //进行slidemenu初始化
            View headerView = navigationView.getHeaderView(0);
            slideImg = (CircleImageView) headerView.findViewById(R.id.imageView);
            slideName = (TextView) headerView.findViewById(R.id.slidenickname);
            //Log.i("smile","清空后的user是++++++"+user.toString());
            Log.i("smile","slidemenu初始化成功");

            initSlideImg();
            //****************************************slidemenu相关****************************************/
            //mAppList = getPackageManager().getInstalledApplications(0);
            //initmAppList();
           // Log.e("smile","mappList111111的值是+++++"+mAppList.get(0));
            //Log.e("smile","mappList222222的值是+++++"+mAppList.get(0));
            /*******
             * 为listView添加下拉刷新,初始化无数据时的列表
             */
            mListView = (SwipeMenuListView) findViewById(R.id.listview);
            mListView.setPullRefreshEnable(true);
            mListView.setPullLoadEnable(true);
            mListView.setXListViewListener(this);
            handler = new Handler() {
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case 1:
                        mAppList = new ArrayList<Record>();
                        mAppList =(List<Record>) msg.obj;
                        Log.e("smile","传递出来的list赋值之后的mAppList的值是++++++"+mAppList.toString());break;
                    default:break;
                }
                Log.e("smile","mAppList的实际值是++++++++"+mAppList.toString());

                mAdapter = new AppAdapter();
                Log.e("smile","mAdapter的数值是+++++"+mAdapter.toString());
                Log.e("smile","mlistView是否已经获得+++++++++"+mListView.toString());
                mListView.setAdapter(mAdapter);
            }
            };
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
            mListView.setMenuCreator(creator);

            // step 2. listener item click event
            mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                @Override
                public void onMenuItemClick(int position, SwipeMenu menu, int index) {

                    if (mAppList==null) {
                        //nodata.setVisibility(View.VISIBLE);
                        //nodata.setText("您还没有任何记录，赶紧添加一下吧");
                        Log.e("smile","mappList是空的");
                    }else {
                        Record item = mAppList.get(position);
                        switch (index) {
                            case 0:
                                // open
                                edit(item);
                                break;
                            case 1:
                                // delete
//					delete(item);
                                mAppList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                delete(item);
                                break;
                        }
                    }
                }
            });

            // set MenuStateChangeListener

            // other setting

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addnew);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,AddActivity.class);
                    startActivity(intent);
                }
            });

            /********点击进入详情信息界面************/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mAppList==null) {
                    //nodata.setVisibility(View.VISIBLE);
                    //nodata.setText("您还没有任何记录，赶紧添加一下吧");
                    Log.e("smile","mappList是空的");
                }else {
                    Record item = mAppList.get(i-1);
                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item",item);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        /******************************************************************/

            //查询数据：
            BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            bmobQuery.addWhereEqualTo("userId",user.getObjectId()).addWhereEqualTo("deleted",false).findObjects(new FindListener<Record>() {
                @Override
                public void done(List<Record> list, BmobException e) {
                    if(e==null)
                    {
                     //   if(!list.isEmpty()) {
                            Message message = new Message();
                            message.obj = list;
                            //Log.e("smile","查询的list值是+++++"+list.toString());
                            message.what= 1;
                            handler.sendMessage(message);//将获得的list发送出去
                    //    }else
                    //    {
                    //        Log.e("smile","查询的list是空的");
                     //   }
                    }
                }
            });

        }//else结束
        }//Oncreate结束

    private void initSlideImg() {
        /***********
         * 下载图片
         */
        if(user.getImageURL()!=null)
        {
            BmobFile bmobfile =new BmobFile(user.getUsername()+".png","",user.getImageURL());
            bmobfile.download(new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Log.e("smile","下载成功,保存路径:"+s);
                        Bitmap bm = BitmapFactory.decodeFile(s);
                        slideImg.setImageBitmap(bm);
                    }else{
                        Log.e("smile","下载失败："+e.getErrorCode()+","+e.getMessage());
                    }
                }
                @Override
                public void onProgress(Integer integer, long l) {
                }
            });
        }
        slideName.setText(user.getNickName());
    }


    private void delete(Record item) {
        // delete app
        item.setDeleted(true);
        item.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e!=null)
                {
                    Log.e("smile","删除遇到的问题是++++++++"+e.getMessage());
                }else
                    Log.e("smile","删除成功");
            }
        });
    }

    private void edit(Record item) {
        // open app
       String userid = item.getUserId();
        Intent intent = new Intent(MainActivity.this,ModifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("item",item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //侧滑栏点击响应
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sliderecord) {
            onRefresh();
            // Handle the camera action
        } else if (id == R.id.slidepaint) {
            Log.e("smile","点击上了报表分析模块");
            Intent intent = new Intent(MainActivity.this,AnalyseActivity.class);
            startActivity(intent);
        } else if (id == R.id.slidepersonal) {
            Log.e("smile","点击上了个人信息");
            Intent intent = new Intent(MainActivity.this,PersonalActivity.class);
            startActivity(intent);
        }else if (id == R.id.imageView)
        {
            Intent intent = new Intent(MainActivity.this,PersonalActivity.class);
            startActivity(intent);
        }else if(id == R.id.slideloginout)
        {
            BmobUser.logOut();
            finish();
        }else if (id == R.id.slidepocket)
        {
            Intent intent  = new Intent(MainActivity.this, PocketActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
/*下拉刷新的三个函数，分别为停止刷新，开始刷新，加载更多****************/
    private void onLoad() {
        mListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
        mListView.stopRefresh();

        mListView.stopLoadMore();

    }
    @Override
    public void onRefresh() {
        if(startDate==null || endDate.equals(""))
        {
            if (filterIn.size()==0&&filterOut.size()!=0)
            {
                Log.e("smile","执行到对支出进行筛选了");
                BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                Log.e("smile","ReFresh查询过程中，filterout is++++++++++"+filterOut.toString()+"filterIn is+++++++++"+filterIn.toString());
                bmobQuery.addWhereContainedIn("kind",filterOut);//筛选支出种类
                //bmobQuery.addWhereContainedIn("kind",filterIn);//筛选收入种类
                bmobQuery.addWhereEqualTo("userId",user.getObjectId()).addWhereEqualTo("deleted",false).findObjects(new FindListener<Record>() {
                    @Override
                    public void done(List<Record> list, BmobException e) {
                        if(e==null)
                        {
                            Message message = new Message();
                            message.obj = list;
                            Log.e("smile","查询的list值是+++++"+list.toString());
                            message.what= 1;
                            handler.sendMessage(message);//将获得的list发送出去
                        }
                    }
                });
            }else if(filterOut.size()==0&&filterIn.size()!=0)
            {
                Log.e("smile","执行到对收入进行筛选了");
                BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                Log.e("smile","ReFresh查询过程中，filterout is++++++++++"+filterOut.toString()+"filterIn is+++++++++"+filterIn.toString());
                //bmobQuery.addWhereContainedIn("kind",filterOut);//筛选支出种类
                bmobQuery.addWhereContainedIn("kind",filterIn);//筛选收入种类
                bmobQuery.addWhereEqualTo("userId",user.getObjectId()).addWhereEqualTo("deleted",false).findObjects(new FindListener<Record>() {
                    @Override
                    public void done(List<Record> list, BmobException e) {
                        if(e==null)
                        {
                            Message message = new Message();
                            message.obj = list;
                            Log.e("smile","查询的list值是+++++"+list.toString());
                            message.what= 1;
                            handler.sendMessage(message);//将获得的list发送出去
                        }
                    }
                });
            }else
            {
                Log.e("smile","执行到无筛选了++++++++");
                BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                Log.e("smile","ReFresh查询过程中，filterout is++++++++++"+filterOut.toString()+"filterIn is+++++++++"+filterIn.toString());
                //bmobQuery.addWhereContainedIn("kind",filterOut);//筛选支出种类
                //bmobQuery.addWhereContainedIn("kind",filterIn);//筛选收入种类
                bmobQuery.addWhereEqualTo("userId",user.getObjectId()).addWhereEqualTo("deleted",false).findObjects(new FindListener<Record>() {
                    @Override
                    public void done(List<Record> list, BmobException e) {
                        if(e==null)
                        {
                            Message message = new Message();
                            message.obj = list;
                            Log.e("smile","查询的list值是+++++"+list.toString());
                            message.what= 1;
                            handler.sendMessage(message);//将获得的list发送出去
                        }
                    }
                });
            }
        }else
        {
            /*************************
             * 日期选择查询
             */
            Date dateStart = null;
            Date dateEnd = null;
            BmobQuery<Record> bmobQuery = new BmobQuery<Record>();
            BmobQuery<Record> startBmobQuery = new BmobQuery<Record>();
            BmobQuery<Record> endBmobQuery = new BmobQuery<Record>();
            List<BmobQuery<Record>> and = new ArrayList<BmobQuery<Record>>();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT+08:00' yyyy", Locale.US);
            try {
                dateStart =sdf.parse(startDate.toString());
                dateEnd = sdf.parse(endDate.toString());
                Log.e("smile","++++++++++++++++++"+dateStart.toString());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            startBmobQuery.addWhereGreaterThanOrEqualTo("updatedAt",new BmobDate(dateStart));
            and.add(startBmobQuery);
            endBmobQuery.addWhereLessThanOrEqualTo("updatedAt",new BmobDate(dateEnd));
            and.add(endBmobQuery);
            bmobQuery.and(and);
            if(filterIn.size()!=0&&filterOut.size()==0)
            {
                Log.e("smile","日期选择执行到对收入进行筛选了");
                bmobQuery.addWhereContainedIn("kind",filterIn);
            }else if (filterIn.size()==0&&filterOut.size()!=0)
            {
                Log.e("smile","日期选择执行到对支出进行筛选了");
                bmobQuery.addWhereContainedIn("kind",filterOut);
            }
            Log.e("smile","日期选择执行到无筛选了");
            bmobQuery.addWhereEqualTo("userId",user.getObjectId()).addWhereEqualTo("deleted",false).findObjects(new FindListener<Record>() {
                @Override
                public void done(List<Record> list, BmobException e) {
                    if(e==null)
                    {
                        Message message = new Message();
                        message.obj = list;
                        Log.e("smile","查询的list值是+++++"+list.toString());
                        message.what= 1;
                        handler.sendMessage(message);//将获得的list发送出去
                    }
                }
            });
        }
        onLoad();
    }

    @Override
    public void onLoadMore() {
        onLoad();
    }
/*********************************************************************************/
    //***************************记录列表的Addapter***********************************/
    class AppAdapter extends BaseSwipListAdapter {

        @Override
        public int getCount() {
                return mAppList.size();
        }

        @Override
        public Record getItem(int position) {
            return mAppList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //*************************************为list item设置值*************************************/
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.item_list_app, null);
                new RecordHolder(convertView);
            }
            RecordHolder holder = (RecordHolder) convertView.getTag();
            Record item = getItem(position);
            //Log.e("smile",item.toString());
            if(item.getType().equals("收入"))
                holder.methodPic.setImageResource(R.drawable.in);
            else holder.methodPic.setImageResource(R.drawable.out);
            holder.number.setText(item.getNumber().toString());
            holder.location.setText(item.getLocation());
            holder.kind.setText(item.getKind());
            String date = item.getUpdatedAt();
            holder.timeText.setText(date);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date sdfDate = sdf.parse(date);
                int hour = sdfDate.getHours();
                if(hour>=6&&hour<=20)
                    holder.dayornight.setImageResource(R.drawable.day);
                else holder.dayornight.setImageResource(R.drawable.night);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //设置币种
            if(item.getCoin().equals("人民币"))
            {
                holder.coin.setText("¥");
            } else if(item.getCoin().equals("欧元"))
            {
                holder.coin.setText("€");
            }else
            {
                holder.coin.setText("$");
            }
            holder.methodText.setText(item.getMethod());
            //Log.e("smile","time++++++++++"+item.getUpdatedAt());
            holder.kind.setText(item.getKind());
           /* holder.dayornight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "iv_icon_click", Toast.LENGTH_SHORT).show();
                }
            });
            holder.number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"iv_name_click",Toast.LENGTH_SHORT).show();
                }
            });*/
            return convertView;
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.dateselect) {
            //初始化日期选择器
            Calendar nextMonth = Calendar.getInstance();
            nextMonth.add(Calendar.MONTH, 1);

            Calendar lastMonth = Calendar.getInstance();
            lastMonth.add(Calendar.MONTH, -12);
            Log.e("smile","点击上了日期选择");
            Calendar today = Calendar.getInstance();
            //初始化日期范围为上一个月
            ArrayList<Date> dates = new ArrayList<Date>();
            today.add(Calendar.DATE, -getCurrentMonthLastDay());
            dates.add(today.getTime());
            today.add(Calendar.DATE, getCurrentMonthLastDay()+1);
            dates.add(today.getTime());
            dialogView = (CalendarPickerView) getLayoutInflater().inflate(R.layout.dateselectdialog, null, false);
            dialogView.init(lastMonth.getTime(), nextMonth.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE) //
                    .withSelectedDates(dates);
            theDialog =
                    new AlertDialog.Builder(MainActivity.this).setTitle("选择日期范围")
                            .setView(dialogView)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startDate = dialogView.getSelectedDates().get(0);
                            endDate = dialogView.getSelectedDates().get(dialogView.getSelectedDates().size()-1);
                            Log.e("smile",startDate.toString());
                            Log.e("smile",endDate.toString());
                            onRefresh();
                        }
                            }).create();//创建日期选择窗口
                    theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Log.d("smile", "onShow: fix the dimens!");
                    dialogView.fixDialogDimens();
                }
            });
            theDialog.show();
            return true;
        }
        if(id==R.id.filter)
        {
            Log.e("smile","进行支出或收入筛选+++");
            final String[] type = {"不限","支出","收入"};//支出和收入和不限
            final ArrayList<String> tempOutkinds = new ArrayList<>();//
            final ArrayList<String> tempInkinds = new ArrayList<>();//用户所拥有的支出和收入类别（ArrayList// ）
            outkinds = user.getOutKinds();
            inkinds = user.getInKinds();
            tempOutkinds.addAll(outkinds);
            tempInkinds.addAll(inkinds);//转化为Array
            typeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,type);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filterMultAdapterIn = new FilterMultAdapter(this, tempInkinds);
            filterMultAdapterOut = new FilterMultAdapter(this,tempOutkinds);//构建适配器
            //typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View filterDialog = layoutInflater.inflate(R.layout.filterview,null);//实现自定义View的Dialog
            Spinner spinner = (Spinner) filterDialog.findViewById(R.id.filtertypespnnier);
            listView= (ListView) filterDialog.findViewById(R.id.filterkindlist);//自定义View中的控件
            spinner.setAdapter(typeAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    final String temp = type[i];
                    Log.e("smile","temp is++++++++"+temp);
                    if(temp.equals("收入"))
                    {
                        state = STATE_IN;//设置标志
                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(filterMultAdapterIn);
                    }else if(temp.equals("支出"))
                    {
                        state = STATE_OUT;
                        listView.setVisibility(View.VISIBLE);
                        listView.setAdapter(filterMultAdapterOut);
                    }else
                    {
                        state = STATE_NONE;
                        listView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    listView.setVisibility(View.GONE);
                }
            });
            final AlertDialog.Builder filterAlertBuilder = new AlertDialog.Builder(MainActivity.this).setTitle("选择类别").setView(filterDialog);
            filterAlertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (state)
                    {
                        case STATE_IN:
                            filterIn.clear();
                            for(int j =0;j<tempInkinds.size();j++)
                        {
                            if (filterMultAdapterIn.getIsSelected().get(j))
                            {
                                Log.e("smile","test +++multyAdapter.get(i)"+filterMultAdapterIn.getItem(j));
                                filterIn.add(filterMultAdapterIn.getItem(j).toString());//初始化收入查询条件
                            }
                        }
                            filterOut.clear();
                            Log.e("smile","filterIn is+++++++++++++++++"+filterIn.toString());
                            break;
                        case STATE_OUT:
                            filterOut.clear();
                            for (int j =0 ;j<tempOutkinds.size();j++)
                        {
                            if (filterMultAdapterOut.getIsSelected().get(j))
                            {
                                Log.e("smile","test +++multyAdapter.get(i)"+filterMultAdapterOut.getItem(j));
                                filterOut.add(filterMultAdapterOut.getItem(j).toString());//初始化支出查询条件
                            }
                        }
                            filterIn.clear();
                            Log.e("smile","filterOut is+++++++++++++++++"+filterOut.toString());
                            break;
                        case STATE_NONE:filterOut = tempOutkinds;
                            filterIn = tempInkinds;
                            break;
                        default:break;
                    }
                    onRefresh();
                }
            });
            filterAlertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            filterAlertBuilder.setCancelable(false);
            filterAlertBuilder.show();
            /*final List<String> kindFilterList = user.getInKinds();
            kindFilterList.addAll(user.getOutKinds());
            String[] tempList = (String[])kindFilterList.toArray();
            boolean[] temp = new boolean[]{};*/
        }

        return super.onOptionsItemSelected(item);
    }

    /*************
     * 获取当月天数
     * @return
     */
    public static int getCurrentMonthLastDay()
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }
}
