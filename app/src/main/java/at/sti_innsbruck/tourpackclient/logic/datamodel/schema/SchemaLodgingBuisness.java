package at.sti_innsbruck.tourpackclient.logic.datamodel.schema;

import com.google.gson.annotations.SerializedName;


public class SchemaLodgingBuisness {

    @SerializedName("@id")
    private String id;
    @SerializedName("@type")
    private String type;
    private String[] makesOffer;
    @SerializedName("http://schema.org/name")
    private String name;
    private String wholeSchema;
    private SchemaLodgingBuisnessDetails details;

    public SchemaLodgingBuisness(String id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public SchemaLodgingBuisness(String id, String type, String[] offers, String name) {
        this.id = id;
        this.type = type;
        this.makesOffer = offers;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getMakesOffer() {
        return makesOffer;
    }

    public void setMakesOffer(String[] makesOffer) {
        this.makesOffer = makesOffer;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWholeSchema() {
        return wholeSchema;
    }

    public void setWholeSchema(String wholeSchema) {
        this.wholeSchema = wholeSchema;
    }

    public SchemaLodgingBuisnessDetails getDetails() {
        return details;
    }

    public void setDetails(SchemaLodgingBuisnessDetails details) {
        this.details = details;
    }
}
