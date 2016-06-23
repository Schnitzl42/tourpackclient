package at.sti_innsbruck.tourpackclient.view.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import at.sti_innsbruck.tourpackclient.R;
import at.sti_innsbruck.tourpackclient.view.fragments.SavedOffersFragment;
import at.sti_innsbruck.tourpackclient.view.fragments.SearchOffersFragment;
import at.sti_innsbruck.tourpackclient.view.fragments.TabbedFragmentMain;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager mFragmentManager;
    private Context context;


    public SectionsPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        mFragmentManager = fm;
        this.context = context;
    }

    /*
        will be called once to initialize fragment once
     */
    @Override
    public Fragment getItem(int position) {
        //Log.d("allt", "getItem() called, pos: "+position);
        String name = makeFragmentName(R.id.pagerTabLayout, position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if(fragment == null) {
            switch (position) {
                case TabbedFragmentMain.SEARCH_TAB_POS:
                    fragment = new SearchOffersFragment();
                    break;
                case TabbedFragmentMain.SAVED_TAB_POS:
                    fragment =  new SavedOffersFragment();
                    break;
            }
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {

            case  TabbedFragmentMain.SEARCH_TAB_POS:
                return context.getString(R.string.main_tab_search);
            case TabbedFragmentMain.SAVED_TAB_POS:
                return context.getString(R.string.main_tab_saved);
        }
        return null;
    }

    /*
    override this method from the base class to ensure whe have the right tag
     */
    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }
}

