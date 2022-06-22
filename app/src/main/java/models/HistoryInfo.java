package models;

public class HistoryInfo extends LocationInfo{

    String OpeningHours;
    String Rating;
    String Latitude;
    String Longitude;
    String LocationType;
    String PlaceId;

    public HistoryInfo(String location, String address, String description, String openingHours, String rating, String latitude, String longitude, String locationType) {
        super(location, address, description);
        OpeningHours = openingHours;
        Rating = rating;
        Latitude = latitude;
        Longitude = longitude;
        LocationType = locationType;
    }
    public HistoryInfo(){}

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }



    public String getOpeningHours() {
        return OpeningHours;
    }

    public void setOpeningHours(String openingHours) {
        OpeningHours = openingHours;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }


    public String getLocationType() {
        return LocationType;
    }

    public void setLocationType(String locationType) {
        LocationType = locationType;
    }
}
