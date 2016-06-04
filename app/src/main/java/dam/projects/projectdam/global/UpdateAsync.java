package dam.projects.projectdam.global;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dam.projects.projectdam.R;
import dam.projects.projectdam.gui.MenuActivity;
import dam.projects.projectdam.notifications.Notification;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.friends.FriendsActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.friend.JFriend;
import dam.projects.projectdam.json.server.friend.JResultFriend;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.objects.Friend;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by pedro on 15/05/2016.
 */
public class UpdateAsync extends AsyncTask<HttpNRequest, Void, Code> {
    private JSONClass json;
    private RequestDetail details;
    private DataBase db;
    private Notification noti;
    private Context context;

    public UpdateAsync(DataBase db,Context context){
        noti = new Notification(context);
        this.db = db;
        this.context = context;
    }

    public void getInvitations() {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.GET_FRI_INV_REQ.getApiMethod(),
                s_id,
                token};
        HttpNRequest request = new HttpNRequest(RequestDetail.GET_FRI_INV_REQ, params);
        this.execute(request);
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Code doInBackground(HttpNRequest... params) {
        details = params[0].getConnectionDetails();
        try {
            // create http connection
            HttpURLConnection conn = Helpers.createConnection(params[0]);

            conn.connect();

            Code replayCode = Helpers.checkConnectionSate(conn);
            if (replayCode != Code.NET_SUCCESS_COD) {
                return replayCode;
            }

            if (details == RequestDetail.GET_FRIENDS_REQ || details == RequestDetail.GET_FRI_INV_REQ) {
                json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JResultFriend.class);
            }

            return operation(details, json);
        } catch (InvalidStudentException ex) {
            return Code.INVALID_STUDENT;
        } catch (SocketTimeoutException out) {
            return Code.NET_GENERIC_ERR;
        } catch (Exception e) {
            Log.wtf("Exception", "unhandle");
            e.printStackTrace();
            return Code.COD_UNHANDLED_EXC;
        }
    }

    private Code operation(RequestDetail req, JSONClass json) throws InvalidStudentException {
        JResultFriend jfriend = (JResultFriend) json;
        switch(req)
        {
            case GET_FRI_INV_REQ:
                if (!Helpers.checkJsonObject(jfriend)) return Code.GET_FRI_INV_ERR;

                Friend[] invites = db.getNewInvites(jsonIntoFriends(jfriend, true));

                db.insertFriends(jsonIntoFriends(jfriend, true), true);

                if(invites.length>0) {
                    /*String message = "";
                    for (int i = 0; i < invites.length; i++) {
                        message += invites[i].getName() + " ";
                    }*/
                    String newInvites = context.getString(R.string.notification_new_invites);
                    noti.createNotification(Code.FRIENDS_NOTIFICATION.code, newInvites, invites, R.mipmap.photo, MenuActivity.class);
                    return Code.GET_FRI_INV_SUC;
                }
            default:
                return Code.COD_UNHANDLED_EXC;
        }
    }

    @Override
    protected void onPostExecute(Code code) {
        switch(code) {
            case GET_FRI_INV_SUC:
                break;
        }
    }

    public void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            getInvitations();
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task,new Date(), 300*1000);  // interval of 5 minutes // interval of 5 minutes
    }


    /**
     * By Renato
     * @param json bla
     * @param isInvite bla
     * @return bla
     */
    private Friend[] jsonIntoFriends(JResultFriend json, boolean isInvite) {
        List<JFriend> jFriends = json.result;
        ArrayList<Friend> friends = new ArrayList<>();
        for (JFriend each : jFriends) {
            friends.add(new Friend(each.friend_id, each.friend_name, each.friend_mail,
                    each.friend_photo, null, isInvite));
        }
        return friends.toArray(new Friend[friends.size()]);
    }
}
