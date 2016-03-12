package broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import service.EarthQuakeUpdateService;

/**
 * Created by ws02 on 2016/1/19.
 */
public class EarthquakeBreadcast extends BroadcastReceiver {
    private Intent intent;
    public static final String EarthquakeBreadcast_Action="broadcast.EarthquakeBreadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("majun","EarthquakeBreadcast");
        intent = new Intent(context, EarthQuakeUpdateService.class);
        context.startService(intent);
    }
}
