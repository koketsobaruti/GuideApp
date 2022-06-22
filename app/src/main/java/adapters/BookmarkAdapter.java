package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.guideapp.R;

import java.util.List;

import models.BookmarkInfo;

public class BookmarkAdapter extends ArrayAdapter<BookmarkInfo> {
    private static final String TAG = "LOCATION_SCHEDULE_ADAPTER";
    private Context mContext;
    private int mResource;


    public BookmarkAdapter(Context context, int resource, List<BookmarkInfo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BookmarkInfo info = getItem(position);

        //initialise view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_cell,parent, false);

        }

        TextView location = (TextView) convertView.findViewById(R.id.locationBookmarked);
        TextView description = (TextView) convertView.findViewById(R.id.descriptionBookmarked);
        TextView address = (TextView) convertView.findViewById(R.id.addressBookmarked);
        TextView dateSet = (TextView) convertView.findViewById(R.id.dateBookmarked);
        TextView timeSet = (TextView) convertView.findViewById(R.id.timeBookmarked);

        location.setText(info.getLocation());
        description.setText(info.getDescription());
        address.setText(info.getAddress());
        dateSet.setText(info.getDate());
        timeSet.setText(info.getTime());


        

        return convertView;
    }
}

