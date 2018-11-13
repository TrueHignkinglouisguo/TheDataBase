package com.example.louis.guobase;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import Tools.MyActivityManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class TableDesc extends BaseActivity {
    private Activity activity;
    private TextView title,text;
    private ImageView back;
    Handler handler=new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if(msg.what==1){
                text.setText((String)msg.obj);
            }
            return false;
        }
    });
    protected int  setStatusBarColor(){
        return 0;
    }
    protected int CreatetLayout(){
        return R.layout.tabledescpage;
    }
    protected void CreateView(){
        back=(ImageView) findViewById(R.id.tabledesc_back);
        title=(TextView)findViewById(R.id.tabledesc_title);
        text=(TextView)findViewById(R.id.tabledesc_text);
    }
    protected void CreateData(){
        activity=this;
        MyActivityManager.getInstance().add(activity);
        Bundle data=getIntent().getExtras();
        title.setText("当前表:"+data.getString("tablename"));
        gettableinfo(data.getString("tablename"),data.getString("dbname"));
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyActivityManager.getInstance().destorySpecActivity(activity);
            }
        });
    }
    private void gettableinfo(final String tbname,final String dbname){
        new Thread(new Runnable(){
            public void run() {
                FormBody.Builder params=new FormBody.Builder();
                params.add("tbname",tbname)
                        .add("dbname",dbname)
                        .add("type","desc_table");
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
                        msg.obj=result;
                        msg.what =1;
                        handler.sendMessage(msg);
                    }
                    public void onFailure(Call arg0, IOException arg1) {
                    }
                });
            }
        }).start();
    }
}
