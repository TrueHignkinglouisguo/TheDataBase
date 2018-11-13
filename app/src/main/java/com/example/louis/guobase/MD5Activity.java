package com.example.louis.guobase;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import Tools.MD5;
import Tools.MyActivityManager;
import Tools.MyToast;

public class MD5Activity extends BaseActivity {
    private Activity activity;
    private EditText input,show;
    private TextView createmd5;
    private ImageView back;
    protected int  setStatusBarColor(){
        return 0;
    }
    protected int CreatetLayout(){
        return R.layout.md5page;
    }
    protected void CreateView(){
        back=(ImageView) findViewById(R.id.md5_back);
        createmd5=(TextView)findViewById(R.id.md5_createmad5_button);
        input=(EditText)findViewById(R.id.md5_input);
        show=(EditText)findViewById(R.id.md5_show);
    }
    protected void CreateData(){
        activity=this;
        MyActivityManager.getInstance().add(activity);
        createmd5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                final String text=input.getText().toString().trim();
                if(text.isEmpty()){
                    MyToast.show(MD5Activity.this,"输入不能为空!");
                }else{
                    String text_md5=MD5.getmd5(text);
                    show.setText(text_md5);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyActivityManager.getInstance().destorySpecActivity(activity);
            }
        });
    }
}
