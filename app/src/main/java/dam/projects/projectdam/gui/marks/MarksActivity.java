package dam.projects.projectdam.gui.marks;

import android.app.Fragment;
import android.app.ProgressDialog;
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

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.siupt.JResultUPT;
import dam.projects.projectdam.json.siupt.grades.JGradeUPT;
import dam.projects.projectdam.json.siupt.grades.JGradeResultUPT;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.network.SIUPTinfo;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.Course;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by Diogo on 08/04/2016 : 22:35.
 */
public class MarksActivity extends Fragment {

    View marksView;

    private boolean novoPedido,isLaunch;

    private ProgressDialog progress;

    private AcademicYear academicYear;
    private int semester;
    private int period,situation;

    Spinner spinnerMarksSemester;
    Spinner spinnerMarksYear;
    Spinner spinnerMarksPeriod;
    Spinner spinnerMarksSituation;

    private ExpandListAdapterActivity ExpAdapter;
    private ArrayList<Course> ExpListItems;
    private ExpandableListView ExpandList;

    private DataBase db;

    /*
       By Diogo
       Last change: 2016-04-09
       View object named as "marksView" was initiated with the marks_layout to show content
       @return marksView
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isLaunch = true;
        semester=getSemester();
        novoPedido=true;
        period=0;
        situation=0;

        marksView = inflater.inflate(R.layout.marks_layout, container, false);

        spinnerMarksSemester = (Spinner) marksView.findViewById(R.id.spinner_marks_semester);
        spinnerMarksYear = (Spinner) marksView.findViewById(R.id.spinner_marks_year);
        spinnerMarksPeriod = (Spinner) marksView.findViewById(R.id.spinner_marks_period);
        spinnerMarksSituation = (Spinner) marksView.findViewById(R.id.spinner_marks_situation);

        ArrayAdapter adapterSemesters = ArrayAdapter.createFromResource(marksView.getContext(), R.array.marks_semesters, android.R.layout.simple_spinner_item);
        ArrayAdapter adapterYears = new ArrayAdapter<AcademicYear>(marksView.getContext(),android.R.layout.simple_spinner_item, getAcademicYears());
        ArrayAdapter adapterPeriods = ArrayAdapter.createFromResource(marksView.getContext(), R.array.marks_periods, android.R.layout.simple_spinner_item);
        ArrayAdapter adapterSituations = ArrayAdapter.createFromResource(marksView.getContext(), R.array.marks_situations, android.R.layout.simple_spinner_item);

        spinnerMarksSemester.setAdapter(adapterSemesters);
        spinnerMarksYear.setAdapter(adapterYears);
        spinnerMarksPeriod.setAdapter(adapterPeriods);
        spinnerMarksSituation.setAdapter(adapterSituations);

        spinnerMarksSemester.setSelection(semester-1);

        ExpandList = (ExpandableListView) marksView.findViewById(R.id.expandableListView2);

        spinnerMarksSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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

        spinnerMarksYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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

        spinnerMarksPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if(period!=0){
                            novoPedido=true;
                            period=0;
                        }
                        break;
                    case 1:
                        if(period!=1){
                            novoPedido=true;
                            period=1;
                        }
                        break;
                    case 2:
                        if(period!=2){
                            novoPedido=true;
                            period=2;
                        }
                        break;
                    case 3:
                        if(period!=3){
                            novoPedido=true;
                            period=3;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMarksSituation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if(situation!=0){
                            novoPedido=true;
                            situation=0;
                        }
                    case 1:
                        if(situation!=1){
                            novoPedido=true;
                            situation=1;
                        }
                        break;
                    case 2:
                        if(situation!=2){
                            novoPedido=true;
                            situation=2;
                        }
                        break;
                    case 3:
                        if(situation!=3){
                            novoPedido=true;
                            situation=3;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (db == null) {
            db = new DataBase(getActivity());
        }

        checkIfUserChangedRequest();
        return marksView;
    }
    private void checkIfUserChangedRequest() {

        final Handler handler = new Handler();
        Timer timer = new Timer();
        //TODO VERIFICAR NOTAS EM PERIODOS E SITUAÇOES
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(novoPedido) {
                            try {
                                if(period==0 && situation==0){
                                    getGrades(academicYear,semester,null,null);
                                }
                                else if(period!=0 && situation==0){
                                    getGrades(academicYear,semester,period,null);
                                }
                                else if(period==0 && situation!=0){
                                    getGrades(academicYear,semester,null,situation);
                                }
                                else {
                                    getGrades(academicYear, semester, period, situation);
                                }
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

    /**
     * era -> época
     */
    public void getGrades(AcademicYear academicYear, int semester, Integer era, Integer state) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String[] params = new String[] {
                SIUPTinfo.API_KEY.key(),
                RequestDetail.SIUPT_GRADE_REQ.getApiMethod(),
                s_id,
                academicYear.toString(),
                String.valueOf(semester),
                era == null ? "null" : era.toString(),
                state == null ? "null" : state.toString(),
        };
        HttpNRequest request = new HttpNRequest(RequestDetail.SIUPT_GRADE_REQ, params);
        new GradeAsync().execute(request);
    }

    private class GradeAsync extends AsyncTask<HttpNRequest, Void, Code> {
        private HttpNRequest request;
        private JSONClass json;
        private String jsonContent;
        private RequestDetail details;

        @Override
        protected void onPreExecute() {
            novoPedido=false;
            isLaunch = false;
            String textWaitDialog = getActivity().getString(R.string.text_wait_dialog);
            progress = ProgressDialog.show(getActivity(), "",
                    textWaitDialog, true);
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
                progress.dismiss();
                return Code.INVALID_STUDENT;
            } catch (SocketTimeoutException out) {
                progress.dismiss();
                return Code.NET_GENERIC_ERR;
            } catch (Exception e) {
                progress.dismiss();
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
                case GET_GRADE_SUC:
                    Grade[] grades = db.getGrades(academicYear,semester);
                    ArrayList<String> nomeCadeiras = new ArrayList<String>();
                    for(Grade grade : grades){
                        if(!nomeCadeiras.contains(grade.getCourseName())){
                            nomeCadeiras.add(grade.getCourseName());
                        }
                    }

                    ArrayList<Course> list = new ArrayList<Course>();
                    ArrayList<Grade> tempList;
                    for (String course_name : nomeCadeiras) {
                        Course gru = new Course();
                        gru.setCourse(course_name);
                        tempList = new ArrayList<>();
                        for (int j = 0; j < grades.length; ++j) {
                            if (grades[j].getCourseName().equals(course_name)) {
                                tempList.add(grades[j]);
                            }
                        }

                        if (tempList.size() != 0) {
                            gru.setItems(tempList);
                            list.add(gru);
                        }
                    }
                    ExpListItems = list;
                    ExpAdapter = new ExpandListAdapterActivity(marksView.getContext(), ExpListItems);
                    ExpandList.setAdapter(ExpAdapter);

                    break;
                case GET_GRADE_NULL:
                    String textImpossibleMarks = getActivity().getString(R.string.text_impossible_marks);
                    Snackbar.make(null,textImpossibleMarks,Snackbar.LENGTH_LONG).show();
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
                    // add to database
                    db.insertGrades(convertGrades(jresult, request));
                    return Code.GET_GRADE_SUC;
                default:
                    return Code.COD_UNHANDLED_EXC;
            }
        }
        private Grade[] convertGrades(JGradeResultUPT json, HttpNRequest request) {
            ArrayList<Grade> list = new ArrayList<>();
            for (JGradeUPT each : json.result) {
                list.add(new Grade(each.pv_data_inicio == null ? null : HelpersDate.stringToDateTimeUPT(each.pv_data_inicio),
                        each.d_nome,
                        each.pvep_nome,
                        each.avi_nome,
                        each.dfm_nome,
                        each.pvn_media,
                        each.pvn_observacao,
                        each.pv_observacao,
                        each.observacao,
                        each.nota,
                        each.est_nome,
                        each.assiduidade,
                        each.est_assiduidade,
                        each.dfm_nota_minima,
                        each.dfm_peso,
                        (request.getParametersDetails()[5].equals("null") ? null : Integer.parseInt(request.getParametersDetails()[5])),
                        (request.getParametersDetails()[6].equals("null") ? null : Integer.parseInt(request.getParametersDetails()[6])),
                        (request.getParametersDetails()[4].equals("null") ? null : Integer.parseInt(request.getParametersDetails()[4])),
                        AcademicYear.toObject(request.getParametersDetails()[3])));
            }
            return list.toArray(new Grade[list.size()]);
        }
    }
}