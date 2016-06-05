package dam.projects.projectdam.network;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

import dam.projects.projectdam.R;
import dam.projects.projectdam.global.MarksAsync;
import dam.projects.projectdam.global.UpdateAsync;
import dam.projects.projectdam.gui.MenuActivity;
import dam.projects.projectdam.notifications.Notification;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by pedro on 04/06/2016.
 */
public class MyService extends Service {

    private int mInterval = 5 * 1000; // Começa com 1 minutos
    private Handler mHandlerMarks,mHandlerInvitations;
    private Runnable mStatusCheckerMarks= null,mStatusCheckerInvitations = null;
    DataBase db;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // START YOUR TASKS
        db = new DataBase(this);
        mHandlerMarks = new Handler();
        mHandlerInvitations = new Handler();
        mStatusCheckerMarks = new Runnable() {
            @Override
            public void run() {
                try {
                    new MarksAsync(db,getApplicationContext()).setRepeatingAsyncTask();
                    //this function can change value of mInterval.
                } finally {
                    mInterval *= 1.3;
                    if(mInterval >= 1800*1000){
                        mInterval = 1800 * 1000;
                    }
                    Log.i("TEMPO",mInterval/60+"");
                    mHandlerMarks.postDelayed(mStatusCheckerMarks, mInterval);
                }
            }
        };

        mStatusCheckerInvitations = new Runnable() {
            @Override
            public void run() {
                try {
                    new UpdateAsync(db,getApplicationContext()).setRepeatingAsyncTask();
                    //this function can change value of mInterval.
                } finally {
                    mHandlerInvitations.postDelayed(mStatusCheckerInvitations, mInterval);
                }
            }
        };

        runTasks();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void runTasks(){
        mStatusCheckerMarks.run();
        mStatusCheckerInvitations.run();
    }
}
