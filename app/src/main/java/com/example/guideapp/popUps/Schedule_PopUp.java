package com.example.guideapp.popUps;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.guideapp.R;
import com.example.guideapp.fragments.History_List;
import com.example.guideapp.fragments.Schedule;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import classes.AlertReceiver;
import classes.Calculations;
import models.LocationInfo;
import models.ScheduleInfo;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class Schedule_PopUp extends DialogFragment {

    GoogleApi mGoogleApiClient;
    Boolean alarmSet = false;
    private TextView locationTxt, addressTxt, dateScheduledTxt, timeScheduledtxt, descriptionTxt;
    //private final String TAG = "SCHEDULE_POP_UP";
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ImageView datePickerImage, timePickerImage, exit;
    private Switch alarmSwitch;
    private Button saveBtn;
    private Boolean alarm;
    private Calendar cal;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String userIUD;
    FirebaseUser currentUser;
    private int tHour, tMinute, d, m,y;
    private ArrayList<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();


    private String locationStr, addressStr, dateStr, timeStr, descriptionStr;
    private long timeInMillies;
    String time, date;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_popup_schedule, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userIUD = currentUser.getUid();
        myRef = database.getReference(userIUD).child("schedules");

        datePickerImage = view.findViewById(R.id.datePickerSchedule);
        dateScheduledTxt = view.findViewById(R.id.dateScheduleTxt);
        timePickerImage = view.findViewById(R.id.timePickerSchedule);
        timeScheduledtxt = view.findViewById(R.id.timeScheduleTxt);
        descriptionTxt = view.findViewById(R.id.descriptionScheduleTxt);

        locationTxt = view.findViewById(R.id.scheduleLocationTxt);
        addressTxt = view.findViewById(R.id.scheduleAddressTxt);
        Places.initialize(getContext(), "AIzaSyA3mnQFSM39ZwEamw9X_yrDk35M5DX87Rg");
        locationTxt.setFocusable(false);

        saveBtn = view.findViewById(R.id.saveScheduleBtn);
        alarmSwitch = view.findViewById(R.id.alarmScheduleSwitch);
        exit = view.findViewById(R.id.exitSchedule);

        //when the user clicks to save the new schedule
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });

        //opens the google autocomplete and
        //assigns the value to the location and address
        locationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fields).build(getContext());
                startActivityForResult(intent,100);

            }
        });

        //opens date dialog when the user clicks on the calendar icon
        datePickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                d = dayOfMonth;
                m = month;
                y = year;
               // Log.d(TAG, "Date scheduled (mm/dd/yyy) : " + dayOfMonth + "/" + month  + "/" + year );

                date = dayOfMonth + "/" + month  + "/" + year;
                boolean state = Calculations.checkDate(date);
                if(!state){
                    Toast.makeText(getContext(),"Please select a valid date.", Toast.LENGTH_SHORT).show();
                }else{
                    dateScheduledTxt.setText(date);
                }
            }
        };

        //opens time dialog when the user clicks on the time icon
        timePickerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inititalize timepicker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //initialize hour and minute
                                tHour = hourOfDay;
                                tMinute = minute;

                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);

                                //String timeNow=getCurrentTime();
                                //String dateNow=getCurrentDate();

                                //check whether it's upcoming or past
                                //if(timeStr)
                                time = tHour + ":" + tMinute;
                                //initialize calendar
                                Calendar calendar = Calendar.getInstance();
                                //set hour and minute
                                timeInMillies = calendar.getTimeInMillis();
                                //Set selected time on textView
                                //DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                                //String time = dateFormat.format(calendar);
                                timeScheduledtxt.setText(time);
                            }
                        },12,0,false
                );
                timePickerDialog.updateTime(tHour, tMinute);
                timePickerDialog.show();
            }
        });

        alarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((alarmSwitch.isChecked()) && (alarmSet == false)){
                    setAlarm();
                }else if((alarmSet == true) && (!alarmSwitch.isChecked())){
                    cancelAlarm();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        alarmSet = false;
        Toast.makeText(getContext(),"Alarm cancelled.", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillies, pendingIntent);
        alarmSet = true;
        Toast.makeText(getContext(),"Alarm has been set.", Toast.LENGTH_SHORT).show();
        /*if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }*/
        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void validateInput() {
        locationStr = locationTxt.getText().toString();
        descriptionStr = descriptionTxt.getText().toString();
        addressStr = addressTxt.getText().toString();
        dateStr = dateScheduledTxt.getText().toString();
        timeStr = timeScheduledtxt.getText().toString();

        //String tense = "Past";
        boolean mode = true;
        /*if(mode){
            tense = "Upcoming";
        }*/


        if(locationStr.equals("") || addressStr.equals("") || dateStr.equals("") ||
            timeStr.equals("")){
            Toast.makeText(getContext(),"Enter the missing information.",Toast.LENGTH_LONG).show();
        }else{
            ScheduleInfo info = new ScheduleInfo(locationStr,addressStr,descriptionStr,dateStr,timeStr,alarmSet,mode);
            myRef.push().setValue(info);
            dismiss();
            //FragmentTransaction ftran = getActivity().getFragmentManager().beginTransaction();
            Schedule schedule = new Schedule();
           // schedule.get
            //getChildFragmentManager().
            //getChildFragmentManager().beginTransaction().replace(R.id.navigationFragment, schedule).commit();

            //NavigationActivity.fragmentManager.beginTransaction().replace(R.id.navigationFragment,new Schedule(),null).commit();

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationTxt.setText(place.getName());
            addressTxt.setText(place.getAddress());
            // Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            status.getStatusMessage();
            //Log.i("Error", status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
        return;
    }


}
