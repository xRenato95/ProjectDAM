package dam.projects.projectdam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dam.projects.projectdam.network.MyService;

/**
 * Created by pedro on 05/06/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);
        }
    }
}


