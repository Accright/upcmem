package com.upc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.upc.javabean.User;

public class StateChangeReceiver extends BroadcastReceiver {

    private static final int CHANGED = 1;
    private static final int UNCHANGED = 0;
    private int state = UNCHANGED;
    public User user = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle bundle = intent.getExtras();
        user = (User)bundle.getSerializable("user");
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
