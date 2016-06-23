package at.sti_innsbruck.tourpackclient.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;


import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import at.sti_innsbruck.tourpackclient.MainActivity;
import at.sti_innsbruck.tourpackclient.logic.http.AsyncHttp;
import at.sti_innsbruck.tourpackclient.logic.http.AsyncHttpRequestListener;
import at.sti_innsbruck.tourpackclient.logic.datamodel.MyLocation;
import at.sti_innsbruck.tourpackclient.logic.datamodel.schema.HotelOrOfferWrapper;
import at.sti_innsbruck.tourpackclient.logic.datamodel.schema.SchemaLodgingBuisness;
import at.sti_innsbruck.tourpackclient.logic.datamodel.schema.SchemaLodgingBuisnessDetails;
import at.sti_innsbruck.tourpackclient.logic.datamodel.schema.SchemaOffer;
import at.sti_innsbruck.tourpackclient.R;
import at.sti_innsbruck.tourpackclient.view.adapters.SearchOffersAdapter;
import at.sti_innsbruck.tourpackclient.view.events.PlaceSelectedEvent;
import at.sti_innsbruck.tourpackclient.view.events.ThingRemovedEvent;
import okhttp3.Request;



public class SearchOffersFragment extends Fragment implements DatePickerDialog.OnDateSetListener, AsyncHttpRequestListener {

    //SEARCH
    private LinearLayout linearLayoutSearch;
    private EditText editTextSearchString;
    private ImageView imageViewSearch;
    //search type
    private Spinner spinnerType;
    //price
    private TextView textViewPrice;
    private SeekBar seekBarPrice;
    //Region
    private TextView textViewRadius;
    private TextView textViewLocation;
    private SeekBar seekbarRadius;
    private Button buttonSelectPlace;
    private MyLocation networkLocation;
    //Date
    private TextView textViewDate;
    private Button buttonDateRange;

    public boolean isSearchVisible = true;

    //RESULT LIST
    private RecyclerView recyclerView;
    private SearchOffersAdapter adapter;
    private LinearLayoutManager layoutManager;

    private Context context;
    private MenuItem searchItem;

    private LatLng coords;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        //subscribe event bus
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show options menu with search icon
        setHasOptionsMenu(true);
    }

    //TODO listener for actionbar
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d("searchOffers", "prepare options menu");
        MenuItem item = menu.findItem(R.id.action_search_offers);
        item.setEnabled(true);
        item.setVisible(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_offers, container, false);
        //to search offers
        setupSearchView(view);
        //to display results
        setupRecyclerView(view);
        return view;
    }

    private void setupSearchView(View view) {

        linearLayoutSearch = (LinearLayout) view.findViewById(R.id.linearLayoutSearch);
        //search
        editTextSearchString = (EditText) view.findViewById(R.id.editTextSearchString);
        imageViewSearch = (ImageView) view.findViewById(R.id.imageViewSearch);
        //search type
        spinnerType = (Spinner) view.findViewById(R.id.spinner);
        //price
        textViewPrice = (TextView) view.findViewById(R.id.textViewPrice);
        seekBarPrice = (SeekBar) view.findViewById(R.id.seekbarPrice);
        //region
        textViewRadius = (TextView) view.findViewById(R.id.textViewRadius);
        //http://www.truiton.com/2015/04/using-new-google-places-api-android/
        //TODO tutorial for setting up place api
        seekbarRadius = (SeekBar) view.findViewById(R.id.seekbarRadius);
        textViewLocation = (TextView) view.findViewById(R.id.textViewLocation);
        buttonSelectPlace = (Button) view.findViewById(R.id.buttonSelectPlace);
        //Date
        textViewDate = (TextView) view.findViewById(R.id.textViewDateRange);
        buttonDateRange  = (Button) view.findViewById(R.id.buttonSelectDateRange);

        //lisetners
        //Spinner search type
        List<String> categories = new ArrayList<>();
        categories.add("Lodging Buisnesses");
        categories.add("Offers");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerType.setAdapter(dataAdapter);

        //PRICE
        seekBarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==seekBar.getMax()){
                    textViewPrice.setText(">"+progress + "");
                }else {
                    textViewPrice.setText(progress + "");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarPrice.setProgress(50);
        //REGION
        networkLocation = getNetworkLocation();
        textViewLocation.setText(networkLocation.address);
        seekbarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==seekBar.getMax()){
                    textViewRadius.setText(">"+progress + "");
                }else {
                    textViewRadius.setText(progress + "");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //start Location picker with current location
        buttonSelectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    // use current location
                    //final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                    //        new LatLng(networkLocation.lat-0.3,  networkLocation.lng+0.3), new LatLng(networkLocation.lat+0.3, networkLocation.lng-0.3));
                    //not required will pick current location instead
                    //intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    ((MainActivity) context).showPlacePicker(intentBuilder);
            }
        });
        seekbarRadius.setProgress(10);

        //DATE
        buttonDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        SearchOffersFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(((MainActivity)context).getFragmentManager(), "Datepickerdialog");
            }
        });

        //SEARCH
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("search","requested search!");
                //currently backend does not support search strings
                String query = editTextSearchString.getText().toString();

                //ignore price when set to 0
                int price = seekBarPrice.getProgress();
                if(price==0 || price==seekBarPrice.getMax()){
                    price = Integer.MAX_VALUE;
                }
                int radius = seekbarRadius.getProgress();

                String queryType;
                String selected = spinnerType.getSelectedItem().toString().toLowerCase();
                if(selected.contains("offer")){
                    queryType = "offers";
                }else{
                    queryType = "businesses";
                }
                    //if everything is OK
                    Request request = new Request.Builder()
                            .url("http://sws.tr1k.de/"+queryType)
                            .header("lat", coords.latitude+"")
                            .addHeader("lon", coords.longitude+"")
                            .addHeader("radius", radius+"")
                            .addHeader("maxprice", price+"")
                            .build();

                    AsyncHttp conn = new AsyncHttp();
                    //invokes processResult(json)
                    conn.getJSON(SearchOffersFragment.this, context, request);

                    Toast.makeText(context, "Searching: " + query +
                            " price: " + price, Toast.LENGTH_LONG).show();

            }
        });
    }

    //https://developer.android.com/guide/topics/location/strategies.html
    private MyLocation getNetworkLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Or use LocationManager.GPS_PROVIDER
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);

        MyLocation location = new MyLocation(47.2692,11.4041,"Innsbruck", "Austria");
        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                int minDistChangesForUpdate = 100; //meters
                ActivityCompat.requestPermissions(((MainActivity) context), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        minDistChangesForUpdate);
            }

            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);


            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            this.coords = new LatLng(latitude, longitude);

            //location details
            Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
            StringBuilder builder = new StringBuilder();
            try {
                List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i = 0; i < maxLines; i++) {
                    String addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append(" ");
                }

                String finalAddress = builder.toString();

                location = new MyLocation(longitude, latitude, finalAddress,finalAddress);

                return location;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    private void setupRecyclerView(View mView) {
        //setup recycler view (list)
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SearchOffersAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    //--------------------------
    // ACTION BAR SEARCH BEHVIOR
    //--------------------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_search_offers:
                Log.d("searchFragment", "pressed search action");
                changeSearchViewVisibility();
                return true;
        }
        return false;
    }

    private void changeSearchViewVisibility() {
        isSearchVisible = !isSearchVisible;
        if(isSearchVisible){

            linearLayoutSearch.setVisibility(View.VISIBLE);
           /* linearLayoutSearch.setAlpha(0.0f);
            linearLayoutSearch.animate()
                    .translationY(linearLayoutSearch.getHeight())
                    .alpha(1.0f);*/
        }else{
         /*   linearLayoutSearch.animate()
                    .translationY(0)
                 .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //linearLayoutSearch.setVisibility(View.GONE);
                        }
                    });*/
            linearLayoutSearch.setVisibility(View.GONE);

        }
    }

    //--------------------------
    // HTTP RESULTS
    //--------------------------

    private void loadFakeResults() {
        //Load json from assets
        try {
        InputStream is = context.getAssets().open("hotels.json");
            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObj = new JSONObject(json);
            //hide search box when results are retrived
            changeSearchViewVisibility();
            //onRetrieveResult(jsonObj);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void processResult(JSONObject json) {
        //hide search box
        if(json==null){
            Toast.makeText(context, "Could not load offers!", Toast.LENGTH_LONG).show();
        }else {
            changeSearchViewVisibility();
            Log.d("result", json.toString());

            //parse result into objects
            Map<String, JSONObject> parts = new HashMap<>();
            //reference for displayable objects
            List<String> objectIds = new ArrayList<>();
            try {
                JSONArray graph = json.getJSONArray("@graph");
                List<HotelOrOfferWrapper> lodgingBuisnesses = new ArrayList<>();

                for (int i = 0; i < graph.length(); i++) {

                    JSONObject part = graph.getJSONObject(i);
                    String id = part.getString("@id");
                    parts.put(id, part);
                    //only save "real" objects to objectId-list
                    if (id.startsWith("http")) {
                        objectIds.add(id);
                    }

                    Log.d("part", "@id: " + id + " cont: " + part.toString());
                /*final Gson gson = new Gson();

                SchemaLodgingBuisness lodgingBuisness = null;
                lodgingBuisness = gson.fromJson(graph.get(i).toString(), SchemaLodgingBuisness.class);
                //add schema string
                lodgingBuisness.setWholeSchema(graph.get(i).toString());
                //TODO add hotel details
                lodgingBuisness.setDetails(new SchemaLodgingBuisnessDetails());
                lodgingBuisnesses.add(new HotelOrOfferWrapper(lodgingBuisness));
                Log.d("hotel", lodgingBuisness.getId()+" "+lodgingBuisness.getName()+" "+lodgingBuisness.getMakesOffer());
                //TODO add offers after hotel
                //for(...
                SchemaOffer offer = new SchemaOffer();

                lodgingBuisnesses.add(new HotelOrOfferWrapper(offer));
                //get offers from makesOffer
                //get hotel info form id
                */
                }

                if (adapter != null) {
                    adapter.displayNewResults(parts, objectIds);
                    //adapter.displaySearchResults(lodgingBuisnesses);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
/*
    private void onRetrieveResult(JSONObject json){
        //TODO
        Log.d("result", json.toString()+"");
        try {
            JSONArray hotels =  json.getJSONArray("@graph");
            List<HotelOrOfferWrapper> lodgingBuisnesses = new ArrayList<>();
            for(int i=0; i<hotels.length(); i++) {
                //ObjectMapper mapper = new ObjectMapper();
                //SchemaLodgingBuisness lodgingBuisness = null;

                //lodgingBuisness = mapper.readValue(hotels.get(i).toString(), SchemaLodgingBuisness.class);


                final Gson gson = new Gson();

                SchemaLodgingBuisness lodgingBuisness = null;
                lodgingBuisness = gson.fromJson(hotels.get(i).toString(), SchemaLodgingBuisness.class);
                //add schema string
                lodgingBuisness.setWholeSchema(hotels.get(i).toString());
                //TODO add hotel details
                lodgingBuisness.setDetails(new SchemaLodgingBuisnessDetails());
                lodgingBuisnesses.add(new HotelOrOfferWrapper(lodgingBuisness));
                Log.d("hotel", lodgingBuisness.getId()+" "+lodgingBuisness.getName()+" "+lodgingBuisness.getMakesOffer());
                //TODO add offers after hotel
                //for(...
                SchemaOffer offer = new SchemaOffer();

                lodgingBuisnesses.add(new HotelOrOfferWrapper(offer));
                //get offers from makesOffer
                //get hotel info form id
            }

            if(adapter!=null){
                adapter.displaySearchResults(lodgingBuisnesses);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }*/

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        if(textViewDate!=null){
            //TODO save numbers or as date
            textViewDate.setText(String.format(Locale.ROOT, "%02d.%02d.%d - %02d.%02d.%d ",dayOfMonth,monthOfYear,year,dayOfMonthEnd,monthOfYearEnd, yearEnd));
        }
    }


    //--------------------------
    // EVENT BUS
    //--------------------------

    //event sent by main activity when place is selected
    @Subscribe
    public void onPlaceSelectedEvent(PlaceSelectedEvent event){
       if(textViewLocation != null){
           this.coords = event.coords;
           textViewLocation.setText(event.name+" "+event.address+" "+event.attributtions);
       }
        //TODO save data that is required for search
    }

    @Subscribe
    public void onThingRemovedEvent(ThingRemovedEvent event){
       if(this.adapter!=null){
           adapter.thingRemoved(event.thingId);
       }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
