package dam.projects.projectdam.gui.schedule;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.DateTime;

import org.joda.time.LocalDate;

import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.IActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.siupt.JResultUPT;
import dam.projects.projectdam.json.siupt.schedule.JScheduleResultUPT;
import dam.projects.projectdam.json.siupt.schedule.JScheduleUPT;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.network.SIUPTinfo;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.ScheduleDay;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.objects.WeekDay;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by Diogo on 21/05/2016 : 01:08.
 */
public class ScheduleActivity extends Fragment {

    View scheduleView;

    private boolean novoPedido,isLaunch;

    private ProgressDialog progress;

    Spinner semesters;
    Spinner years;

    private AcademicYear academicYear;
    private int semester;

    private ExpandListAdapterActivity ExpAdapter;
    private ArrayList<WeekDay> ExpListItems;
    private ExpandableListView ExpandList;

    private DataBase db;

    /*
     By Diogo
     Last change: 2016-05-21
     Altered to implement the expandable list view schedule
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        semester=getSemester();
        novoPedido=true;
        isLaunch = true;

        if (db == null) {
            db = new DataBase(getActivity());
        }

        scheduleView = inflater.inflate(R.layout.schedule_layout, container, false);

        semesters = (Spinner) scheduleView.findViewById(R.id.spin_semesters);
        years = (Spinner) scheduleView.findViewById(R.id.spin_years);

        ArrayAdapter adapterSemesters = ArrayAdapter.createFromResource(scheduleView.getContext(), R.array.schedule_semesters, android.R.layout.simple_spinner_item);
        ArrayAdapter adapterYears = new ArrayAdapter<AcademicYear>(scheduleView.getContext(),android.R.layout.simple_spinner_item, getAcademicYears());

        semesters.setAdapter(adapterSemesters);
        years.setAdapter(adapterYears);

        semesters.setSelection(semester-1);

        ExpandList = (ExpandableListView) scheduleView.findViewById(R.id.scheduleExpandableListView);
        /*ExpListItems = getSchedule(new AcademicYear(2014,2015),1);
        ExpAdapter = new ExpandListAdapterActivity(scheduleView.getContext(), ExpListItems);
        ExpandList.setAdapter(ExpAdapter);*/

        semesters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if(semester!=1 && !isLaunch){
                            novoPedido=true;
                            semester = 1;
                        }
                        break;
                    case 1:
                        if(semester!=2 && !isLaunch) {
                            novoPedido=true;
                            semester = 2;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        years.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(academicYear!=null && academicYear!=getAcademicYears().get(position)){
                    novoPedido=true;
                    academicYear=getAcademicYears().get(position);
                }
                else{
                    novoPedido=true;
                    academicYear=getAcademicYears().get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        checkIfUserChangedRequest();

        return scheduleView;
    }

    private void checkIfUserChangedRequest() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(novoPedido) {
                            try {
                                getSchedule(academicYear, semester);
                            } catch (Exception e) {
                                // error, do something
                            }
                        }
                    }
                });
            }
        };

        timer.schedule(task, 0, 1*1000);  // interval of 1 second
    }

    private ArrayList<AcademicYear> getAcademicYears(){
        ArrayList<AcademicYear> aux = new ArrayList<>();
        LocalDate ld = new LocalDate();
        int anoActual=ld.getYear();
        if(ld.getMonthOfYear()>=9){
            aux.add(new AcademicYear(anoActual,anoActual+1));
        }
        while(anoActual>2012){
            aux.add(new AcademicYear(anoActual-1,anoActual));
            anoActual--;
        }
        return aux;
    }

    private int getSemester(){
        LocalDate ld = new LocalDate();
        if(ld.getMonthOfYear()>9 || ld.getMonthOfYear() <3){
            return 1;
        }
        else{
            return 2;
        }
    }

    public void getSchedule(AcademicYear academicYear, int semester) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String[] params = new String[] {SIUPTinfo.API_KEY.key(),
                RequestDetail.SIUPT_SCHED_REQ.getApiMethod(),
                s_id,
                academicYear.toString(),
                String.valueOf(semester)
        };
        HttpNRequest request = new HttpNRequest(RequestDetail.SIUPT_SCHED_REQ, params);
        new ScheduleAsync().execute(request);
    }

    private class ScheduleAsync extends AsyncTask<HttpNRequest, Void, Code> {
        private HttpNRequest request;
        private JSONClass json;
        private String jsonContent;
        private RequestDetail details;

        @Override
        protected void onPreExecute() {
            novoPedido=false;
            isLaunch = false;
            progress = ProgressDialog.show(getActivity(), "",
                    "Please wait...", true);
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
                try {
                    json = Helpers.jsonToObject(jsonContent, JScheduleResultUPT.class);
                } catch (Exception e) {
                    json = Helpers.jsonToObject(jsonContent, JResultUPT.class);
                }

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
            progress.dismiss();

            // endregion
            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), code, true);
            if (shown) return;
            // region POST NETWORK ACTIONS
            switch (code) {
                case GET_SCHEDULE_SUC:
                    ScheduleDay[] aux = db.getScheduleDays(AcademicYear.toObject(request.getParametersDetails()[3]),
                            Integer.parseInt(request.getParametersDetails()[4]));
                    // update list
                    String groupSunday = getActivity().getString(R.string.group_sunday);
                    String groupMonday = getActivity().getString(R.string.group_monday);
                    String groupTuesday = getActivity().getString(R.string.group_tuesday);
                    String groupWednesday = getActivity().getString(R.string.group_wednesday);
                    String groupThursday = getActivity().getString(R.string.group_thursday);
                    String groupFriday = getActivity().getString(R.string.group_friday);
                    String groupSaturday = getActivity().getString(R.string.group_saturday);
                    String group_names[] = {groupSunday,
                            groupMonday,
                            groupTuesday,
                            groupWednesday,
                            groupThursday,
                            groupFriday,
                            groupSaturday};

                    ArrayList<WeekDay> list = new ArrayList<WeekDay>();
                    ArrayList<ScheduleDay> tempList;
                    int dayOfTheWeek = new LocalDate().getDayOfWeek();
                    int extender = -1;
                    if(dayOfTheWeek==7){
                        dayOfTheWeek=0;
                    }
                    int firstClassDay = aux[0].getDayOfTheWeek();

                    int i = 0;
                    for (String group_name : group_names) {
                        WeekDay gru = new WeekDay();
                        gru.setWeekDay(group_name);
                        tempList = new ArrayList<>();
                        for (int j = 0; j < aux.length; ++j) {
                            if (aux[j].getDayOfTheWeek() == i) {
                                tempList.add(aux[j]);
                                if(dayOfTheWeek==aux[j].getDayOfTheWeek()){
                                    switch(i){
                                        case 0 : extender=0; break;
                                        case 1 : extender=1; break;
                                        case 2 : extender=2; break;
                                        case 3 : extender=3; break;
                                        case 4 : extender=4; break;
                                        case 5 : extender=5; break;
                                        case 6 : extender=6; break;
                                    }
                                }
                            }
                        }

                        if (tempList.size() != 0) {
                            gru.setItems(tempList);
                            list.add(gru);
                        }

                        i++;
                    }
                    if(list.get(0).getItems().get(0).getDayOfTheWeek() == 0){
                        extender+=1;
                    }
                    else if(list.get(0).getItems().get(0).getDayOfTheWeek() == 2){
                        extender-=1;
                    }
                    else if(list.get(0).getItems().get(0).getDayOfTheWeek() == 3){
                        extender-=2;
                    }
                    else if(list.get(0).getItems().get(0).getDayOfTheWeek() == 4){
                        extender-=3;
                    }
                    else if(list.get(0).getItems().get(0).getDayOfTheWeek() == 5){
                        extender-=4;
                    }
                    else if(list.get(0).getItems().get(0).getDayOfTheWeek() == 6){
                        extender-=5;
                    }
                    ExpListItems = list;
                    ExpAdapter = new ExpandListAdapterActivity(scheduleView.getContext(), ExpListItems);
                    ExpandList.setAdapter(ExpAdapter);
                    if(extender!=-1 && firstClassDay==dayOfTheWeek) {
                        ExpandList.expandGroup(extender-1);
                    }
                    break;
                case GET_SCHEDULE_NULL:
                    String titleImpossibleOpenSchedule = getActivity().getString(R.string.title_impossible_schedule);
                    String textImpossibleOpenSchedule = getActivity().getString(R.string.text_impossible_schedule);
                    Snackbar.make(getActivity().getCurrentFocus(),titleImpossibleOpenSchedule,Snackbar.LENGTH_INDEFINITE)
                            .setAction(textImpossibleOpenSchedule, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                    break;
                case GET_SCHEDULE_ERR:
                    break;
            }

            // endregion
        }

        private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
            JScheduleResultUPT jresult;
            switch (req) {
                case SIUPT_SCHED_REQ:
                    if (json instanceof JResultUPT) return Code.GET_SCHEDULE_NULL;
                    jresult = (JScheduleResultUPT) json;
                    if (!jresult.isValid()) return Code.GET_SCHEDULE_ERR;
                    // add to database
                    db.insertScheduleDays(convertSchedule(jresult, request));
                    return Code.GET_SCHEDULE_SUC;
                default:
                    return Code.COD_UNHANDLED_EXC;
            }
        }

        private ScheduleDay[] convertSchedule(JScheduleResultUPT json, HttpNRequest request) {
            ArrayList<ScheduleDay> list = new ArrayList<>();
            for (JScheduleUPT each : json.result) {
                list.add(new ScheduleDay(each.horario_id, each.disciplina_nome,
                        each.tipo_ensino_nome, each.tipo_ensino_abreviatura,
                        each.sala_nome, each.turma_nome,
                        HelpersDate.dateStringToHour(each.horario_hora_inicio),
                        HelpersDate.dateStringToHour(each.horario_hora_fim),
                        each.horario_duracao, (each.horario_dia_semana),
                        AcademicYear.toObject(request.getParametersDetails()[3]), Integer.parseInt(request.getParametersDetails()[4])));
            }
            return list.toArray(new ScheduleDay[list.size()]);
        }
    }
}
