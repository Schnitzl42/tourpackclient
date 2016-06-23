package at.sti_innsbruck.tourpackclient.logic.datamodel;


public class MyLocation {

    public double lng;
    public double lat;
    public String name;
    public String address;

    public MyLocation(double lng, double lat, String name, String address) {
        this.lng = lng;
        this.lat = lat;
        this.name = name;
        this.address = address;
    }
}
