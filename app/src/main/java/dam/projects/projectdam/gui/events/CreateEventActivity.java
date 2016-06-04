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
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.IActivity;
import dam.projects.projectdam.gui.MenuActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.JServerAbstract;
import dam.projects.projectdam.json.server.event.JResultEvent;

import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.objects.EventType;
import dam.projects.projectdam.objects.Friend;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.objects.VisibilityType;
import dam.projects.projectdam.sqlite.DataBase;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateEventActivity extends AppCompatActivity implements IActivity, EasyPermissions.PermissionCallbacks{

    private EventFinal evento;
    private DataBase db;

    //Spinner categories;
    Spinner dates;
   // Spinner visibilities;

    private EventType eventType;
    private VisibilityType visibilityType;

    GoogleAccountCredential mCredential;
    private ProgressDialog mProgress;
    static final int REQUEST_ACCOUNT_PICKER = 251;
    static final int REQUEST_AUTHORIZATION = 252;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 253;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 254;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY,CalendarScopes.CALENDAR};

    /*
       By Diogo
       Last change: 2016-05-21
       Altered to manage dates spinner
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //categories = (Spinner) findViewById(R.id.spin_categories);
        dates = (Spinner) findViewById(R.id.spin_dates);
        //visibilities = (Spinner) findViewById(R.id.spin_visibilities);

        //ArrayAdapter adapterCategories = ArrayAdapter.createFromResource(this, R.array.event_categories, android.R.layout.simple_spinner_item);
        ArrayAdapter adapterDates = ArrayAdapter.createFromResource(this, R.array.event_dates, android.R.layout.simple_spinner_item);
        //ArrayAdapter adapterVisibilities = ArrayAdapter.createFromResource(this, R.array.event_visibilities, android.R.layout.simple_spinner_item);

       // categories.setAdapter(adapterCategories);
        dates.setAdapter(adapterDates);
        //visibilities.setAdapter(adapterVisibilities);

        final TextView label_final_date = (TextView) findViewById(R.id.textView6);
        final EditText text_final_date = (EditText) findViewById(R.id.editText4);
        final TextView label_initial_hour = (TextView) findViewById(R.id.textView7);
        final EditText text_initial_hour = (EditText) findViewById(R.id.editText5);
        final TextView label_final_hour = (TextView) findViewById(R.id.textView8);
        final EditText text_final_hour = (EditText) findViewById(R.id.editText6);

        dates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        label_final_date.setVisibility(View.INVISIBLE);
                        text_final_date.setVisibility(View.INVISIBLE);
                        label_initial_hour.setVisibility(View.INVISIBLE);
                        text_initial_hour.setVisibility(View.INVISIBLE);
                        label_final_hour.setVisibility(View.INVISIBLE);
                        text_final_hour.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        label_final_date.setVisibility(View.VISIBLE);
                        text_final_date.setVisibility(View.VISIBLE);
                        label_initial_hour.setVisibility(View.VISIBLE);
                        text_initial_hour.setVisibility(View.VISIBLE);
                        label_final_hour.setVisibility(View.VISIBLE);
                        text_final_hour.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mProgress = new ProgressDialog(this);
        String createEventCalendar = getString(R.string.create_event_calendar);
        mProgress.setMessage(createEventCalendar);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        eventType = null;
        visibilityType = null;
        evento = null;

        db = new DataBase(getActivity());
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void createEvent(View Button){
        evento = checkInformation();

        if(evento!=null) {
            addEvent(evento);
            clearCampos();
        }
        else{
            String textEvent = getString(R.string.impossible_event);
            Toast.makeText(getApplicationContext(), textEvent, Toast.LENGTH_LONG).show();
        }

    }

    public void callAPI() {
        AlertDialog.Builder winChoice = new AlertDialog.Builder(getActivity());
        String titleCalendar = getString(R.string.title_calendar);
        String textCalendar = getString(R.string.text_calendar);
        String posCalendar = getString(R.string.positive_calendar);
        String negCalendar = getString(R.string.negative_calendar);
        winChoice
                .setTitle(titleCalendar)
                .setMessage(textCalendar)
                .setNegativeButton(negCalendar, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(posCalendar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface self, int what) {
                        //Adicionar ao calendário
                        getResultsFromApi();
                    }
                }).show();
    }

    public void getCategoryFromUser(View button){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.event_category)
                .setItems(R.array.event_categories, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EventType[] aux = Helpers.getEventTypes(getActivity());
                        eventType = aux[which];
                        Button temp = (Button) findViewById(R.id.button);
                        temp.setText(Helpers.getEventTypeString(getActivity(), eventType.getType_code()));
                    }
                });
        builder.show();
    }

    public void getVisibilityFromUser(final View button){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.event_visibility)
                .setItems(R.array.event_visibilities, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        VisibilityType[] aux = Helpers.getVisibilityTypes(getActivity());
                        visibilityType = aux[which];
                        Button temp = (Button) findViewById(R.id.button2);
                        temp.setText(Helpers.getVisibilityCodeString(getActivity(), visibilityType.getVisibility_code()));
                    }
                });
        builder.show();
    }

    private EventFinal checkInformation() {
        EditText titleET = (EditText) findViewById(R.id.editText1);
        String title = titleET.getText().toString();
        EditText descriptionET =(EditText) findViewById(R.id.editText2);
        String description = descriptionET.getText().toString();
        EditText localET = (EditText) findViewById(R.id.editText7);
        String local = localET.getText().toString();
        Spinner isAllDayS = (Spinner) findViewById(R.id.spin_dates);
        String isAllDay = isAllDayS.getSelectedItem().toString();

        LocalDate auxDateInicial = null,auxDateFinal = null;
        LocalTime auxTimeInicial = null,auxTimeFinal = null;

        if(eventType == null || visibilityType == null){
            String textTypes = getString(R.string.choose_types);
            Toast.makeText(getApplicationContext(), textTypes, Toast.LENGTH_LONG).show();
            return null;
        }

        if (title.length()== 0 || title.length() > 200) {
            String titleWrong = getString(R.string.wrong_title);
            Toast.makeText(getApplicationContext(), titleWrong, Toast.LENGTH_LONG).show();
            return null;
        } else if (description.length()==0 || description.length() > 2000) {
            String descriptionWrong = getString(R.string.wrong_description);
            Toast.makeText(getApplicationContext(), descriptionWrong, Toast.LENGTH_LONG).show();
            return null;
        } else if (local.length()==0 || local.length() > 200) {
            String localWrong = getString(R.string.wrong_local);
            Toast.makeText(getApplicationContext(), localWrong, Toast.LENGTH_LONG).show();
            return null;
        }
        //Verificar data
        EditText et = (EditText) findViewById(R.id.editText3);
        String[] temp = et.getText().toString().split("-");
        boolean certo = true;
        LocalDateTime ldt = new LocalDateTime();
        if (temp.length == 3) {
            if (temp[0].length() != 4 && ldt.getYear() < Integer.parseInt(temp[0])) {
                certo = false;
            } else if (temp[1].length() != 2 && Integer.parseInt(temp[1]) > 12 && Integer.parseInt(temp[1]) < 1) {
                certo = false;
            } else if (temp[2].length() != 2 && Integer.parseInt(temp[2]) > 31 && Integer.parseInt(temp[0]) < 1) {
                certo = false;
            } else if (Integer.parseInt(temp[1]) == 2 && Integer.parseInt(temp[2]) > 29) {
                certo = false;
            }

            if (!certo) {
                return null;
            }
        } else {
            return null;
        }
        auxDateInicial = new LocalDate(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
        if(!(isAllDay.equals(getString(R.string.event_dat_1)))) {
            et = (EditText) findViewById(R.id.editText4);
            temp = et.getText().toString().split("-");
            certo = true;
            ldt = new LocalDateTime();
            if (temp.length == 3) {
                if (temp[0].length() != 4 && ldt.getYear() < Integer.parseInt(temp[0])) {
                    certo = false;
                } else if (temp[1].length() != 2 && Integer.parseInt(temp[1]) > 12 && Integer.parseInt(temp[1]) < 1) {
                    certo = false;
                } else if (temp[2].length() != 2 && Integer.parseInt(temp[2]) > 31 && Integer.parseInt(temp[0]) < 1) {
                    certo = false;
                } else if (Integer.parseInt(temp[1]) == 2 && Integer.parseInt(temp[2]) > 29) {
                    certo = false;
                }

                if (!certo) {
                    return null;
                }
            } else {
                return null;
            }
            auxDateFinal = new LocalDate(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
            et = (EditText) findViewById(R.id.editText5);
            temp = et.getText().toString().split(":");
            certo = true;
            if (temp.length == 2) {
                if (temp[0].length() > 2 && temp[0].length() < 1 && Integer.parseInt(temp[0]) > 23 && Integer.parseInt(temp[0]) < 1) {
                    certo = false;
                } else if (temp[1].length() != 2 && Integer.parseInt(temp[1]) > 59 && Integer.parseInt(temp[0]) < 0) {
                    certo = false;
                }
            } else {
                return null;
            }
            if (!certo) {
                return null;
            }
            auxTimeInicial = new LocalTime(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            et = (EditText) findViewById(R.id.editText6);
            temp = et.getText().toString().split(":");
            certo = true;
            if (temp.length == 2) {
                if (temp[0].length() > 2 && temp[0].length() < 1 && Integer.parseInt(temp[0]) > 23 && Integer.parseInt(temp[0]) < 1) {
                    certo = false;
                } else if (temp[1].length() != 2 && Integer.parseInt(temp[1]) > 59 && Integer.parseInt(temp[0]) < 0) {
                    certo = false;
                }
            } else {
                return null;
            }
            if (!certo) {
                return null;
            }
            else{
                auxTimeFinal = new LocalTime(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            }
        }

        if(auxDateFinal!=null && auxDateFinal.isBefore(auxDateInicial)){
            return null;
        }

        if(auxDateFinal!=null && auxTimeFinal!=null && (auxDateFinal.isAfter(auxDateInicial) ||  auxDateFinal.isEqual(auxDateInicial))&& auxTimeFinal.isBefore(auxTimeInicial)){
            return null;
        }


        if(isAllDay.equals("Data e hora")){
            return new EventFinal(eventType,visibilityType,title,description,local,auxDateInicial,auxDateFinal,auxTimeInicial,auxTimeFinal);
        }
        else{
            return  new EventFinal(eventType,visibilityType,title,description,local,auxDateInicial,null,null,null);
        }
    }

    public void getResultsFromApi() {
            if (!isGooglePlayServicesAvailable()) {
                acquireGooglePlayServices();
            } else if (mCredential.getSelectedAccountName() == null) {
                chooseAccount();
            } else if (!isDeviceOnline()) {
                String networkConnect = getString(R.string.network_connect);
                Toast.makeText(getApplicationContext(), networkConnect, Toast.LENGTH_LONG).show();
            } else {
                new GoogleCalendarRequest(mCredential,evento).execute();
            }
       /* }
        catch(Exception e){
            Log.i("i",e.getMessage());
        }*/
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
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        Log.i("i",requestCode+"");
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    String createEventCalendarReq1 = getString(R.string.create_event_calendar_req1);
                    String createEventCalendarReq2 = getString(R.string.create_event_calendar_req2);
                    Toast.makeText(this,createEventCalendarReq1 + " \" +\n" +
                            "                                    \"" + createEventCalendarReq2,Toast.LENGTH_LONG).show();
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
        Log.i("i",requestCode+"");
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void clearCampos(){
        EditText titleET = (EditText) findViewById(R.id.editText1);
        titleET.setText("");
        EditText descriptionET =(EditText) findViewById(R.id.editText2);
        descriptionET.setText("");
        EditText localET = (EditText) findViewById(R.id.editText7);
        localET.setText("");
        EditText et = (EditText) findViewById(R.id.editText3);
        et.setText("");
        et = (EditText) findViewById(R.id.editText4);
        et.setText("");
        et = (EditText) findViewById(R.id.editText5);
        et.setText("");
        et = (EditText) findViewById(R.id.editText6);
        et.setText("");
    }

    private class GoogleCalendarRequest extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public GoogleCalendarRequest(GoogleAccountCredential credential, EventFinal evento) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("UPT")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                createEvent(evento);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        private void createEvent(EventFinal evento) throws IOException {
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
                    Toast.makeText(getApplicationContext(),"The following error occurred:\n"
                            + mLastError.getMessage(),Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Request cancelled.",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String eventAdded = getString(R.string.event_added);
            Toast.makeText(getApplicationContext(),eventAdded, Toast.LENGTH_LONG).show();
        }
    }

    public void addEvent(EventFinal event) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {
                RequestDetail.ADD_EVENT_REQ.getApiMethod(),
                s_id,
                token,
                String.valueOf(event.getEventType().getType_id()),
                String.valueOf(event.getVisibilityType().getVisibility_id()),
                event.getTitle(),
                event.getDescription(),
                event.getDateBegin()!=null?HelpersDate.dateToString(event.getDateBegin(), HelpersDate.DATE_FORMAT):null,
                event.getHourBegin()!=null?HelpersDate.hourToString(event.getHourBegin(), HelpersDate.HOUR_FORMAT_BIG):null,
                event.getDateEnd()!=null?HelpersDate.dateToString(event.getDateEnd(), HelpersDate.DATE_FORMAT):null,
                event.getHourEnd()!=null?HelpersDate.hourToString(event.getHourEnd(), HelpersDate.HOUR_FORMAT_BIG):null,
                event.getLocal()

        };

        HttpNRequest request = new HttpNRequest(RequestDetail.ADD_EVENT_REQ, params);
        new EventsAsync().execute(request);
    }

    private class EventsAsync extends AsyncTask<HttpNRequest, Void, Code> {
        private HttpNRequest request;
        private JSONClass json;
        private String jsonContent;
        private RequestDetail details;

        @Override
        protected void onPreExecute() {
            String createEventWait = getString(R.string.text_wait_dialog);
            mProgress = ProgressDialog.show(getActivity(), "",
                    createEventWait, true);
        }

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

                jsonContent = Helpers.getConnectionContent(conn.getInputStream())[0];
                json = Helpers.jsonToObject(jsonContent, JServerAbstract.class);


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
            // region PRE NETWORK ACTIONS
            mProgress.dismiss();
            // endregion

            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), code, true);
            if (shown) return;

            // region POST NETWORK NOTIFICATION
            switch (code) {
                case ADD_EVENT_SUC:
                    String titleCreateSuccess = getString(R.string.title_create_success);
                    String textCreateSuccess = getString(R.string.text_create_success);
                    String titleAddSuc = titleCreateSuccess;
                    String textAddSuc =  textCreateSuccess;
                    callAPI();
                    Helpers.showMsgBox(getActivity(), titleAddSuc, textAddSuc);
                    break;
                case ADD_EVENT_ERR:
                    String titleCreateError1 = getString(R.string.title_create_error1);
                    String textCreateError1 = getString(R.string.text_create_error1);
                    String titleAddErr = titleCreateError1;
                    String textAddErr =  textCreateError1;
                    Helpers.showMsgBox(getActivity(), titleAddErr, textAddErr);
                    break;
                case ADD_EVENT_TYPE_VISIB_ERR:
                    String titleCreateError2 = getString(R.string.title_create_error2);
                    String textCreateError2 = getString(R.string.text_create_error2);
                    String titleTypeErr = titleCreateError2;
                    String textTypeErr =  textCreateError2;
                    Helpers.showMsgBox(getActivity(), titleTypeErr, textTypeErr);
                    break;
                case ADD_EVENT_DATE_INV_ERR:
                    String titleCreateError3 = getString(R.string.title_create_error3);
                    String textCreateError3 = getString(R.string.text_create_error3);
                    String titleDateErr = titleCreateError3;
                    String textDateErr =  textCreateError3;
                    Helpers.showMsgBox(getActivity(), titleDateErr, textDateErr);
                    break;
            }
            // endregion
        }

        private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
            JServerAbstract jresult;
            switch (req) {
                case ADD_EVENT_REQ:
                    jresult = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jresult)) return Code.ADD_EVENT_ERR;
                    // extra conditions
                    if (jresult.state == 3) return Code.ADD_EVENT_TYPE_VISIB_ERR;
                    if (jresult.state == 4) return Code.ADD_EVENT_DATE_INV_ERR;
                    return Code.ADD_EVENT_SUC;
                default:
                    return Code.COD_UNHANDLED_EXC;
            }
        }
    }

    /*switch (code) {
                case GET_EVENTS_PUB_SUC:
                    break;
                /*case GET_SCHEDULE_NULL:
                    String titleImpossibleOpenEvents = getActivity().getString(R.string.title_impossible_events);
                    String textImpossibleOpenEvents = getActivity().getString(R.string.text_impossible_events);
                    Snackbar.make(getActivity().getCurrentFocus(),titleImpossibleOpenEvents,Snackbar.LENGTH_INDEFINITE)
                            .setAction(textImpossibleOpenEvents, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                    break;
                case GET_EVENTS_PUB_ERR:
                    break;
            }*/
}
