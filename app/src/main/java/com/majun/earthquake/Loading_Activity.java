package com.majun.earthquake;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import service.EarthQuakeUpdateService;

/**
 * Created by ws02 on 2016/1/28.
 */
public class Loading_Activity extends Activity {
    private InitBroadcastReciver mInitBroadcastReciver;
    public static String INIT_SUCCESS_BROADCAST = "EARTHQUAKE_INIT_SUCCESS_BROADCAST";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            unRegisterInitBroadcastReciver();
            Intent startIntent = new Intent(Loading_Activity.this, Earthquake.class);
            startActivity(startIntent);
            finish();
        }
    };

    private void unRegisterInitBroadcastReciver() {
        unregisterReceiver(mInitBroadcastReciver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        Intent intent = new Intent(this, EarthQuakeUpdateService.class);
        startService(intent);
        registerInitBroadcastReciver();
    }

    public void registerInitBroadcastReciver() {
        if (mInitBroadcastReciver == null) {
            mInitBroadcastReciver = new InitBroadcastReciver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INIT_SUCCESS_BROADCAST);
            registerReceiver(mInitBroadcastReciver, intentFilter);
        }
    }

    class InitBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(0);
        }
    }
}
