package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guideapp.R;

import java.util.List;

import models.HistoryInfo;

public class HistoryRecycleView extends RecyclerView.Adapter<HistoryRecycleView.ViewHolder> {

    private List<HistoryInfo> info;
    private Context context;

    public HistoryRecycleView(List<HistoryInfo> info, Context context){
        this.info = info;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_cell,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryInfo hist = info.get(position);

        holder.location.setText(hist.getLocation());
        holder.description.setText(hist.getDescription());
        holder.address.setText(hist.getAddress());
        holder.openState.setText(hist.getOpeningHours());
        holder.r.setText(hist.getRating());

    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView location;
        public TextView description;
        public TextView address ;
        public TextView openState;
        public TextView r;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            location = (TextView) itemView.findViewById(R.id.locationHistory);
            description = (TextView) itemView.findViewById(R.id.descriptionHistory);
            address = (TextView) itemView.findViewById(R.id.addressHistory);
            openState = (TextView) itemView.findViewById(R.id.txtOpenHistory);
            r = (TextView) itemView.findViewById(R.id.txtRatingHistory);


        }
    }
}
