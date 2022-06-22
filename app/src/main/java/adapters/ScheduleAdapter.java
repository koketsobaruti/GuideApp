package adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.guideapp.R;

import java.util.List;

import models.ScheduleInfo;

public class ScheduleAdapter extends ArrayAdapter<ScheduleInfo> {
    private static final String TAG = "LOCATION_SCHEDULE_ADAPTER";
    private Context mContext;
    private int mResource;


    public ScheduleAdapter(@NonNull Context context, int resource, List<ScheduleInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ScheduleInfo info = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_cell,parent, false);

        }
        TextView location = (TextView) convertView.findViewById(R.id.locationScheduled);
        TextView description = (TextView) convertView.findViewById(R.id.descriptionScheduled);
        TextView address = (TextView) convertView.findViewById(R.id.addressScheduled);
        Switch alarmState = (Switch) convertView.findViewById(R.id.alarmScheduleSwitch);
        TextView dateSet = (TextView) convertView.findViewById(R.id.dateScheduled);
        TextView timeSet = (TextView) convertView.findViewById(R.id.timeScheduled);
        TextView tenseTxt = (TextView) convertView.findViewById(R.id.txtTense);


        location.setText(info.getLocation());
        description.setText(info.getDescription());
        address.setText(info.getAddress());
        alarmState.setChecked(info.getAlarmState());
        dateSet.setText(info.getDate());
        timeSet.setText(info.getTime());

        boolean tense = info.getTense();
        if(tense){
            //t = "Upcoming";
            tenseTxt.setText("Upcoming");
            tenseTxt.setTextColor(Color.parseColor("#23eb05"));
        }else{
            tenseTxt.setText("Past");
            tenseTxt.setTextColor(Color.parseColor("#ed1d0e"));
        }
        //tenseTxt.setText(t);

        return convertView;
    }
}
