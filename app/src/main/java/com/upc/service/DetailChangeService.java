package com.upc.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;

import com.upc.javabean.User;
import com.upc.receiver.StateChangeReceiver;

public class DetailChangeService extends IntentService {

    StateChangeReceiver stateChangeReceiver;
    public DetailChangeService() {
        super("DetailChangeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            stateChangeReceiver = new StateChangeReceiver();//注册接收器
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.upc.action.USERCHANGE");
            registerReceiver(stateChangeReceiver,intentFilter);
            Intent receiveIntent = new Intent();
            User user = (User)intent.getSerializableExtra("user");
            Bundle bundle = new Bundle();
            bundle.putSerializable("user",user);
            receiveIntent.putExtras(bundle);
            receiveIntent.setAction("com.upc.action.USERCHANGE");
            sendBroadcast(receiveIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
