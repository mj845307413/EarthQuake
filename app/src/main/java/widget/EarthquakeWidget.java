package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.majun.earthquake.Earthquake;
import com.majun.earthquake.R;

import provider.EarthquakeProvider;

/**
 * Created by ws02 on 2016/1/26.
 */
public class EarthquakeWidget extends AppWidgetProvider {
    public static String EARTHQUAKE_ACTION = "widget.EarthquakeWidget.EARTHQUAKE_ACTION";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i("majun", "ondelete");
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i("majun", "onenable");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i("majun", "onDisable");

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() == EARTHQUAKE_ACTION) {
            Log.i("majun", "onreceive");
            refreshEarthquake(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i("majun", "onupdate");
        try {

            Log.i("majun", "1111111111111111");
            refreshEarthquake(context, appWidgetIds, appWidgetManager);
        } catch (Exception e) {
            Log.e("majun", e.toString(), e);
        }

    }

    private void refreshEarthquake(Context context) {
        ComponentName componentName = new ComponentName(context, EarthquakeWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetID = appWidgetManager.getAppWidgetIds(componentName);
        refreshEarthquake(context, widgetID, appWidgetManager);
    }

    private void refreshEarthquake(Context context, int[] widgetID, AppWidgetManager appWidgetManager) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Earthquake.class), 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_palce, pendingIntent);
        appWidgetManager.updateAppWidget(widgetID, remoteViews);
        Log.i("majun", "222222222222");
        Cursor cursor = context.getContentResolver().query(EarthquakeProvider.CONTENT_URI, null, null, null, null);
        float mig = 0;
        String place = "majun";
        try {
            if (cursor != null) {
                if (cursor.moveToLast()) {
                    mig = cursor.getFloat(cursor.getColumnIndex(EarthquakeProvider.KEY_MAGNITUDE));
                    place = cursor.getString(cursor.getColumnIndex(EarthquakeProvider.KEY_DETAILS));
                }
            }
        } finally {
            cursor.close();
        }
        final int N = widgetID.length;
        Log.i("majun", "N :" + N);
        for (int i = 0; i < N; i++) {
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
            remoteView.setTextViewText(R.id.widget_mig, String.valueOf(mig));
            remoteView.setTextViewText(R.id.widget_palce, place);
            appWidgetManager.updateAppWidget(widgetID[i], remoteView);
        }
    }
}
