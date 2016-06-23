package at.sti_innsbruck.tourpackclient.logic.datamodel.schema;

import com.google.gson.annotations.SerializedName;

/**
 *
 *
 *
 * offer has (always) a BuyAction.
 */
public class SchemaOffer {

    @SerializedName("http://schema.org/price")
    double price;
    @SerializedName("price:Currency")
    String currency;
    @SerializedName("http://schema.org/valueAddedTaxIncluded")
    boolean taxIncluded;

    @SerializedName("BuyAction")
    String buyLink;

    private String wholeSchema;
}
