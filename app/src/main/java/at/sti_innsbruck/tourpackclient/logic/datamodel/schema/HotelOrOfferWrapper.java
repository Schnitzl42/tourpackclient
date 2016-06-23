package at.sti_innsbruck.tourpackclient.logic.datamodel.schema;

public class HotelOrOfferWrapper {

    private Object hotelOrOffer;
    private boolean isHotel;

    public HotelOrOfferWrapper(SchemaLodgingBuisness hotel){
        this.hotelOrOffer =  hotel;
        isHotel = true;
    }

    public HotelOrOfferWrapper(SchemaOffer offer){
        this.hotelOrOffer = offer;
        isHotel = false;
    }

    public boolean isHotel() {
        return isHotel;
    }

    public Object getHotelOrOffer() {
        return hotelOrOffer;
    }

    public SchemaLodgingBuisness getHotel(){
        if(isHotel){
            return (SchemaLodgingBuisness) hotelOrOffer;
        }else{
            return null;
        }
    }

    public SchemaOffer getOffer(){
        if(!isHotel){
            return (SchemaOffer) hotelOrOffer;
        }else{
            return null;
        }
    }
}
