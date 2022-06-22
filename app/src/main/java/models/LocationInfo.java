package models;

public class LocationInfo {
    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


    public LocationInfo() {

    }

    public LocationInfo(String location, String address, String description) {
        Location = location;
        Address = address;
        Description = description;
    }


    String Location;
    String Address;
    String Description;

}
