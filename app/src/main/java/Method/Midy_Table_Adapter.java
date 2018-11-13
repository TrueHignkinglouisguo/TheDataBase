package Method;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.louis.guobase.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
public class Midy_Table_Adapter extends RecyclerView.Adapter<Midy_Table_Adapter.MyViewHolder> {
   private JSONObject jsonObject;
    private List<String> item_keys;
    private List<String> item_values;
    private LayoutInflater inflater;
    private Activity activity;
    private Context mContext;
    private Midy_Table_Adapter.ItemClickListener ClickListener;
    public Midy_Table_Adapter(Activity activity, Context context, List<String> item_keys,List<String> item_values) {
        this.item_keys= item_keys;
        this.item_values= item_values;
        this.activity=activity;
        this.mContext=context;
        inflater = LayoutInflater.from(context);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView key;
        private TextView value;
        private TableRow tb;
        public MyViewHolder(View itemView){
            super(itemView);
            key=(TextView)itemView.findViewById(R.id.midy_table_key);
            value=(TextView) itemView.findViewById(R.id.midy_table_value);
            tb=(TableRow)itemView.findViewById(R.id.midy_table_tb);
        }
    }

    @Override
    public Midy_Table_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Midy_Table_Adapter.MyViewHolder holder = new Midy_Table_Adapter.MyViewHolder(inflater.inflate(R.layout.midy_table_item, parent, false));
        return holder;
    }

    public void onBindViewHolder(final Midy_Table_Adapter.MyViewHolder holder, final int position) {
        holder.key.setText(item_keys.get(position));
        holder.value.setText(item_values.get(position));
        holder.tb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ClickListener.OnItemClick(view,position);
            }
        });

    }
        public int getItemCount() {
        return item_keys.size();
    }
    public Midy_Table_Adapter setClickListener(Midy_Table_Adapter.ItemClickListener ClickListener){
        this.ClickListener = ClickListener;
        return this;
    }
    public interface ItemClickListener{
        //声明接口ItemClickListener
        void OnItemClick(View view, int position);
    }
}
