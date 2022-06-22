package models;

import java.util.List;

public class Favourites {
    public List<String> getFavouritesList() {
        return FavouritesList;
    }

    public void setFavouritesList(List<String> favouritesList) {
        FavouritesList = favouritesList;
    }

    public Favourites(List<String> favouritesList) {
        FavouritesList = favouritesList;
    }

    List<String> FavouritesList;

}
