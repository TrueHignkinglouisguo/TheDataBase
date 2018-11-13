package com.example.louis.guobase;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import Tools.MyActivityManager;
import Tools.StatusBarUtil;
public abstract class BaseActivity extends AppCompatActivity {
    protected Activity activity;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置无ActionBar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarUtil.setStatusBarColor(this, setStatusBarColor());
        // 设置布局
        setContentView(CreatetLayout());
        activity = this;
        MyActivityManager.getInstance().add(this);
        CreateView();
        CreateData();
    }

    /**
     * 初始化布局
     * @return Activity的布局ID
     */
    protected abstract int CreatetLayout();

    /**
     * 设置各个页面的状态栏颜色
     * @return 颜色值
     */
    protected abstract int setStatusBarColor();

    /**
     * 初始化布局
     */
    protected abstract void CreateView();


    /**
     * 初始化数据
     */
    protected abstract void CreateData();

    /**
     * 设置不需要设置状态栏的颜色
     *
     * 是否设置StatusBar的颜色，绝大部分是要设置的，特殊的不需要设置，例如一个Activity中有多个Fragment的
     * Activity，多个Fragment的状态栏颜色可能不相同，那就只好交给Fragment自己去设置。遇到这样的Activity
     * 需要返回false
     */
    protected void notSetStatusBarColor() {
        StatusBarUtil.setStatusBarTranslucent(this);
    }

}
