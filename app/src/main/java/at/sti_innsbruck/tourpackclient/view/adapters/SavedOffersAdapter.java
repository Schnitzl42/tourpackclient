package at.sti_innsbruck.tourpackclient.view.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import at.sti_innsbruck.tourpackclient.R;
import at.sti_innsbruck.tourpackclient.logic.database.DBHelper;
import at.sti_innsbruck.tourpackclient.logic.greendao.DaoSession;
import at.sti_innsbruck.tourpackclient.logic.greendao.ExtraProperties;
import at.sti_innsbruck.tourpackclient.logic.greendao.ExtraPropertiesDao;
import at.sti_innsbruck.tourpackclient.logic.greendao.Thing;
import at.sti_innsbruck.tourpackclient.logic.greendao.ThingDao;
import at.sti_innsbruck.tourpackclient.view.events.ThingRemovedEvent;


public class SavedOffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Map<String, JSONObject> resultsMap;
    private List<String> objectIds;

    private Context context;
    public static final int VIEW_TYPE_HOTEL = 1, VIEW_TYPE_OFFER = 2;




    public class ViewHoderDynamic extends RecyclerView.ViewHolder{

        ImageView imageViewBookmark;
        LinearLayout root;

        public ViewHoderDynamic(View itemView) {
            super(itemView);
            imageViewBookmark = (ImageView) itemView.findViewById(R.id.imageViewBookmark);
            root = (LinearLayout) itemView.findViewById(R.id.linearLayout);

            imageViewBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show dialog if you want to unsafe the offer
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Offer")
                            .setMessage("Are you sure you want to delete the offer?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    int pos = getAdapterPosition();
                                    //remove offer from internal db
                                    String thingId = objectIds.get(pos);

                                    DBHelper helper = new DBHelper();
                                    DaoSession sesison = helper.getSession(context, false);
                                    Thing t = sesison.getThingDao().queryBuilder()
                                            .where(ThingDao.Properties.Thing_id.eq(thingId)).build().unique();
                                    sesison.delete(t);
                                    //delete properties of t
                                    List<ExtraProperties> props = sesison.getExtraPropertiesDao()
                                            .queryBuilder().where(ExtraPropertiesDao
                                                    .Properties.Thing_id.eq(thingId)).build().list();
                                    for(ExtraProperties p : props){
                                        sesison.delete(p);
                                    }
                                    helper.closeDB();
                                    //update gui
                                    objectIds.remove(pos);
                                    resultsMap.remove(t);
                                    notifyItemRemoved(pos);
                                    //send notifcation to other fragment
                                    EventBus.getDefault().post(new ThingRemovedEvent(thingId));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                    //remove or keep offer
                }
            });
            imageViewBookmark.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.ic_bookmark_orange_24dp));
        }
    }

    public void saveNewThing(Thing t) {
        if(!objectIds.contains(t.getThing_id())) {
            objectIds.add(t.getThing_id());
            try {
                resultsMap.put(t.getThing_id(), new JSONObject(t.getContent()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }
    }


    public void displayNewResults(Map<String, JSONObject> parts, List<String> objectIds) {
        this.resultsMap.clear();
        this.resultsMap = parts;
        this.objectIds.clear();
        this.objectIds = objectIds;
        notifyDataSetChanged();
    }


    public SavedOffersAdapter(Context context, Map<String, JSONObject> objectMap, List<String> objectIds){
        this.context = context;
        this.objectIds = objectIds;
        resultsMap = objectMap;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = null;
        //inflate dynamic layout for fields
        cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_with_linear_layout, parent, false);
        ViewHoderDynamic vh = new ViewHoderDynamic(cardView);
        return vh;


        /*switch (viewType){
            case VIEW_TYPE_HOTEL:
                cardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout .card_lodging_preview, parent, false);
                return new ViewHoderLodgingBuisness(cardView);

            case VIEW_TYPE_OFFER:
                cardView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_offer_preview, parent, false);
                return new ViewHoderOffer(cardView);
        }

        return null;*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        LinearLayout root = ((ViewHoderDynamic) holder).root;
        View bottomView = null;
        Set<String> duplicateKeys = new HashSet<>();
        //create dynamic content
        //clear first
        if(root.getChildCount()>0){
            root.removeAllViews();
        }
        //Create new content
        String objectID = objectIds.get(position);
        JSONObject jsonObject = resultsMap.get(objectID);
        parseJson(root, bottomView, duplicateKeys, jsonObject, objectID);

        ((ViewHoderDynamic) holder).imageViewBookmark.setImageDrawable(context.getResources()
                .getDrawable(R.drawable.ic_bookmark_orange_24dp));

        /*
        HotelOrOfferWrapper hotelOrOfferWrapper = offers.get(position);

        if(hotelOrOfferWrapper.isHotel()){
            SchemaLodgingBuisness hotel = hotelOrOfferWrapper.getHotel();
            ((ViewHoderLodgingBuisness)holder).textViewOfferTitle.setText(hotel.getName());


        }else{
            //TODO display offer
        }
        */
    }

    private void parseJson(LinearLayout root, View bottomView, Set<String> duplicateKeys, JSONObject jsonObject, String objectID) {
        if(jsonObject!=null) {
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                //skip "meta"-attributes
                if (!key.startsWith("@")) {
                    Log.d("key", key + "");
                    try {
                        final String value = jsonObject.getString(key);

                        //directly linked image
                        if (key.contains("image") && value.contains("http:")) {
                            //create image view
                            insertImageView(root, value);

                            //get geo coordinates
                        } else if (key.contains("geo")) {
                            bottomView = insertGeoButton(value, objectID);


                        } else if (key.contains("/name")) {
                            //name always on top
                            String name = value;
                            //parse multi-lang value
                            //extractNestedTextView();
                            if (value.trim().startsWith("[")) {
                                JSONArray values = new JSONArray(value);
                                //TODO decide based on device lang
                                JSONObject langObj = values.getJSONObject(0);
                                name = langObj.getString("@value");

                            } else if (value.trim().startsWith("{")) {
                                JSONObject langObj = new JSONObject(value);
                                name = langObj.getString("@value");
                            }

                            TextView textView = new TextView(context);
                            textView.setTextSize(20);
                            textView.setText(name);
                            root.addView(textView, 0);

                        } else if (key.endsWith("phone")) {
                            //show dial option
                            TextView textView = new TextView(context);
                            textView.setText("phone: " + value);
                            textView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent dial = new Intent(Intent.ACTION_DIAL);
                                    dial.setData(Uri.parse(value));
                                    context.startActivity(dial);
                                }
                            });
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            root.addView(textView);

                        } else {
                            //parse as text
                            //parsing logic
                            //1. create human readable key names
                            String hrKey = key;
                            if (key.startsWith("http")) {
                                //get string after last / of url
                                hrKey = key.substring(key.lastIndexOf("/") + 1);
                            }

                            //reached when image is a block _:b3
                            if (hrKey.contains("image")) {
                                //get image from block
                                JSONObject imageJson = getJsonObjectProperty(value, objectID);
                                String url = imageJson.getString("http://schema.org/contentUrl");

                                insertImageView(root, url);
                            }


                            //ensure vaule is a string
                            if (value.startsWith("_")) {
                                //parse + display sub part (geo is handled above)
                                //or http for buy action
                                //TODO

                            } else if (value.contains("http:")) {
                                //create clickable link
                                TextView textView = new TextView(context);
                                textView.setText(value);
                                textView.setAutoLinkMask(Linkify.WEB_URLS);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browser = new Intent(Intent.ACTION_VIEW);
                                        browser.setData(Uri.parse(value));
                                        context.startActivity(browser);
                                    }
                                });
                                textView.setMovementMethod(LinkMovementMethod.getInstance());
                                root.addView(textView);

                                //some text
                            } else if (value.length() > 0 && !duplicateKeys.contains(hrKey) &&
                                    //no arrays - ugly
                                    !value.contains("[")) {
                                duplicateKeys.add(hrKey);
                                //don´t show empty values
                                insertSimpleTextView(root, hrKey, value);

                            } else if ((value.contains("@") || value.contains("[")) &&
                                    !duplicateKeys.contains(hrKey)) {
                                duplicateKeys.add(hrKey);

                                Log.d("value", "got nested: " + value);
                                //parse multi-lang value
                                //extractNestedTextView();

                                if (value.trim().startsWith("[")) {
                                    JSONArray values = new JSONArray(value);
                                    //TODO decide based on device lang
                                    if(values.get(0) instanceof JSONObject) {
                                        JSONObject langObj = values.getJSONObject(0);
                                        String text = langObj.getString("@value");

                                        insertSimpleTextView(root, hrKey, text);
                                    }else{
                                        String text = values.getString(0);
                                        insertSimpleTextView(root, hrKey, text);
                                    }
                                } else if (value.trim().startsWith("{")) {
                                    JSONObject langObj = new JSONObject(value);
                                    String text = langObj.getString("@value");

                                    insertSimpleTextView(root, hrKey, text);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            //set bottom view /name
            if (bottomView != null) {
                root.addView(bottomView);
            }
        }
    }

    @NonNull
    private View insertGeoButton(String value, String objectID) throws JSONException {
        //display button to open google maps
        View bottomView = null;
        JSONObject geoJson = getJsonObjectProperty(value, objectID);

       if(geoJson!=null) {
           final double lat = geoJson.getDouble("http://schema.org/latitude");
           final double lon = geoJson.getDouble("http://schema.org/longitude");
           Log.d("coords", "lat: " + lat + " lon: " + lon);
           //show button to open maps
           Button button = new Button(context);
           button.setText("Show on maps");
           LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                   LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
           button.setLayoutParams(layout);
           button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String uri = String.format(Locale.ROOT, "geo:%f,%f", lon, lat);
                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                   context.startActivity(intent);
               }
           });
           //add button at end of layout
           bottomView = button;
       }
        return bottomView;
    }

    @Nullable
    private JSONObject getJsonObjectProperty(String propertyId, String objectID) throws JSONException {
        DBHelper dbHelper = new DBHelper();
        DaoSession session = dbHelper.getSession(context, false);
        List<ExtraProperties> prperties = session.getExtraPropertiesDao().queryBuilder()
                .where(ExtraPropertiesDao.Properties.Thing_id.eq(objectID)).build().list();

        JSONObject geoJson = null;
        for(ExtraProperties property : prperties){
            if(property.getProperty_id().equals(propertyId)){
                geoJson = new JSONObject(property.getContent());
                break;
            }
        }
        return geoJson;
    }

    private void insertSimpleTextView(LinearLayout root, String hrKey, String text) {
        TextView textView = new TextView(context);
        if (hrKey.contains("descr")) {
            //descr requires no descr...
            textView.setText(text);
        } else {
            textView.setText(hrKey + ": " + text);
        }
        root.addView(textView);
    }

    private void insertImageView(LinearLayout root, String url) {
        ImageView imageView = new ImageView(context);

        int heightInDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                200, context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, heightInDP);
        imageView.setLayoutParams(layout);
        //load image
        Picasso.with(context).load(url).resize(1000, 700).centerInside()
                .onlyScaleDown().into(imageView);
        root.addView(imageView);
    }



    @Override
    public int getItemCount() {
        return objectIds.size();
    }

    /*
    @Override
    public int getItemViewType(int position) {
        if(offers.get(position).isHotel()){
            return VIEW_TYPE_HOTEL;
        }else {
            return VIEW_TYPE_OFFER;
        }
    }*/
}