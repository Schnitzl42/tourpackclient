package at.sti_innsbruck.tourpackclient.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.sti_innsbruck.tourpackclient.logic.datamodel.schema.HotelOrOfferWrapper;
import at.sti_innsbruck.tourpackclient.logic.greendao.DaoSession;
import at.sti_innsbruck.tourpackclient.logic.database.DBHelper;
import at.sti_innsbruck.tourpackclient.R;
import at.sti_innsbruck.tourpackclient.logic.greendao.Thing;
import at.sti_innsbruck.tourpackclient.view.adapters.SavedOffersAdapter;
import at.sti_innsbruck.tourpackclient.view.events.ThingSavedEvent;


public class SavedOffersFragment extends Fragment {


    private RecyclerView recyclerView;
    private SavedOffersAdapter adapter;
    private LinearLayoutManager layoutManager;

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d("savedOffers", "prepare options menu");
        MenuItem item = menu.findItem(R.id.action_search_offers);
        item.setEnabled(false);
        item.setVisible(false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.recycler_view, container, false);


        setupRecyclerView(mView);
        return mView;
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
        //load data for adapter
        DBHelper dbHelper = new DBHelper();
        DaoSession session = dbHelper.getSession(getContext(), true);
        List<Thing> things = session.getThingDao().loadAll();

        Map<String, JSONObject> parts = new HashMap<>();
        //reference for displayable objects
        List<String> objectIds = new ArrayList<>();
        try {

            //put things into map
            for (Thing thing : things) {
                parts.put(thing.getThing_id(), new JSONObject(thing.getContent()));
                objectIds.add(thing.getThing_id());
            }

            adapter = new SavedOffersAdapter(context, parts, objectIds);
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //--------------------------
    // EVENT BUS
    //--------------------------


    @Subscribe
    public void onSavedThingrEvent(ThingSavedEvent event) {
        Log.d("saveFragment", "received new save event!");
        if (adapter != null) {
            adapter.saveNewThing(event.t);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

