package models;

public class BookmarkInfo extends LocationInfo{

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

    public BookmarkInfo(){
    }

    String date;
    String time;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public BookmarkInfo(String location, String address, String description, String date, String time, String key) {
        super(location, address, description);
        this.date = date;
        this.time = time;
        this.key = key;
    }

    String key;
}
