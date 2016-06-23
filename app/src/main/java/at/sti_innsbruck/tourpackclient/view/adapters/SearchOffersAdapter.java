package at.sti_innsbruck.tourpackclient.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

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
import at.sti_innsbruck.tourpackclient.view.events.ThingSavedEvent;

public class SearchOffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
                    Log.d("bookmark", "bookmarked!");
                    //TODO save + change color
                    String thingId = objectIds.get(getAdapterPosition());
                    Thing thing = new Thing();
                    thing.setThing_id(thingId);
                    thing.setContent(resultsMap.get(thingId).toString());
                    //save new thing
                    DBHelper helper = new DBHelper();
                    DaoSession sesison = helper.getSession(context, false);

                    sesison.insertOrReplace(thing);
                    //parse thing for blocks

                    saveBlocks(thing, thingId, sesison, resultsMap.get(thingId));

                    helper.closeDB();

                    //change color
                    imageViewBookmark.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_bookmark_orange_24dp));
                    notifyItemChanged(getAdapterPosition());

                    //notify new saved with event bus
                    EventBus.getDefault().post(new ThingSavedEvent(thing));
                    Toast.makeText(context, "Saved offer!", Toast.LENGTH_LONG).show();

                }
            });
        }
    }


    public void thingRemoved(String thingId) {
        notifyDataSetChanged();
    }


    public class ViewHoderLodgingBuisness extends RecyclerView.ViewHolder{

        TextView textViewOfferTitle;
        ImageView imageViewSaveOffer;
        ImageView imageViewOfferImage;
        TextView textViewOfferDescription;

        public ViewHoderLodgingBuisness(View itemView) {
            super(itemView);
            textViewOfferTitle = (TextView) itemView.findViewById(R.id.textViewOfferTitle);
            imageViewSaveOffer = (ImageView) itemView.findViewById(R.id.imageViewSaveOffer);
            imageViewOfferImage = (ImageView) itemView.findViewById(R.id.imageViewOfferImage);
            textViewOfferDescription = (TextView) itemView.findViewById(R.id.textViewOfferDescription);



        }
    }
/*
    public class ViewHoderOffer extends RecyclerView.ViewHolder{

        TextView textViewOfferTitle;
        ImageView imageViewSaveOffer;
        ImageView imageViewOfferImage;
        TextView textViewOfferDescription;

        public ViewHoderOffer(View itemView) {
            super(itemView);
            textViewOfferTitle = (TextView) itemView.findViewById(R.id.textViewOfferTitle);
            imageViewSaveOffer = (ImageView) itemView.findViewById(R.id.imageViewSaveOffer);
            imageViewOfferImage = (ImageView) itemView.findViewById(R.id.imageViewOfferImage);
            textViewOfferDescription = (TextView) itemView.findViewById(R.id.textViewOfferDescription);

            //listeners
            imageViewSaveOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HotelOrOfferWrapper offer = offers.get(getAdapterPosition());
                    //save offer in db
                    DBHelper helper = new DBHelper();
                    DaoSession session = helper.getSession(context, false);
                    session.insertOrReplace(offer);
                    //notify new saved with event bus
                    //EventBus.getDefault().post(new SavedOfferEvent(offer));
                    Toast.makeText(context, "Saved offer!", Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    public void displaySearchResults(List<HotelOrOfferWrapper> offers) {
        Log.d("searchAdapter", "displaying search results");
        int oldSize = offers.size();
        this.offers.clear();
        notifyItemRangeRemoved(0, oldSize);
        this.offers.addAll(offers);
        notifyItemRangeInserted(0, offers.size());
    }
*/


    public void displayNewResults(Map<String, JSONObject> parts, List<String> objectIds) {
        this.resultsMap.clear();
        this.resultsMap = parts;
        this.objectIds.clear();
        this.objectIds = objectIds;
        notifyDataSetChanged();
    }


    public SearchOffersAdapter(Context context){
        this.context = context;
        objectIds = new ArrayList<>();
        resultsMap = new HashMap<>();
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
        JSONObject jsonObject = resultsMap.get(objectIds.get(position));
        parseJson(root, bottomView, duplicateKeys, jsonObject);

        DBHelper helper = new DBHelper();
        DaoSession session = helper.getSession(context, false);
        Thing thing = session.getThingDao().queryBuilder().where(ThingDao.Properties.Thing_id.eq(objectIds.get(position))).build().unique();
        if(thing!=null){
            ((ViewHoderDynamic)holder).imageViewBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_orange_24dp));
        }else{
            ((ViewHoderDynamic)holder).imageViewBookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark_grey_24dp));
        }

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

    private void parseJson(LinearLayout root, View bottomView, Set<String> duplicateKeys, JSONObject jsonObject) {
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
                            bottomView = insertGeoButton(value);


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
                                JSONObject imageJson = resultsMap.get(value);
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
                                    JSONObject langObj = values.getJSONObject(0);
                                    String text = langObj.getString("@value");

                                    insertSimpleTextView(root, hrKey, text);
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


    private void saveBlocks(Thing thing, String objectID, DaoSession session, JSONObject jsonObject){
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            //skip "meta"-attributes
            if (!key.startsWith("@")) {
                Log.d("key", key + "");
                try {
                    final String value = jsonObject.getString(key);

                        //get geo coordinates
                    if (key.contains("geo")) {
                        ExtraProperties prp = new ExtraProperties();
                        prp.setThing(thing);
                        prp.setProperty_id(value);
                        JSONObject geoJson = resultsMap.get(value);
                        prp.setContent(geoJson.toString());
                        session.insertOrReplace(prp);

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
                            JSONObject imageJson = resultsMap.get(value);
                            if(imageJson!=null) {
                                ExtraProperties prp = new ExtraProperties();
                                prp.setThing(thing);
                                prp.setProperty_id(value);
                                prp.setContent(imageJson.toString());
                                session.insertOrReplace(prp);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @NonNull
    private View insertGeoButton(String value) throws JSONException {
        //display button to open google maps
        View bottomView;JSONObject geoJson = resultsMap.get(value);
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
        return bottomView;
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
