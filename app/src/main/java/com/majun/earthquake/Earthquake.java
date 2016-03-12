package com.majun.earthquake;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import gson.Quake;
import gson.QuakeMessage;
import service.EarthQuakeUpdateService;
import widget.EarthquakeWidget;

import static android.app.ActionBar.Tab;

public class Earthquake extends Activity implements EarthquakeListFragment.ChangeFragment {
    private TabListener<EarthquakeListFragment> listFragmentTabListener;
    private TabListener<MapFragment> mapFragmentTabListener;
    private static String Tag = "majun";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        TextView textView = (TextView) findViewById(R.id.textview);
//        textView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//        if (fragment==null)
//        {
//            Log.i(Tag,"fragment is null");
//            getFragmentManager().beginTransaction().add(new Fragment(),"majun");
//        }
//        Fragment fragment1=getFragmentManager().findFragmentByTag("majun");
//        if (fragment1==null)
//        {
//            Log.i(Tag,"haishi null");
//
//        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        updateFromPrefence();
        ActionBar actionBar = getActionBar();
        Log.i(Tag, "actionbar is not null");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        View fragmentContainer = findViewById(R.id.earthquake_fragment);
        listFragmentTabListener = new TabListener<EarthquakeListFragment>(EarthquakeListFragment.class, R.id.earthquake_fragment, this);
        mapFragmentTabListener = new TabListener<MapFragment>(MapFragment.class, R.id.earthquake_fragment, this);
        Tab tab = actionBar.newTab();
        tab.setText("LIST")
                .setIcon(R.drawable.ic_launcher)
                .setContentDescription("earthquake_list")
                .setTabListener(listFragmentTabListener);
        actionBar.addTab(tab);
        Tab maptab = actionBar.newTab();
        maptab.setText("MAP")
                .setIcon(R.drawable.ic_launcher)
                .setContentDescription("show_map")
                .setTabListener(mapFragmentTabListener);
        actionBar.addTab(maptab);
    }


    private static String ACTION_BAR_INDEX = "ACTION_BAR_INDEX";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(Tag, "onSaveInstanceState");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int index = getActionBar().getSelectedTab().getPosition();
        editor.putInt(ACTION_BAR_INDEX, index);
        editor.commit();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (listFragmentTabListener.fragment != null) {
            fragmentTransaction.detach(listFragmentTabListener.fragment);
        }
        if (mapFragmentTabListener.fragment != null) {
            fragmentTransaction.detach(mapFragmentTabListener.fragment);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(Tag, "onRestoreInstanceState");
        listFragmentTabListener.fragment = getFragmentManager().findFragmentByTag(EarthquakeListFragment.class.getName());
        mapFragmentTabListener.fragment = getFragmentManager().findFragmentByTag(MapFragment.class.getName());
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        int index = sharedPreferences.getInt(ACTION_BAR_INDEX, 0);
        getActionBar().setSelectedNavigationItem(index);

    }

    @Override
    protected void onResume() {
        Log.i(Tag, "onResume");
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        int index = sharedPreferences.getInt(ACTION_BAR_INDEX, 0);
        getActionBar().setSelectedNavigationItem(index);
    }

    static final private int MENU_PREFERENCES = Menu.FIRST + 1;
    static final private int MENU_UPDATE = Menu.FIRST + 2;
    private static final int SHOW_PREFERENCES = 1;
    public int minimumMagnitude = 3;
    public boolean autoUpdateChecked = false;
    public int updateFreq = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        MenuItem menuItem = menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
//        menuItem.setIcon(R.drawable.ic_launcher);
//        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        MenuItem menuItem1 = menu.add(0, MENU_UPDATE, Menu.NONE, R.string.menu_update);
//        menuItem1.setIcon(R.drawable.ic_launcher);
//        menuItem1.setActionView(R.layout.maplayout);
//        menuItem1.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.prefence:
                Intent intent = new Intent(this, PreferenceActivity.class);
                startActivityForResult(intent, SHOW_PREFERENCES);
                return true;
            case R.id.refresh:
                intent = new Intent(this, EarthQuakeUpdateService.class);
                startService(intent);
                sendBroadcast(new Intent(EarthquakeWidget.EARTHQUAKE_ACTION));
                return true;
            case R.id.fragment_dialog:
                if (refreshListener2 != null) {
                    refreshListener2.refreshFragment();
                }
//                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newinstance("majun");
//                alertDialogFragment.show(getFragmentManager(), "my_dialog");
                return true;
            case R.id.normal_dialog:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("majun");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;
        }
        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHOW_PREFERENCES:
                if (resultCode == RESULT_OK) {
                    updateFromPrefence();
                    //FragmentManager fragmentManager = getFragmentManager();
//                    final EarthquakeListFragment fragment = (EarthquakeListFragment) fragmentManager.findFragmentById(R.id.EarthquakeListFragment);
                    if (refreshListener != null) {
                        refreshListener.refreshFragment();
                    }
                    if (refreshListener1 != null) {
                        refreshListener1.refreshFragment();
                    }
                    if (refreshListener2 != null) {
                        refreshListener2.refreshFragment();
                    }
                }
        }
    }

    private void updateFromPrefence() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        boolean autoUpChecked = sharedPreferences.getBoolean(PreferenceActivity.PREF_AUTO_UPDATE, false);
        int updateFreqIndex = sharedPreferences.getInt(PreferenceActivity.PREF_UPDATE_FREQ_INDEX, 2);
        int minMagIndex = sharedPreferences.getInt(PreferenceActivity.PREF_MIN_MAG_INDEX, 0);
        String[] mintime = getResources().getStringArray(R.array.magnitude);
        String[] updateFrequent = getResources().getStringArray(R.array.update_freq_values);
        minimumMagnitude = Integer.valueOf(mintime[minMagIndex]);
        updateFreq = Integer.valueOf(updateFrequent[updateFreqIndex]);
        autoUpdateChecked = autoUpChecked;
    }

    @Override
    public void changeToMap(Quake quake) {
        try {
            QuakeMessage.getInstance_quakeMessage().setLocation(quake.getLocation());
            getActionBar().setSelectedNavigationItem(1);
        } catch (Exception e) {
            Log.e("majun", e.getMessage(), e);
        }

//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        MapFragment mapFragment = new MapFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("earthquake", quake);
//        mapFragment.setArguments(bundle);
//
//        fragmentTransaction.replace(R.id.earthquake_fragment, mapFragment);
//        fragmentTransaction.commit();
//        Toast.makeText(getApplicationContext(), quake.toString(), Toast.LENGTH_LONG).show();
    }


    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment fragment;
        private Activity activity;
        private Class<T> fragmentClass;
        private int fragmentContainer;

        public TabListener(Class<T> fragmentClass, int fragmentContainer, Activity activity) {
            this.fragmentClass = fragmentClass;
            this.activity = activity;
            this.fragmentContainer = fragmentContainer;
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
            Log.i("majun", "onTabSelected");
            if (fragment == null) {
                String fragmentName = fragmentClass.getName();
                fragment = Fragment.instantiate(activity, fragmentName);//
                if (QuakeMessage.getInstance_quakeMessage() != null) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("quake", QuakeMessage.getInstance_quakeMessage());
                        fragment.setArguments(bundle);
                    } catch (Exception e) {
                        Log.e("majun", e.toString(), e);
                    }
                }
                fragmentTransaction.add(fragmentContainer, fragment, fragmentName);
            } else {
                fragmentTransaction.attach(fragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
            Log.i("majun", "onTabUnselected");

            if (fragment != null) {

                fragmentTransaction.detach(fragment);
            }
        }

        //当连续点击两次tab时会加载这个函数
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
            Log.i("majun", "onTabReselected");
            if (fragment != null) {
                fragmentTransaction.attach(fragment);
            }
        }
    }

    public void setRefreshListener(RefreshListener refreshListener) {
        Earthquake.refreshListener = refreshListener;
    }


    private static RefreshListener refreshListener;

    public interface RefreshListener {
        void refreshFragment();

    }


    public void setRefreshListener1(RefreshListener1 refreshListener1) {
        Earthquake.refreshListener1 = refreshListener1;
    }

    private static RefreshListener1 refreshListener1;

    public interface RefreshListener1 {
        void refreshFragment();

    }

    public void setRefreshListener2(RefreshListener2 refreshListener2) {
        Earthquake.refreshListener2 = refreshListener2;
    }

    private static RefreshListener2 refreshListener2;

    public interface RefreshListener2 {
        void refreshFragment();

    }
}
