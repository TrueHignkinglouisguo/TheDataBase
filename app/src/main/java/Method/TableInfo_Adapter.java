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
public class TableInfo_Adapter extends RecyclerView.Adapter<TableInfo_Adapter.MyViewHolder> {
    private List<String> item_text;
    private LayoutInflater inflater;
    private TableInfo_Adapter.ItemClickListener ClickListener;
    private Activity activity;
    public TableInfo_Adapter(Activity activity, Context context, List<String> item_text) {
        this.item_text = item_text;
        this.activity=activity;
        inflater = LayoutInflater.from(context);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView text,tilte;
        private TableRow tb;
        public MyViewHolder(View itemView){
            super(itemView);
            tilte=(TextView)itemView.findViewById(R.id.info_rv_title);
            text=(TextView)itemView.findViewById(R.id.info_rv_text);
            tb=(TableRow) itemView.findViewById(R.id.info_rv_tb);
        }
    }

    @Override
    public TableInfo_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TableInfo_Adapter.MyViewHolder holder = new TableInfo_Adapter.MyViewHolder(inflater.inflate(R.layout.info_item, parent, false));
        return holder;
    }

    public void onBindViewHolder(final TableInfo_Adapter.MyViewHolder holder, final int position){
        holder.text.setText(item_text.get(position));
        holder.tilte.setText(String.valueOf(position+1));
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
    public TableInfo_Adapter setClickListener(TableInfo_Adapter.ItemClickListener ClickListener){
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
