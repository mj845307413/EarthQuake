package service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.majun.earthquake.Earthquake;
import com.majun.earthquake.Loading_Activity;
import com.majun.earthquake.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;

import broadcast.EarthquakeBreadcast;
import gson.Features;
import gson.MYProperties;
import gson.Quake;
import gson.Status;
import provider.EarthquakeProvider;
import widget.EarthquakeListWidget;

/**
 * Created by ws02 on 2016/1/18.
 */
public class EarthQuakeUpdateService extends IntentService {

    private static final String TAG = "majun";
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    //private Timer timer;
    private Earthquake earthquakeActivity;
    private int updateFrequent;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private NotificationManager notificationManager;
    private PendingIntent notifyPendingIntent;
    private Notification.Builder notificationBuilder;

    public EarthQuakeUpdateService() {
        super("service.EarthQuakeUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service_oncreat");
        earthquakeActivity = new Earthquake();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        earthquakeActivity.setRefreshListener(new Earthquake.RefreshListener() {
            @Override
            public void refreshFragment() {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
                    int updateFrequentIndex = sharedPreferences.getInt("PREF_UPDATE_FREQ_INDEX", 0);
                    String[] strings = getResources().getStringArray(R.array.update_freq_values);
                    updateFrequent = Integer.parseInt(strings[updateFrequentIndex]);
                    Boolean isUpdate = sharedPreferences.getBoolean(PREF_AUTO_UPDATE, false);

                    if (isUpdate) {
                        Log.i(TAG, "start timer");
                        //
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + updateFrequent * 5000, updateFrequent * 5000, pendingIntent);
                        //timerTask.setPeriod(updateFrequent * 1000*3);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                alarmManager.cancel(pendingIntent);
                                refreshQuakeStatus();
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(EarthquakeBreadcast.EarthquakeBreadcast_Action);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        Intent intent = new Intent(this, EarthQuakeUpdateService.class);
//        pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreference", MODE_PRIVATE);
        int updateFrequentIndex = sharedPreferences.getInt("PREF_UPDATE_FREQ_INDEX", 0);
        String[] strings = getResources().getStringArray(R.array.update_freq_values);
        updateFrequent = Integer.parseInt(strings[updateFrequentIndex]);
        Boolean isUpdate = sharedPreferences.getBoolean(PREF_AUTO_UPDATE, false);
//        timer=new Timer();
        if (isUpdate) {
            Log.i(TAG, "start timer");
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + updateFrequent * 5000, updateFrequent * 5000, pendingIntent);
//            timer = new Timer();
//            timer.schedule(timerTask, 0, updateFrequent * 60*1000);
        }
        notifyPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, Earthquake.class), 0);
        notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                .setTicker("earthquake")
                .setAutoCancel(true);

    }

    private void broadcastNotification(Quake quake) {

        notificationBuilder
                .setContentIntent(notifyPendingIntent)
                .setContentText(quake.getDetails())
                .setContentTitle("M:" + quake.getMagnitude())
                .setWhen(quake.getDate().getTime())
                .setVibrate(new long[]{1000, 1000, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                .setLights(Color.RED, 0, 1);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(0, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "service_onHandleIntent");
        try {
            if (intent.getData() != null) {
                Log.i(TAG, intent.getData().toString());
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    refreshQuakeStatus();
                }
            }).start();
//        timer.cancel();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

//    private TimerTest timerTask = new TimerTest() {
//        @Override
//        public void run() {
//            refreshQuakeStatus();
//        }
//    };

    private void addNewQuake(Quake _quake) {
        // Add the new quake to our list of earthquakes.
//        if (_quake.getMagnitude() > earthquakeActivity.minimumMagnitude) {
//            earthquakes.add(_quake);
//            simpleCursorAdapter.notifyDataSetChanged();
//        }
        // Notify the array adapter of a change.
        //Log.i(TAG, "start add quake");
        if (_quake.getMagnitude() > earthquakeActivity.minimumMagnitude) {
            //Log.i(TAG, "earthquakeActivity.minimumMagnitude:" + earthquakeActivity.minimumMagnitude);
            ContentResolver contentResolver = getContentResolver();
            String where = EarthquakeProvider.KEY_DATE + "=" + _quake.getDate().getTime();//用于判断更新的地震信息是否已经有过
            Cursor cursor = contentResolver.query(EarthquakeProvider.CONTENT_URI, null, where, null, null);
            if (cursor.getCount() == 0) {
                ContentValues values = new ContentValues();

                values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
                values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());
                values.put(EarthquakeProvider.KEY_SUMMARY, _quake.toString());
                double lat = _quake.getLocation().getLatitude();
                double lng = _quake.getLocation().getLongitude();
                values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
                values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
                values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
                values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());
                contentResolver.insert(EarthquakeProvider.CONTENT_URI, values);
                broadcastNotification(_quake);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                ComponentName componentName = new ComponentName(getApplicationContext(), EarthquakeListWidget.class);
                int[] widgetIDs = appWidgetManager.getAppWidgetIds(componentName);
                appWidgetManager.notifyAppWidgetViewDataChanged(widgetIDs, R.id.collect_list_widget);

            }
            cursor.close();
        }

    }

    Handler handler = new Handler();

    private void refreshQuakeStatus() {
        new Thread() {
            @Override
            public void run() {
                try {

                    refreshEarthquakes_gson();
                } catch (Exception e) {
                    Log.e(TAG, "inThread:" + e.toString(), e);
                }
            }
        }.start();
    }

    private void refreshEarthquakes_gson() {
        Log.i(TAG, "start_thread");
        String quakeFeed = getString(R.string.quake_feed);
        String s = getJsonContent(quakeFeed);
        Gson gson = new Gson();
        Status status = gson.fromJson(s, Status.class);
        //String featureString=status.getFeatures().toString();
        // Log.i(TAG, "status=" + status);
        //String featureString=status.getFeatures().get(1).toString();
        //Log.i(TAG, "feature=" + featureString);
//        earthquakes.clear();
        Log.i(TAG, "features_num:" + status.getFeatures().size());

        for (int i = 0; i < status.getFeatures().size(); i++) {
            Features features = status.getFeatures().get(i);
            MYProperties myProperties = features.getMyProperties();
            float[] coordinations = features.getGeometry().getCoordinates();
            final Quake quake = new Quake();
//           quake.setLocation(myProperties.getPlace());
            Location location = new Location("dummyGPS");
            location.setLatitude(coordinations[1]);
            location.setLongitude(coordinations[0]);
            quake.setLocation(location);
            quake.setDate(new Date(myProperties.getTime()));
            //quake.setDetails("gao");
            quake.setDetails(myProperties.getPlace());
            quake.setLink(myProperties.getUrl());
            //quake.setLink("majun");
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            quake.setMagnitude(Double.parseDouble(decimalFormat.format(Double.parseDouble(String.valueOf(myProperties.getMag())))));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    addNewQuake(quake);
                }
            });

        }
        Intent intent = new Intent();
        intent.setAction(Loading_Activity.INIT_SUCCESS_BROADCAST);
        sendBroadcast(intent);
    }

    public static String getJsonContent(String urlStr) {
        try {// 获取HttpURLConnection连接对象
            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url
                    .openConnection();
            // 设置连接属性
            httpConn.setConnectTimeout(3000);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("GET");
            // 获取相应码
            int respCode = httpConn.getResponseCode();
            if (respCode == 200) {
                return ConvertStream2Json(httpConn.getInputStream());
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }


    private static String ConvertStream2Json(InputStream inputStream) {
        String jsonStr = "";
        // ByteArrayOutputStream相当于内存输出流
        //缓存专用。
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        // 将输入流转移到内存输出流中
        try {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            // 将内存流转换为字符串
            jsonStr = new String(out.toByteArray());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Log.i(TAG, "jsonStr1:" + jsonStr);
        return jsonStr;
    }
}
