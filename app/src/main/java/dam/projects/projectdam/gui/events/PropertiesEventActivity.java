package dam.projects.projectdam.gui.events;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.SearchManager;
import android.util.Log;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.IActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.JServerAbstract;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.sqlite.DataBase;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PropertiesEventActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, IActivity {

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    int idEvento;

    EventFinal evento;

    DataBase db;

    static final int REQUEST_ACCOUNT_PICKER = 251;
    static final int REQUEST_AUTHORIZATION = 252;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 253;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 254;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY,CalendarScopes.CALENDAR};

    /*
       By Diogo
       Last change: 2016-05-22
       Altered to set visibility depending on category and some properties of an event
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_event);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView eventName = (TextView) findViewById(R.id.text_event_name);
        TextView eventDesc = (TextView) findViewById(R.id.editText_event_desc);
        TextView eventInitialDate = (TextView) findViewById(R.id.editText_event_initial_date);
        TextView eventInitialHour = (TextView) findViewById(R.id.text_event_initial_hour);
        TextView eventFinalDate = (TextView) findViewById(R.id.editText_event_initial_hour);
        TextView eventFinalHour = (TextView) findViewById(R.id.text_event_final_hour);

        idEvento = getIntent().getIntExtra("EventID",-1);
        db = new DataBase(getApplicationContext());
        if(idEvento>-1){
            evento = db.getEvent(idEvento);
        }

        eventName.setText(evento.getTitle());
        eventDesc.setText(evento.getDescription());
        eventInitialDate.setText(evento.getDateBegin().toString());
        if(evento.getHourBegin()!=null) {
            eventInitialHour.setText(evento.getHourBegin().toString());
            eventFinalDate.setText(evento.getDateEnd().toString());
            eventFinalHour.setText(evento.getHourEnd().toString());
        }
        else{
            eventInitialHour.setText("");
            eventFinalDate.setText("");
            eventFinalHour.setText("");
        }

        /*String eventNameIntent = getIntent().getStringExtra("EventName");
        String eventDescIntent = getIntent().getStringExtra("EventDesc");
        String eventDescIntent = getIntent().getStringExtra("InitialDate");
        String eventDescIntent = getIntent().getStringExtra("InitialHour");
        String eventDescIntent = getIntent().getStringExtra("FinalDate");
        String eventDescIntent = getIntent().getStringExtra("FinalHour");

        eventName.setText(eventNameIntent);
        eventDesc.setText(eventDescIntent);

        if (visibility == public) {
            searchView.setVisibility(View.VISIBLE);
        }
        else {
            searchView.setVisibility(View.INVISIBLE);
        }

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                chkStudent(query.trim());
                return true;
            }
        });*/

        //toogleBtn = true;
        /*toggle = (ToggleButton) findViewById(R.id.btn_fav);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (toogleBtn) setFav(10);
                } else {
                    if (toogleBtn) unsetFav(10);
                }
            }
        });*/

        mProgress = new ProgressDialog(this);
        //studentProgress = new ProgressDialog(this);
        String googleAPICall = getString(R.string.create_event_calendar);
        mProgress.setMessage(googleAPICall);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        db = new DataBase(getActivity());
    }

    /*
       By Diogo
       Last change: 2016-05-23
       Added to add event on calendar
    */
    public void addToCalendarAPI(View button){
        AlertDialog.Builder winChoice = new AlertDialog.Builder(getActivity());

        String titleCalendar = getString(R.string.title_calendar);
        String textCalendar = getString(R.string.text_calendar);
        String posCalendar = getString(R.string.positive_calendar);
        String negCalendar = getString(R.string.negative_calendar);

        winChoice
                .setTitle(titleCalendar)
                .setMessage(textCalendar)
                .setNegativeButton(negCalendar, null)
                .setPositiveButton(posCalendar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface self, int what) {
                        //Adicionar ao calendário
                        getResultsFromApi();
                    }
                }).show();
    }

    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            String networkIncorrect = getString(R.string.network_incorrect);
            Toast.makeText(getApplicationContext(),networkIncorrect,Toast.LENGTH_LONG).show();
        } else {
            new GoogleCalendarRequest(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            String propertiesAppNeed = getString(R.string.properties_app_need);
            EasyPermissions.requestPermissions(
                    this,
                    propertiesAppNeed,
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    String createEventCalendarReq1 = getString(R.string.create_event_calendar_req1);
                    String createEventCalendarReq2 = getString(R.string.create_event_calendar_req2);
                    Toast.makeText(this,createEventCalendarReq1 + "\" +\n" +
                            "                                    \""+createEventCalendarReq2,Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /*
       By Diogo
       Last change: 2016-05-22
    */
    public void okEvent(View button)
    {
        /*Intent intent = new Intent();
        Helpers.changeActivity(CreateEventActivity.this, MenuActivity.class, false, null, null);*/
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    private class GoogleCalendarRequest extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public GoogleCalendarRequest(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("UPT")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                createEvent();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        private boolean createEvent() throws IOException {
            Event event = new Event()
                    .setSummary(evento.getTitle())
                    .setLocation(evento.getLocal())
                    .setDescription(evento.getDescription());

            if(evento.getHourBegin()!=null) {
                DateTime startDateTime = new DateTime(evento.getDateBegin() + "T" + evento.getHourBegin());
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime);
                event.setStart(start);

                DateTime endDateTime = new DateTime(evento.getDateEnd()+"T"+evento.getHourEnd());
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime);
                event.setEnd(end);

                String calendarId = "primary";
                mService.events().insert(calendarId, event).execute();
                return true;
            }
            else{
                DateTime startDateTime = new DateTime(evento.getDateBegin()+"");
                EventDateTime start = new EventDateTime()
                        .setDate(startDateTime);
                event.setStart(start);

                DateTime endDateTime = new DateTime(evento.getDateBegin()+"");
                EventDateTime end = new EventDateTime()
                        .setDate(endDateTime);
                event.setEnd(end);

                String calendarId = "primary";
                mService.events().insert(calendarId, event).execute();
                return true;
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            PropertiesEventActivity.REQUEST_AUTHORIZATION);
                } else {
                    String propertiesErrorOccurred = getString(R.string.properties_error_occurred);
                    Toast.makeText(getApplicationContext(),propertiesErrorOccurred + "\n"
                            + mLastError.getMessage(),Toast.LENGTH_LONG).show();
                }
            } else {
                String propertiesRequestCancel = getString(R.string.properties_request_cancel);
                Toast.makeText(getApplicationContext(),propertiesRequestCancel,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String propertiesEventOnCalendar = getString(R.string.properties_event_on_calendar);
            Toast.makeText(getApplicationContext(),propertiesEventOnCalendar, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * By Renato
     */
    /*public void setFav(int ev_id) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.FAV_EVENT_REQ.getApiMethod(),
                s_id,
                token,
                String.valueOf(ev_id)};
        HttpNRequest request = new HttpNRequest(RequestDetail.FAV_EVENT_REQ, params);
        new PropertiesAsyncTask().execute(request);
    }

    public void unsetFav(int ev_id) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.UNFAV_EVENT_REQ.getApiMethod(),
                s_id,
                token,
                String.valueOf(ev_id)};
        HttpNRequest request = new HttpNRequest(RequestDetail.UNFAV_EVENT_REQ, params);
        new PropertiesAsyncTask().execute(request);
    }

    private class PropertiesAsyncTask extends AsyncTask<HttpNRequest, Void, Code> {
        private HttpNRequest request;
        private JSONClass json;
        private RequestDetail details;


        @Override
        protected Code doInBackground(HttpNRequest... params) {
            request = params[0];
            details = params[0].getConnectionDetails();
            try {
                if (!Helpers.checkInternetConnection(getActivity())) {
                    return Code.NET_GENERIC_ERR;
                }

                // create http connection
                HttpURLConnection conn = Helpers.createConnection(params[0]);

                conn.connect();

                Code replayCode = Helpers.checkConnectionSate(conn);
                if (replayCode != Code.NET_SUCCESS_COD) {
                    return replayCode;
                }

                json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JServerAbstract.class);

                return operation(details, json, null);
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
            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), code, true);
            if (shown) return;
            String title = "";
            String msg = "";
            // region POST NETWORK NOTIFICATION
            switch (code) {
                case FAV_EVENT_ERR:
                    String titlePropertiesError1 = getString(R.string.title_properties_error_1);
                    String textPropertiesError1 = getString(R.string.text_properties_error_1);
                    title = titlePropertiesError1;
                    msg = textPropertiesError1;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    toogleBtn = false;
                    toggle.setChecked(false);
                    toogleBtn = true;
                    break;
                case UNFAV_EVENT_ERR:
                    String titlePropertiesError2 = getString(R.string.title_properties_error_2);
                    String textPropertiesError2 = getString(R.string.text_properties_error_2);
                    title = titlePropertiesError2
                    msg = textPropertiesError2;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    toogleBtn = false;
                    toggle.setChecked(false);
                    toogleBtn = true;
                    break;
                case RATE_EVENT_ERR:
                    String titlePropertiesError3 = getString(R.string.title_properties_error_3);
                    String textPropertiesError3 = getString(R.string.text_properties_error_3);
                    title = titlePropertiesError3;
                    msg = textPropertiesError3;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
            }
            // endregion
        }

        private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
            JServerAbstract jserver;
            switch (req) {
                case FAV_EVENT_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.FAV_EVENT_ERR;
                    if (jserver.state == 4 || jserver.state == 5) return Code.FAV_EVENT_ERR;
                    return Code.FAV_EVENT_SUC;
                case UNFAV_EVENT_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.UNFAV_EVENT_ERR;
                    if (jserver.state == 4 || jserver.state == 5) return Code.UNFAV_EVENT_ERR;
                    return Code.UNFAV_EVENT_SUC;
                case RATE_EVENT_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.RATE_EVENT_ERR;
                    if (jserver.state == 4 || jserver.state == 5) return Code.RATE_EVENT_ERR;
                    return Code.RATE_EVENT_SUC;
                case UNRATE_EVENT_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.UNRATE_EVENT_ERR;
                    if (jserver.state == 4 || jserver.state == 5) return Code.UNRATE_EVENT_ERR;
                    return Code.UNRATE_EVENT_SUC;
                case CHK_STUDENT_REQ:
                    break;
                default:
                    return Code.COD_UNHANDLED_EXC;
            }
            return null;
        }
    }*/
}
