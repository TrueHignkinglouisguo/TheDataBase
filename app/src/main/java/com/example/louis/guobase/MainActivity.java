package com.example.louis.guobase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import Method.MainActivity_Adapter;
import Tools.MyActivityManager;
import Tools.MyToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ImageView menu,icon;
    private View popwindow;
    private TextView connectmysql,adddatabase,addtable,show;
    private PopupWindow more;
    private Toolbar toolbar;
    private String dbname=null,type,desc_tablename;
    private int item_num=0;
    private boolean connect_succeed=false,database_succeed=false;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private MainActivity_Adapter selectInfo_adapter;
    Handler handler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if(msg.what==1){
                show.setText("已连接到数据库: "+dbname);
                show.setTextColor(Color.GREEN);
                more.dismiss();
                String[] str=(String[])msg.obj;
                createitem(str);
                database_succeed=true;
            }
            if(msg.obj.equals("Success")){
                show.setText("已连接到MySQL");
                show.setTextColor(Color.GREEN);
                connect_succeed=true;
            }else if(msg.obj.equals("db_Error")){
                show.setText("无法连接到此数据库!");
                show.setTextColor(Color.RED);
            }else if(msg.obj.equals("Error")){
                show.setText("连接MySQL失败!");
                show.setTextColor(Color.RED);
            }else if(msg.obj.equals("db_null")){
                show.setText("此数据库为空!");
                show.setTextColor(Color.RED);
                item_num=0;
                String [] strs={""};
                createitem(strs);
                database_succeed=true;
            }else if(msg.obj.equals("create_table_ok")){
                MyToast.show(MainActivity.this,"建表成功!");
                type="connect_db";
                connectdatabase(dbname,type,"","");
            }else if(msg.obj.equals("create_table_error")){
                MyToast.show(MainActivity.this,"建表失败!");
            }else if(msg.obj.equals("drop_table_ok")){
                MyToast.show(MainActivity.this,"成功删除该表!");
                type="connect_db";
                connectdatabase(dbname,type,"","");
            }else if(msg.obj.equals("drop_table_error")){
                MyToast.show(MainActivity.this,"删除失败!");
            }
            return false;
        }
    });
    protected int setStatusBarColor() {
        return 0;
    }
    protected int CreatetLayout(){
        return R.layout.mainactivitypage;
    }
    protected void CreateView(){
        icon=(ImageView)findViewById(R.id.mainactivity_icon);
        menu=(ImageView)findViewById(R.id.mainactivity_menu);
        show=(TextView) findViewById(R.id.mainactivity_show);
        toolbar=(Toolbar)findViewById(R.id.mainactivity_toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.selectinfo_rv);
    }
    protected void CreateData(){
        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popwindow=getLayoutInflater().inflate(R.layout.mainactivity_pop,null);
                more=new PopupWindow(popwindow,350,ViewGroup.LayoutParams.WRAP_CONTENT);
                more.setFocusable(true);
                more.setOutsideTouchable(true);
                more.showAsDropDown(menu,0,50);
                connectmysql=(TextView)popwindow.findViewById(R.id.pop_connectmysql);
                adddatabase=(TextView)popwindow.findViewById(R.id.pop_adddatabase);
                addtable=(TextView)popwindow.findViewById(R.id.pop_addtable);
                connectmysql.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        if(connect_succeed==true){
                            MyToast.show(MainActivity.this,"你已经连接到MySQL!");
                            more.dismiss();
                        }else{
                            type="connect_test";
                            connectdatabase("",type,"","");
                        }
                    }
                });

                adddatabase.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(connect_succeed==false){
                            MyToast.show(MainActivity.this,"请先连接到MySQL!");
                        }else{
                            final EditText daname_input=new EditText(MainActivity.this);
                            new AlertDialog.Builder(MainActivity.this).setTitle("请输入数据库名")
                                    .setIcon(android.R.drawable.sym_def_app_icon)
                                    .setView(daname_input)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i){
                                            dbname=daname_input.getText().toString();
                                            if(dbname.isEmpty()){
                                                MyToast.show(MainActivity.this,"输入不能为空!");
                                            }else{
                                                type="connect_db";
                                                connectdatabase(dbname,type,"","");
                                            }
                                        }
                                    }).setNegativeButton("取消",null).show();
                        }
                    }
                });

                addtable.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(database_succeed==false){
                            MyToast.show(MainActivity.this,"请先连接到一个数据库!");
                        }else{
                            final EditText sql_input=new EditText(MainActivity.this);
                            new AlertDialog.Builder(MainActivity.this).setTitle("请输入建表语句")
                                    .setIcon(android.R.drawable.sym_def_app_icon)
                                    .setView(sql_input)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i){
                                            String sql=sql_input.getText().toString();
                                            if(sql.isEmpty()){
                                                MyToast.show(MainActivity.this,"输入不能为空!");
                                            }else{
                                                type="create_table";
                                                connectdatabase(dbname,type,"",sql);
                                            }
                                        }
                                    }).setNegativeButton("取消",null).show();
                        }
                    }
                });
            }
        });
        drawer = (DrawerLayout)findViewById(R.id.mainpage_nav);
        navigationView = (NavigationView)findViewById(R.id.mainpage_nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawer.openDrawer(navigationView);
            }
        });
    }
    private void createitem(String[] text){
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        selectInfo_adapter = new MainActivity_Adapter(MainActivity.this,MainActivity.this, item_text(text));
        selectInfo_adapter.setClickListener(new MainActivity_Adapter.ItemClickListener(){
            public void OnItemClick(View view, int position){
                for(int i=0;i<item_num;i++){
                    if(position==i){
                        TextView textView=(TextView) view.findViewById(R.id.mainactivity_rv_text);
                        Intent intent=new Intent(MainActivity.this,TableInfo.class);
                        intent.putExtra("dbname",dbname);
                        intent.putExtra("tablename",textView.getText().toString());
                        startActivity(intent);
                    }
                }
            }
            public void OnItemLongClick(final View view, int position){
                for(int i=0;i<item_num;i++){
                    if(position==i){
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("请选择要进行的操作")
                                .setIcon(R.mipmap.icon_me)
                                .setItems(new String[]{"查看表结构", "删除该表"}, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case 0:
                                                TextView textView=(TextView) view.findViewById(R.id.mainactivity_rv_text);
                                                Intent intent=new Intent(MainActivity.this,TableDesc.class);
                                                intent.putExtra("dbname",dbname);
                                                intent.putExtra("tablename",textView.getText().toString());
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                TextView delete_table_name=(TextView) view.findViewById(R.id.mainactivity_rv_text);
                                                type="drop_table";
                                                connectdatabase(dbname,type,delete_table_name.getText().toString(),"");
                                                break;
                                            default:;
                                        }
                                    }
                                })
                                .show();
                    }
                }
            }
        });
        recyclerView.setAdapter(selectInfo_adapter);
    }
    private List<String> item_text(String[] text) {
        List<String> item_text_data = new ArrayList<String>();
        for (int i = 0; i < item_num; i++) {
            item_text_data.add(text[i]);
        }
        return item_text_data;
    }
    public boolean onNavigationItemSelected(MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_scan) {
        } else if (id == R.id.tools) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("请选择要进行的操作")
                    .setIcon(R.mipmap.icon_me)
                    .setItems(new String[]{"MD5工具"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    Intent intent=new Intent(MainActivity.this,MD5Activity.class);
                                    startActivity(intent);
                                    break;
                                default:;
                            }
                        }
                    })
                    .show();
        }else if (id == R.id.nav_more) {
            String url ="https://www.louisguo.cn";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
            startActivity(i);
        }else if (id == R.id.nav_logout) {
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            MyToast.show(MainActivity.this,"注销成功!");
            MyActivityManager.getInstance().destorySpecActivity(activity);
        }
        return true;
    }
    private void connectdatabase(final String dbname,final String type,final String tbname,final String sql){
        new Thread(new Runnable() {
            public void run() {
                FormBody.Builder params=new FormBody.Builder();
                params.add("dbname",dbname)
                        .add("tbname",tbname)
                        .add("sql",sql)
                        .add("type",type);
                OkHttpClient okHttpClient=new OkHttpClient();
                okHttpClient.newBuilder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS).build();
                Request request=new Request.Builder()
                        .url("https://www.louisguo.cn/guobase/connectdatabase.php")
                        .post(params.build())
                        .build();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    public void onResponse(Call arg0, Response response) throws IOException {
                            String result= response.body().string();
                            Message msg = handler.obtainMessage();
                            if(type.equals("connect_test") || type.equals("create_table") || type.equals("drop_table")){
                                msg.obj = result;
                                handler.sendMessage(msg);
                            } else if(type.equals("connect_db")){
                                if(result.equals("db_null")){
                                    msg.obj = result;
                                    handler.sendMessage(msg);
                                }else if(result.equals("db_Error")){
                                    msg.obj = result;
                                    handler.sendMessage(msg);
                                } else {
                                    try {
                                        JSONObject jsonObject=new JSONObject(result);
                                        item_num=Integer.parseInt(jsonObject.optString("num"));
                                        String[] text=new String[item_num];
                                        for(int i=0;i<item_num;i++){
                                            text[i]=jsonObject.optString(String.valueOf(i+1));
                                        }
                                        msg.obj=text;
                                        msg.what =1;
                                        handler.sendMessage(msg);
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                    }
                    public void onFailure(Call arg0, IOException arg1) {
                    }
                });
            }
        }).start();
    }
    }
