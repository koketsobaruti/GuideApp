package models;

public class ScheduleInfo extends LocationInfo {



    public Boolean getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(Boolean alarmState) {
        this.alarmState = alarmState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getTense() {
        return tense;
    }

    public void setTense(Boolean tense) {
        this.tense = tense;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ScheduleInfo(){}


    public ScheduleInfo(String location, String address, String description, String date, String time, Boolean alarmState, Boolean tense) {
        super(location, address, description);
        this.date = date;
        this.time = time;
        this.alarmState = alarmState;
        this.tense = tense;
    }

    String date;
    String time;
    Boolean alarmState;
    Boolean tense;
    String key;
}
