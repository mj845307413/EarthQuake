package service;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.majun.earthquake.R;

import provider.EarthquakeProvider;

/**
 * Created by ws02 on 2016/1/27.
 */
public class EarthquakeRemoteViewService extends RemoteViewsService {
    private static String TAG = "EarthquakeRemoteViewService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(TAG, "EarthquakeRemoteViewService");
        return new EarthquakeRemoteViewFactory(getApplicationContext(), intent);
    }

    class EarthquakeRemoteViewFactory implements RemoteViewsFactory {
        Context context;
        Intent intent;
        int widgetId;

        public EarthquakeRemoteViewFactory(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
            widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            Log.i(TAG, "oncreate");
            cursor = executeCursor();
        }

        @Override
        public void onDataSetChanged() {
            Log.i(TAG, "onDataSetChanged");
            cursor = executeCursor();
        }

        @Override
        public void onDestroy() {
            cursor.close();
        }

        @Override
        public int getCount() {
            if (cursor != null) {
                Log.i(TAG, "count:" + cursor.getCount());
                return cursor.getCount();
            } else
                return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.i(TAG, "getViewAT:" + i);
            RemoteViews remoteViews = null;
            try {
                cursor.moveToPosition(i);
                String id = cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_ID));
                String mag = cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_MAGNITUDE));
                String place = cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_DETAILS));
                remoteViews = new RemoteViews(context.getPackageName(), R.layout.collect_widget_item);
                remoteViews.setTextViewText(R.id.collect_widget_mig, mag);
                remoteViews.setTextViewText(R.id.collect_widget_palce, place);
                Intent fillIntent = new Intent();
                Uri uri = Uri.withAppendedPath(EarthquakeProvider.CONTENT_URI, id);
                fillIntent.setData(uri);
                remoteViews.setOnClickFillInIntent(R.id.collect_widget_palce, fillIntent);
                remoteViews.setOnClickFillInIntent(R.id.collect_widget_mig, fillIntent);

            } catch (Exception e) {
                Log.e("majun", e.toString(), e);
            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            Log.i(TAG, "getViewTypeCount");
            return 1;
        }

        @Override
        public long getItemId(int i) {
            if (cursor != null) {
                long l = cursor.getLong(cursor.getColumnIndex(EarthquakeProvider.KEY_ID));
                Log.i(TAG, "getitem:" + l);
                return l;
            }
            return i;
        }

        @Override
        public boolean hasStableIds() {
            Log.i(TAG, "hasStableIds");
            return true;
        }

        Cursor cursor;

        private Cursor executeCursor() {
            try {
                String[] projection = new String[]{
                        EarthquakeProvider.KEY_ID,
                        EarthquakeProvider.KEY_MAGNITUDE,
                        EarthquakeProvider.KEY_DETAILS
                };

                ContentResolver contentResolver = context.getContentResolver();
                cursor = contentResolver.query(EarthquakeProvider.CONTENT_URI, projection, null, null, null);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return cursor;
        }
    }
}
