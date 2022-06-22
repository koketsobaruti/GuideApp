package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.guideapp.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import models.PlaceInfo;

public class SuggestionsAdapter extends ArrayAdapter<String> {
    private static final String TAG = "LOCATION_SCHEDULE_ADAPTER";
    private Context mContext;
    private int mResource;


    public SuggestionsAdapter(@NonNull Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String info = getItem(position);

        //initialise view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.suggestions_cell, parent, false);
        }

        TextView type = (TextView) convertView.findViewById(R.id.favouriteTypeTxt);

        type.setText(info);

        return convertView;
    }
}
