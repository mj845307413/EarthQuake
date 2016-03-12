package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.majun.earthquake.Earthquake;
import com.majun.earthquake.R;

import service.EarthquakeRemoteViewService;

/**
 * Created by ws02 on 2016/1/27.
 */
public class EarthquakeListWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        try {
            Log.i("majun", "list_Widget_num:" + N);
            for (int i = 0; i < N; i++) {
                int appWidgetID = appWidgetIds[i];

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collect_view_widget);
                Intent intent = new Intent(context, EarthquakeRemoteViewService.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);

                remoteViews.setRemoteAdapter(R.id.collect_list_widget, intent);
                remoteViews.setEmptyView(R.id.collect_list_widget, R.id.collect_empty_widget);
                Intent templateIntent = new Intent(context, Earthquake.class);
                templateIntent.putExtra(appWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, templateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setPendingIntentTemplate(R.id.collect_list_widget, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
                Log.i("majun", "end_EarthquakeListWidget");
            }
        } catch (Exception e) {
            Log.e("majun", e.getMessage(), e);
        }
    }
}
