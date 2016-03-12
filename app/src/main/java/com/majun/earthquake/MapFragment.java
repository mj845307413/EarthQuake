package com.majun.earthquake;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

import gson.QuakeMessage;
import provider.EarthquakeProvider;

/**
 * Created by ws02 on 2016/1/20.
 */
public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View view = null;
    private MapView mapView = null;
    private BaiduMap mBaiduMap = null;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private static String TAG = "majun";
    private Earthquake earthquakeActivity;
    private ArrayList<LatLng> earthLatLngs = new ArrayList<LatLng>();
    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("majun", "mapfragmrnt_oncreatview");

        try {
            Bundle bundle = getArguments();
            QuakeMessage quake = (QuakeMessage) bundle.getSerializable("quake");
            if (quake == null) {
                Log.i("majun", "nullllllllllllllllllllllllllllllllll");
            }
            earthquakeActivity = (Earthquake) getActivity();
            earthquakeActivity.setRefreshListener2(new Earthquake.RefreshListener2() {
                @Override
                public void refreshFragment() {
                    refreshMapStatus();
                }
            });
            getLoaderManager().initLoader(0, null, this);
            view = inflater.inflate(R.layout.maplayout, null);
            mapView = (MapView) view.findViewById(R.id.bmapView);
            mBaiduMap = mapView.getMap();
            locateMarker(quake);
            float v = mBaiduMap.getMinZoomLevel();
            MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(v);
            mBaiduMap.setMapStatus(u1);
        } catch (Exception e) {
            Log.e("majun", e.getMessage(), e);
        }
        return view;

    }

    //增加markerdian
    private void locateMarker(QuakeMessage quake) {

        Toast.makeText(getActivity(), quake.getLocation().toString(), Toast.LENGTH_SHORT).show();
//                    LatLng latLng = new LatLng(39.963175, 116.400244);
        LatLng latLng = new LatLng(quake.getLocation().getLatitude(), quake.getLocation().getLongitude());
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(u);

    }

    private void refreshMapStatus() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMapStatus();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.i(TAG, "map_onCreateLoader");
        String[] projection = new String[]{EarthquakeProvider.KEY_ID, EarthquakeProvider.KEY_LOCATION_LAT, EarthquakeProvider.KEY_LOCATION_LNG, EarthquakeProvider.KEY_DETAILS};
        String where = EarthquakeProvider.KEY_MAGNITUDE + ">" + earthquakeActivity.minimumMagnitude;
        Log.i(TAG, "minimumMagnitude:" + earthquakeActivity.minimumMagnitude);
        CursorLoader cursorLoader = new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI, projection, where, null, null);//selectionargs是配合where使用的
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "map_onLoadFinished");
        earthLatLngs.clear();
//        for (Marker marker : markers) {
//            marker.remove();
//        }
        mBaiduMap.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                float lat = cursor.getFloat(cursor.getColumnIndex(EarthquakeProvider.KEY_LOCATION_LAT));
                float lng = cursor.getFloat(cursor.getColumnIndex(EarthquakeProvider.KEY_LOCATION_LNG));
                String place = cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_DETAILS));
                LatLng latLng = new LatLng(lat, lng);
                earthLatLngs.add(latLng);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, earthLatLngs.toString());
        for (LatLng latLng : earthLatLngs) {
            //MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            //mBaiduMap.animateMapStatus(u);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(bdA);
            Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
            markers.add(marker);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
