package com.example.louis.guobase;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import Method.Midy_Table_Adapter;
import Tools.MyActivityManager;
import Tools.MyToast;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MidyTableActivity extends BaseActivity {
    private Activity activity;
    private TextView title;
    private ImageView back,submit;
    private String dbname,tablename,info;
    private String[] infos,infos2;
    private JSONObject jsonObject=new JSONObject();
    private int j=0;
    private RecyclerView recyclerView;
    private Midy_Table_Adapter midy_table_adapter;
    Handler handler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            String str=(String)msg.obj;
            MyToast.show(MidyTableActivity.this,str);
            return false;
        }
    });
    protected int  setStatusBarColor(){
        return 0;
    }
    protected int CreatetLayout(){ return R.layout.midy_table_info; }
    protected void CreateView(){
        back=(ImageView) findViewById(R.id.midy_table_back);
        submit=(ImageView) findViewById(R.id.midy_table_submit);
        title=(TextView)findViewById(R.id.midy_table_title);
        recyclerView=(RecyclerView)findViewById(R.id.midy_table_rv);
    }
    protected void CreateData(){
        activity=this;
        MyActivityManager.getInstance().add(activity);
        Bundle data=getIntent().getExtras();
        dbname=data.getString("dbname");
        tablename=data.getString("tablename");
        info=data.getString("info");
        title.setText("当前表:"+tablename);
        getkey(info);
        final  LinearLayoutManager linearLayoutManager=new LinearLayoutManager(MidyTableActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        midy_table_adapter = new Midy_Table_Adapter(MidyTableActivity.this,MidyTableActivity.this, item_text(),item_text2());
        midy_table_adapter.setClickListener(new Midy_Table_Adapter.ItemClickListener() {
            public void OnItemClick(View view, final int position) {
                final TextView key=view.findViewById(R.id.midy_table_key);
                final TextView value=view.findViewById(R.id.midy_table_value);
                final EditText sql_input=new EditText(MidyTableActivity.this);
                new AlertDialog.Builder(MidyTableActivity.this).setTitle("请输入值:")
                        .setIcon(android.R.drawable.sym_def_app_icon)
                        .setView(sql_input)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialogInterface, int i){
                                String sql=sql_input.getText().toString();
                                if(sql.isEmpty()){
                                    MyToast.show(MidyTableActivity.this,"输入不能为空!");
                                }else{
                                    value.setText(sql);
                                    try{
                                        jsonObject.put(key.getText().toString(),value.getText().toString());
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).setNegativeButton("取消",null).show();
                }
                });
        recyclerView.setAdapter(midy_table_adapter);
        submit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(jsonObject.toString().equals("{}")){
                    MyToast.show(MidyTableActivity.this,"你没有做任何更改!");
                }else{
                    try{
                        jsonObject.put("unmidy",info);
                        jsonObject.put("dbname",dbname);
                        jsonObject.put("tbname",tablename);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    submitinfo(jsonObject.toString());
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyActivityManager.getInstance().destorySpecActivity(activity);
            }
        });
    }
    private List<String> item_text() {
        List<String> item_text_data = new ArrayList<String>();
        for (int i = 0; i < j; i++) {
            item_text_data.add(infos[i]);
        }
        return item_text_data;
    }
    private List<String> item_text2() {
        List<String> item_text_data = new ArrayList<String>();
        for (int i = 0; i < j; i++) {
            item_text_data.add(infos2[i]);
        }
        return item_text_data;
    }
    private void getkey(String str){
        try {
            JSONObject jsonObject = new JSONObject(str);
            Iterator<String> objs =  jsonObject.keys();
            String[] keys=new String[50];
            String[] keys2=new String[50];
            while (objs.hasNext()){
                keys[j] = objs.next();
                keys2[j]=jsonObject.optString(keys[j]);
                j++;
            }
            infos=keys;
            infos2=keys2;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void submitinfo(final String json){
        new Thread(new Runnable() {
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
                Request request = new Request.Builder()
                        .url("https://www.louisguo.cn/guobase/updata.php")
                        .post(requestBody)
                        .build();
                try {
                    Response response=okHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        String result=response.body().string();
                        Message message=handler.obtainMessage();
                        message.obj=result;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
