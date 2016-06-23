package at.sti_innsbruck.tourpackclient.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import at.sti_innsbruck.tourpackclient.R;
import at.sti_innsbruck.tourpackclient.view.adapters.SectionsPagerAdapter;


public class TabbedFragmentMain  extends Fragment implements ViewPager.OnPageChangeListener {

    public static final int SEARCH_TAB_POS = 0;
    public static final int SAVED_TAB_POS = 1;


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private int mTabPosition;
    //Restoration keys
    private final String TAB_POS_KEY = "tabPosKey";
    private final String FRAGMENT_TAGS_KEY = "fragmentTags";


    public void setCurrentTab(int position){
        mTabPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mViewPager!=null) {
            outState.putInt(TAB_POS_KEY, mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("chatStat", "oncreate");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tabbed_fragment_main, container, false);

        getActivity().invalidateOptionsMenu();
        //adapter for view pager
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager(), this.getActivity());
        //setup view pager
        mViewPager = (ViewPager) v.findViewById(R.id.pagerTabLayout);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        //RESTORE INSTANCE STATE
        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey(TAB_POS_KEY)) {
                mTabPosition = savedInstanceState.getInt(TAB_POS_KEY);
            }
        }
        mViewPager.setCurrentItem(mTabPosition);

        //setup tab layout
        TabLayout mTabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mSectionsPagerAdapter);

        //setup options menu
        setHasOptionsMenu(true);

        return v;
    }

    //done with event bus

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
