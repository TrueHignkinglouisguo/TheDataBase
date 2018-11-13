package com.example.louis.guobase;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import Method.TableInfo_Adapter;
import Tools.MyActivityManager;
import Tools.MyToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class TableInfo extends BaseActivity {
    private Activity activity;
    private TextView title;
    private ImageView back;
    private String dbname,tablename;
    private boolean table_isnull=false;
    private int item_num=0;
    private RecyclerView recyclerView;
    private TableInfo_Adapter tableInfo_adapter;
    Handler handler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg){
            if(msg.what==1){
                String[] str=(String[])msg.obj;
                createitem(str);
            }else if(msg.what==2){
                item_num=1;
                table_isnull=true;
                String[] str={"此表为空表!"};
                createitem(str);
            }
            return false;
        }
    });
    protected int  setStatusBarColor(){
        return 0;
    }
    protected int CreatetLayout(){
        return R.layout.tableinfopage;
    }
    protected void CreateView(){
        back=(ImageView) findViewById(R.id.tableinfo_back);
        title=(TextView)findViewById(R.id.tableinfo_title);
        recyclerView = (RecyclerView)findViewById(R.id.tableinfo_text);
    }
    protected void CreateData(){
        activity=this;
        MyActivityManager.getInstance().add(activity);
        Bundle data=getIntent().getExtras();
        dbname=data.getString("dbname");
        tablename=data.getString("tablename");
        title.setText("当前表:"+tablename);
        gettableinfo(tablename,dbname,"select_tableinfo");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyActivityManager.getInstance().destorySpecActivity(activity);
            }
        });
    }
    private List<String> item_text(String[] text) {
        List<String> item_text_data = new ArrayList<String>();
        for (int i = 0; i < item_num; i++) {
            item_text_data.add(text[i]);
        }
        return item_text_data;
    }
    private void createitem(String[] text){
        recyclerView.setLayoutManager(new LinearLayoutManager(TableInfo.this));
            tableInfo_adapter = new TableInfo_Adapter(TableInfo.this,TableInfo.this, item_text(text));
            tableInfo_adapter.setClickListener(new TableInfo_Adapter.ItemClickListener(){
            public void OnItemClick(View view, int position){
                for(int i=0;i<item_num;i++){
                    if(position==i){
                        if(table_isnull==true){
                        }else{
                            TextView textView=(TextView)view.findViewById(R.id.info_rv_text);
                            Intent intent=new Intent(TableInfo.this,MidyTableActivity.class);
                            intent.putExtra("dbname",dbname);
                            intent.putExtra("tablename",tablename);
                            intent.putExtra("info",textView.getText().toString());
                            startActivity(intent);
                        }
                    }
                }
            }
            public void OnItemLongClick(final View view, int position){
                for(int i=0;i<item_num;i++){
                    if(position==i){
                        if(table_isnull==true){
                            MyToast.show(TableInfo.this,"此表为空表!");
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(tableInfo_adapter);
    }
    private void gettableinfo(final String tbname,final String dbname,final String type){
        new Thread(new Runnable(){
            public void run() {
               FormBody.Builder params=new FormBody.Builder();
                params.add("tbname",tbname)
                        .add("dbname",dbname)
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
                        String result = response.body().string();
                        if (result.equals("tb_null")) {
                            Message msg = handler.obtainMessage();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                item_num = Integer.parseInt(jsonObject.optString("总行数:"));
                                String[] text = new String[item_num];
                                for (int i = 0; i < item_num; i++) {
                                    text[i] = jsonObject.optString(String.valueOf(i + 1));
                                }
                                Message msg = handler.obtainMessage();
                                msg.obj = text;
                                msg.what = 1;
                                handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
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
