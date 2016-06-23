package at.sti_innsbruck.tourpackclient.view.events;

import com.google.android.gms.maps.model.LatLng;


public class PlaceSelectedEvent {
    public String name;
    public String address;
    public String attributtions;
    public LatLng coords;


    public PlaceSelectedEvent(String name, String address, String attributtions, LatLng coords) {
        this.name = name;
        this.address = address;
        this.attributtions = attributtions;
        this.coords = coords;
    }
}
