package Method;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.louis.guobase.R;
import java.util.List;
public class MainActivity_Adapter extends RecyclerView.Adapter<MainActivity_Adapter.MyViewHolder> {
    private List<String> item_text;
    private LayoutInflater inflater;
    private MainActivity_Adapter.ItemClickListener ClickListener;
    private Activity activity;
    public MainActivity_Adapter(Activity activity, Context context, List<String> item_text) {
        this.item_text = item_text;
        this.activity=activity;
        inflater = LayoutInflater.from(context);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView text;
        private TableRow tb;
        public MyViewHolder(View itemView){
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.mainactivity_rv_text);
            tb=(TableRow) itemView.findViewById(R.id.mainactivity_rv_tb);
        }
    }

    @Override
    public MainActivity_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MainActivity_Adapter.MyViewHolder holder = new MainActivity_Adapter.MyViewHolder(inflater.inflate(R.layout.mainactivity_item, parent, false));
        return holder;
    }

    public void onBindViewHolder(final MainActivity_Adapter.MyViewHolder holder, final int position) {
        holder.text.setText(item_text.get(position));
        holder.tb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClickListener.OnItemClick(view,position);
            }
        });
        holder.tb.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View view) {
                ClickListener.OnItemLongClick(view,position);
                return true;
            }
        });
    }
    public MainActivity_Adapter setClickListener(MainActivity_Adapter.ItemClickListener ClickListener){
        this.ClickListener = ClickListener;
        return this;
    }
    @Override
    public int getItemCount() {
        return item_text.size();
    }
    public interface ItemClickListener{
        //声明接口ItemClickListener
        void OnItemClick(View view, int position);
        void OnItemLongClick(View view, int position);
    }

}
