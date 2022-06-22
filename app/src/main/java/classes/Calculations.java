package classes;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Calculations {


    public static int convertToMetricTemp(String t){
        double temp = Double.parseDouble(t);
        double newTemp = (temp - 32) * (0.5556);
        int ti = (int) Math.round(newTemp);
        return ti;
    }

    public static int convertToImpDistance(String d){
        double distance = Double.parseDouble(d);
        double newDistance = distance * 0.00062137119;
        int di = (int) Math.round(newDistance);
        return di;
    }


    public static boolean checkDate(String d){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = getCurrentDate();
        Date dateSet, dateNow;
        boolean state = false;
        try {
            dateSet = dateFormat.parse(d);
            dateNow = dateFormat.parse(currentDate);

            if(dateSet.compareTo(dateNow) > 0){
                state = true;
            }else if(dateSet.compareTo(dateNow) < 0){
                state = false;
            }else if(dateSet.compareTo(dateNow) == 0){
                state = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return state;
    }

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date obj = new Date();
        String time = String.valueOf(dateFormat.format(obj));
        return time;
    }


    public static String getCurrentDate() {
        DateFormat dform = new SimpleDateFormat("dd/MM/yyyy");
        Date obj = new Date();
        String dateToday = dform.format(obj);
        return dateToday;
    }
}
