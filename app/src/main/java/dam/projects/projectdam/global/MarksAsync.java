package dam.projects.projectdam.global;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.MenuActivity;
import dam.projects.projectdam.gui.marks.MarksActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.siupt.JResultUPT;
import dam.projects.projectdam.json.siupt.grades.JGradeResultUPT;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.network.SIUPTinfo;
import dam.projects.projectdam.notifications.Notification;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by pedro on 30/05/2016.
 */
public class MarksAsync extends AsyncTask<HttpNRequest, Void, Code> {
    private HttpNRequest request;
    private JSONClass json;
    private String jsonContent;
    private RequestDetail details;
    private Notification noti;
    private DataBase db;
    private Context context;

    public MarksAsync(DataBase db,Context context){
        noti = new Notification(context);
        this.db = db;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        noti = new Notification(context);
    }

    @Override
    protected Code doInBackground(HttpNRequest... params) {
        request = params[0];
        details = params[0].getConnectionDetails();
        try {
            // create http connection
            HttpURLConnection conn = Helpers.createConnection(params[0]);

            conn.connect();

            Code replayCode = Helpers.checkConnectionSate(conn);
            if (replayCode != Code.NET_SUCCESS_COD) {
                return replayCode;
            }

            jsonContent = Helpers.getConnectionContent(conn.getInputStream())[0];
            json = Helpers.jsonToObject(jsonContent, JGradeResultUPT.class);

            return operation(details, json, db);
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

    @Override
    protected void onPostExecute(Code code) {
        // region PRE NETWORK ACTIONS

        // endregion
        // region POST NETWORK ACTIONS
        switch (code) {
            case GET_GRADE_SUC:

                break;
            case GET_GRADE_NULL:
                break;
        }

        // endregion
    }

    private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
        JGradeResultUPT jresult;
        switch (req) {
            case SIUPT_GRADE_REQ:
                jresult = (JGradeResultUPT) json;
                if (!Helpers.checkJsonObject(jresult)) {
                    if (!Helpers.jsonToObject(jsonContent, JResultUPT.class).isValid()) return Code.GET_GRADE_NULL;
                    return Code.GET_GRADE_ERR;
                }
                Grade[] newGrades = db.getNewGrades(Helpers.userGrades);
                if(newGrades.length>0) {
                    String notificationUptMarks = context.getString(R.string.notification_upt_marks);
                    noti.createNotification(Code.GRADES_NOTIFICATION.code, notificationUptMarks, newGrades, R.mipmap.photo, MenuActivity.class);
                    return Code.GET_GRADE_SUC;
                }
            default:
                return Code.COD_UNHANDLED_EXC;
        }
    }

    private void getNewGrades(){
        Student student = db.getStudent();
        LocalDateTime ldt = new LocalDateTime();
        int ano = ldt.getYear();
        int mes = ldt.getMonthOfYear();
        int semester;
        AcademicYear academicYear;
        if (mes >= 9) {
            academicYear = new AcademicYear(ano, ano + 1);
            semester = 1;
        } else if (mes < 3) {
            academicYear = new AcademicYear(ano - 1, ano);
            semester = 1;
        } else {
            academicYear = new AcademicYear(ano - 1, ano);
            semester = 2;
        }
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String[] params = new String[]{
                SIUPTinfo.API_KEY.key(),
                RequestDetail.SIUPT_GRADE_REQ.getApiMethod(),
                s_id,
                academicYear.toString(),
                String.valueOf(semester),
                null,
                null
        };
        HttpNRequest request = new HttpNRequest(RequestDetail.SIUPT_GRADE_REQ, params);
        this.execute(request);
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
                            getNewGrades();
                        } catch (Exception e) {
                            // error, do something
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task,new Date(), 300*1000);  // interval of 5 minutes
    }
}
