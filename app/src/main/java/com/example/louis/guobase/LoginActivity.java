package com.example.louis.guobase;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import Method.JellyInterpolator;
import Tools.MD5;
import Tools.MyActivityManager;
import Tools.MyToast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class LoginActivity extends BaseActivity{
    private Activity activity;
    private EditText input_username,input_password;
    private TextView login_loginbutton;
    private View login_progress,login_input;
    private Message msg;
    Handler handler = new Handler(new Handler.Callback(){
        public boolean handleMessage(Message message){
            String[] result=(String[])message.obj;
            if(result[0].equals("true")){
                MyToast.show(LoginActivity.this,"登录成功!");
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                MyActivityManager.getInstance().destorySpecActivity(activity);
            }else if(result[1].equals("true")){
                view_show();
                MyToast.show(LoginActivity.this,"账号不存在!");
            }else if(result[2].equals("true")){
                view_show();
                MyToast.show(LoginActivity.this,"密码错误!");
            }
            return false;
        }
    });
    protected int setStatusBarColor() {
        return 0;
    }
    protected int CreatetLayout(){
        return R.layout.loginpage;
    }
    protected void CreateView(){
        login_loginbutton=(TextView)findViewById(R.id.login_loginbutton);
        login_progress=(View)findViewById(R.id.login_progress);
        login_input=(View)findViewById(R.id.login_input);
        input_username=(EditText)login_input.findViewById(R.id.login_username);
        input_password=(EditText)login_input.findViewById(R.id.login_password);
    }
    protected void CreateData(){
        activity=this;
        MyActivityManager.getInstance().add(activity);
        login_loginbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String username=input_username.getText().toString().trim();
                final String username_md5= MD5.getmd5(username);
                final String password=input_password.getText().toString().trim();
                final String password_md5=MD5.getmd5(password);
                if(username.isEmpty() || password.isEmpty()){
                    MyToast.show(LoginActivity.this,"输入不能为空!");
                }else{
                    login_input.setVisibility(View.INVISIBLE);
                    login_progress.setVisibility(View.VISIBLE);
                    progressAnimator(login_progress);
                    login_loginbutton.setClickable(false);
                    TimerTask task = new TimerTask() {
                        public void run() {
                            new Thread(new Runnable() {
                                public void run() {
                                    FormBody.Builder params=new FormBody.Builder();
                                    params.add("username",username_md5)
                                            .add("password",password_md5)
                                            .add("type","login");
                                    OkHttpClient okHttpClient=new OkHttpClient();
                                    okHttpClient.newBuilder()
                                            .connectTimeout(5, TimeUnit.SECONDS)
                                            .readTimeout(5, TimeUnit.SECONDS).build();
                                    Request request=new Request.Builder()
                                            .url("https://www.louisguo.cn/check_class/userinfo.php")
                                            .post(params.build())
                                            .build();
                                    Call call=okHttpClient.newCall(request);
                                    call.enqueue(new Callback() {
                                        public void onResponse(Call arg0, Response response) throws IOException {
                                            //   响应成功  response.body().string() 获取字符串数据，当然还可以获取其他
                                            final String result= response.body().string();
                                            try {
                                                JSONObject jsonObject = new JSONObject(result);
                                                String login_ok = jsonObject.optString("login_ok");
                                                String name = jsonObject.optString("name");
                                                String username_error = jsonObject.optString("username_error");
                                                String password_error = jsonObject.optString("password_error");
                                                final String[] results = {login_ok, username_error, password_error,name};
                                                msg = handler.obtainMessage();
                                                msg.obj = results;
                                                handler.sendMessage(msg);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        public void onFailure(Call arg0, IOException arg1) {
                                        }
                                    });
                                }
                            }).start();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 1000);//1秒后执行TimeTask的run方法
                }
            }
        });
    }
    private void view_show(){
        login_progress.setVisibility(View.INVISIBLE);
        login_input.setVisibility(View.VISIBLE);
        login_loginbutton.setClickable(true);

    }
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view, animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
    }
}
