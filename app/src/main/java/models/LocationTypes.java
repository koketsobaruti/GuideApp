package models;

import java.util.ArrayList;
import java.util.List;

public class LocationTypes {
    List<String> typesList = new ArrayList<String>();

    public List<String> getTypesList() {
        return typesList;
    }

    public void setTypesList(List<String> typesList) {
        this.typesList = typesList;
    }

    public LocationTypes(List<String> typesList) {
        this.typesList = typesList;
    }
}
