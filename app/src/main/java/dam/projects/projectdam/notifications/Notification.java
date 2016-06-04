package dam.projects.projectdam.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import dam.projects.projectdam.R;
import dam.projects.projectdam.objects.Friend;
import dam.projects.projectdam.objects.Grade;

public class Notification {

    NotificationCompat.Builder mBuilder;
    Context context;
    int id = 0;
    int[] numID;

    public Notification(Context context){
        numID= new int[30];
        this.context = context;
        mBuilder = new NotificationCompat.Builder(context);
        for(int i=0;i<30;i++){
            numID[i]=1;
        }
    }

    public void createNotification(int id, String title, Object[] message, int icon, Class activityLoad){

        mBuilder        .setSmallIcon(icon)
                        .setContentTitle(title)
                        .setAutoCancel(true);

        String aux = "";
        for(int i=0;i<message.length;i++){
            if(message[i] instanceof Friend) {
                Friend temp = (Friend) message[i];
                aux += temp.getNumber() + " - "+ temp.getName()+"\n";
            }
            else if(message[i] instanceof Grade) {
                Grade temp = (Grade) message[i];
                aux += temp.getCourseName()+" -> " + temp.getGrade()+"\n";
            }
        }

        if(message[0] instanceof Friend) {
            if(message.length>1){
                String notificationNewInvites = context.getString(R.string.notification_new_invites);
                mBuilder.setContentText(message.length+" " + notificationNewInvites);
            }
            else{
                String notificationNewInvite = context.getString(R.string.notification_new_invite);
                mBuilder.setContentText(message.length+" " + notificationNewInvite);
            }
            Intent resultIntent = new Intent(context, activityLoad);
            resultIntent.putExtra("menuFragment","marks");
        }
        else if(message[0] instanceof Grade) {
            if(message.length>1){
                String notificationNewMarks = context.getString(R.string.notification_new_marks);
                mBuilder.setContentText(message.length+" " + notificationNewMarks);
            }
            else{
                String notificationNewMark = context.getString(R.string.notification_new_mark);
                mBuilder.setContentText(message.length+" " + notificationNewMark);
            }
            Intent resultIntent = new Intent(context, activityLoad);
            resultIntent.putExtra("menuFragment","grades");
        }

                        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(aux));

        Intent resultIntent = new Intent(context, activityLoad);
        resultIntent.putExtra("menuFragment","marks");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context,id,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(
                id,
                mBuilder.build());
    }

}