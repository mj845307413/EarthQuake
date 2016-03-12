package com.majun.earthquake;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;

import java.util.Date;

import gson.Quake;
import provider.EarthquakeProvider;

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {
    private final static String TAG = "majun";
    //ArrayAdapter<Quake> aa;
    SimpleCursorAdapter simpleCursorAdapter;
    Earthquake earthquakeActivity;
    private PopupMenu popupMenu;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        earthquakeActivity = (Earthquake) getActivity();
        earthquakeActivity.setRefreshListener1(new Earthquake.RefreshListener1() {
            @Override
            public void refreshFragment() {
                try {
                    refreshQuakeStatus();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        try {
            int layoutID = android.R.layout.simple_list_item_1;
            simpleCursorAdapter = new SimpleCursorAdapter(getActivity(), layoutID, null, new String[]{EarthquakeProvider.KEY_SUMMARY}, new int[]{android.R.id.text1}, 0);
            //aa = new ArrayAdapter<Quake>(getActivity(), layoutID, earthquakes);
            setListAdapter(simpleCursorAdapter);

            getLoaderManager().initLoader(0, null, this);
            Log.i(TAG, "initLoader");
            refreshQuakeStatus();
            getListView().setOnItemLongClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        changeFragment = (ChangeFragment) activity;
        super.onAttach(activity);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, final long id) {
        super.onListItemClick(l, v, position, id);
        if (changeFragment != null) {
            changeFragment.changeToMap(getquake(id));
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, final long l) {
        Log.i("majun", "i=:" + i + "\r\n" + "l=" + l);
        popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.popupmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu1:
                        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newinstance(getActivity(), getquake(l));
                        alertDialogFragment.show(getFragmentManager(), "quake");
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
        return true;
    }

    private Quake getquake(long id) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContentUris.withAppendedId(EarthquakeProvider.CONTENT_URI, id), null, null, null, null);
        Quake quake = null;
        if (cursor.moveToFirst()) {
            Date date = new Date(cursor.getLong(cursor.getColumnIndex(EarthquakeProvider.KEY_DATE)));
            String detail = new String(cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_DETAILS)));
            Double magnitude = new Double(cursor.getDouble(cursor.getColumnIndex(EarthquakeProvider.KEY_MAGNITUDE)));
            String linkString = new String(cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_LINK)));
            Double lat = new Double(cursor.getDouble(cursor.getColumnIndex(EarthquakeProvider.KEY_LOCATION_LAT)));
            Double lng = new Double(cursor.getDouble(cursor.getColumnIndex(EarthquakeProvider.KEY_LOCATION_LNG)));
            Location location = new Location("db");
            location.setLatitude(lat);
            location.setLongitude(lng);
            quake = new Quake(date, detail, location, magnitude, linkString);
        }
        return quake;
    }

    private void refreshQuakeStatus() {
        Log.i(TAG, "refreshQuakeStatus");
        getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);
    }

    /**
     * 通过cursor，绑定provider
     * 这边通过cursorloader来更新listfragment。
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "onCreateLoader");
        String[] projection = new String[]{EarthquakeProvider.KEY_ID, EarthquakeProvider.KEY_SUMMARY};
        String where = EarthquakeProvider.KEY_MAGNITUDE + ">" + earthquakeActivity.minimumMagnitude;
        Log.i(TAG, "minimumMagnitude:" + earthquakeActivity.minimumMagnitude);
        CursorLoader cursorLoader = new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI, projection, where, null, null);//selectionargs是配合where使用的
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        simpleCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "onLoaderReset");
        simpleCursorAdapter.swapCursor(null);//
    }

    public void setChangeFragment(ChangeFragment changeFragment) {
        this.changeFragment = changeFragment;
    }

    public ChangeFragment changeFragment;

    public interface ChangeFragment {
        void changeToMap(Quake quake);
    }
}