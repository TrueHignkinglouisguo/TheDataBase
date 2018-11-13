package Tools;
import android.app.Activity;
import android.widget.Toast;

public class MyToast {
    public static void show(Activity activity, String msg){
        Toast mToast = Toast.makeText(activity, null, Toast.LENGTH_SHORT);
        mToast.setText(msg);
        mToast.show();
    }
}
