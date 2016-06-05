package dam.projects.projectdam.gui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.global.MarksAsync;
import dam.projects.projectdam.global.UpdateAsync;
import dam.projects.projectdam.gui.events.EventsActivity;
import dam.projects.projectdam.gui.events.MemberAsync;
import dam.projects.projectdam.gui.friends.FriendsActivity;
import dam.projects.projectdam.gui.marks.ExpandListAdapterActivity;
import dam.projects.projectdam.gui.marks.MarksActivity;
import dam.projects.projectdam.gui.schedule.ScheduleActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.JServerAbstract;
import dam.projects.projectdam.json.siupt.JResultUPT;
import dam.projects.projectdam.json.siupt.grades.JGradeResultUPT;
import dam.projects.projectdam.json.siupt.grades.JGradeUPT;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.HttpNResponse;
import dam.projects.projectdam.network.MyService;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.network.SIUPTinfo;
import dam.projects.projectdam.notifications.Notification;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.Course;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.sqlite.DataBase;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IActivity {
    private DataBase db;
    private ProgressDialog progressDialog;

    private int mInterval = 60 * 1000; // Começa com 1 minutos
    private Handler mHandlerMarks,mHandlerInvitations;

    LocalTime temp;

    /*
       By Diogo
       Last change: 2016-05-03 by Renato
       When the user enters the friends activity, does not finishes this activity.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String menuFragment = getIntent().getStringExtra("menuFragment");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (menuFragment != null)
        {
            if (menuFragment.equals("marks")){
                MarksActivity marksFragment = new MarksActivity();
                fragmentTransaction.replace(R.id.content_frame, marksFragment).commit();
            }
            else{
                FriendsActivity friendsFragment = new FriendsActivity();
                fragmentTransaction.replace(R.id.content_frame, friendsFragment).commit();
            }
        }

        // get database object reference
        //Helpers.setUserGrades(getApplicationContext());
        db = new DataBase(getActivity());
        //MarksAsync ma = new MarksAsync(db,getApplicationContext());
       // ma.setRepeatingAsyncTask();
        //UpdateAsync ua = new UpdateAsync(db,getApplicationContext());
        //ua.setRepeatingAsyncTask();
       // mHandlerMarks = new Handler();
        //mHandlerInvitations = new Handler();
        //startRepeatingTaskMarks();
        //startRepeatingTaskInvitations();
        if (!isMyServiceRunning()){
            Intent serviceIntent = new Intent(getApplicationContext(),MyService.class);
            getApplicationContext().startService(serviceIntent);
        }
        else{
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
       By Renato
       Last change: 2016-04-27
       Logout implemented
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logoff)
        {
            // async
            logOutAllStudent();
            return true;
        }

        /*if (id == R.id.action_settings)
        {
            // async
            Helpers.changeActivity(MenuActivity.this, SettingsActivity.class, true, null, null);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*
       By Diogo
       Last change: 2016-05-21 By Diogo
       Added one more option to see Schedule
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_events) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new EventsActivity())
                    .commit();
        } else if (id == R.id.nav_friends) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new FriendsActivity())
                    .commit();
        } else if (id == R.id.nav_marks) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new MarksActivity())
                    .commit();
        } else if (id == R.id.nav_schedule) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new ScheduleActivity())
                    .commit();
        } else if (id == R.id.nav_logoff) {
            // async
            logOutStudent();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    /**
     * By Renato
     * Last Change: 2016-04-26
     * Gathers student and network details required to execute LogOut AsyncTask.
     */
    private void logOutStudent() {
        // get student
        Student student = db.getStudent();
        // setting up http request
        String[] reqParams = new String[] {RequestDetail.LOGOUT_REQUEST.getApiMethod(), student.getId(), student.getToken()};
        HttpNRequest httpRequest = new HttpNRequest(RequestDetail.LOGOUT_REQUEST, reqParams);
        // start!
        new LogOut().execute(httpRequest);
    }

    /**
     * By Renato
     * Last Change: 2016-04-26
     * Gathers student and network details required to execute LogOut  AsyncTask on all logged in devices.
     */
    private void logOutAllStudent() {
        // get student
        Student student = db.getStudent();
        // setting up http request
        String[] reqParams = new String[] {RequestDetail.LOGOUT_ALL_REQ.getApiMethod(), student.getId()};
        HttpNRequest httpRequest = new HttpNRequest(RequestDetail.LOGOUT_ALL_REQ, reqParams);
        // start!
        new LogOut().execute(httpRequest);
    }

    // region Interface Methods
    private void startProgressBar() {
        String titleLogIn = getString(R.string.title_login_in);
        String textLogIn = getString(R.string.text_login_in);
        progressDialog = Helpers.startProgressDialog(getActivity(), titleLogIn, textLogIn);
    }
    private void stopProgressBar() {
        Helpers.stopProgressDialog(progressDialog);
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    // endregion

    // region AsyncTask Class
    /**
     * ----------------------ASYNC TASK--------------------------------------
     * By Renato
     * Last Change: 2016-04-28
     * Improved: Code encapsulation
     */
    private class LogOut extends AsyncTask<HttpNRequest, Void, HttpNResponse> {
        private Code connectionCode;
        @Override
        protected void onPreExecute() {
            // starts progress dialog
            startProgressBar();
        }

        @Override
        protected HttpNResponse doInBackground(HttpNRequest... params) {
            try {
                HttpURLConnection connection = Helpers.createConnection(params[0]);
                // establish connection
                connection.connect();

                // save connection code
                connectionCode = Helpers.checkConnectionSate(connection);
                // check connection reply
                String replyMsg = connection.getResponseMessage();
                int replyStatus = connection.getResponseCode();

                if (connectionCode != Code.NET_SUCCESS_COD) {
                    return new HttpNResponse(replyStatus, replyMsg, new Exception(replyMsg));
                }

                // if the http connection was successful, get the content of the reply
                String[] contents = Helpers.getConnectionContent(connection.getInputStream());
                String content = contents[0];

                // convert to json
                JServerAbstract jResponse = (JServerAbstract) Helpers.jsonToObject(content, JServerAbstract.class);
                if (!jResponse.isValid())
                    return new HttpNResponse(replyStatus, replyMsg, content, null);

                return new HttpNResponse(replyStatus, replyMsg, content, jResponse);
            } catch (SocketTimeoutException e) {
                // without internet!
                connectionCode = Code.NET_GENERIC_ERR;
                return new HttpNResponse(0, null, e);
            } catch (Exception e) {
                connectionCode = Code.COD_UNHANDLED_EXC;
                return new HttpNResponse(0, null, e);
            }
        }

        @Override
        protected void onPostExecute(HttpNResponse httpResponse) {
            // stops progress dialog
            stopProgressBar();
            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), connectionCode, false);
            //if (httpResponse.isRequestSucceed() && httpResponse.getJsonContent() != null) {
            if (!shown)
                Helpers.forceLogout(getActivity());
            //}
        }
    }
    // endregion
}
