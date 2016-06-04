package dam.projects.projectdam.gui.events;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.IActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDB;
import dam.projects.projectdam.json.server.JServerAbstract;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.sqlite.DataBase;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.event.JEvent;
import dam.projects.projectdam.json.server.event.JResultEvent;
import dam.projects.projectdam.json.server.event.member.JMember;
import dam.projects.projectdam.json.server.event.member.JResultMember;
import dam.projects.projectdam.json.server.event.member.invite.JMemberInv;
import dam.projects.projectdam.json.server.event.member.invite.JResultMemberInv;
import dam.projects.projectdam.json.siupt.JResultUPT;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.network.SIUPTinfo;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.objects.EventType;
import dam.projects.projectdam.objects.Member;
import dam.projects.projectdam.objects.MemberInvite;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.objects.VisibilityType;
import dam.projects.projectdam.sqlite.DataBase;

public class EventsActivity extends Fragment {

    View eventsView;
    private DataBase db;
    private boolean novoPedido;
    private ProgressDialog progress;
    private int actual,posicao;

    private Spinner spinnerEventsVisibilities;

    private ListView eventsListView;
    private ArrayAdapter<EventFinal> listEventsAdapter;

    /*
       By Diogo
       Last change: 2016-05-16
       Altered to set an action when click on image button
       @return eventsView
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        eventsView = inflater.inflate(R.layout.events_layout, container, false);

        db= new DataBase(getActivity());
        novoPedido = true;
        actual = 1;

        spinnerEventsVisibilities = (Spinner) eventsView.findViewById(R.id.spinner_events_visibilities);

        ArrayAdapter adapterEventsVisibilities = ArrayAdapter.createFromResource(getActivity(), R.array.events_visibilities, android.R.layout.simple_spinner_item);

        spinnerEventsVisibilities.setAdapter(adapterEventsVisibilities);

        spinnerEventsVisibilities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if(actual!=1){
                            novoPedido = true;
                            actual = 1;
                        }
                        break;
                    case 1:
                        if(actual!=2){
                            novoPedido = true;
                            actual = 2;
                        }
                        break;
                    case 2:
                        if(actual!=3){
                            novoPedido = true;
                            actual = 3;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        listEventsAdapter = new ArrayAdapter<EventFinal>(getActivity(), R.layout.custom_list_content, R.id.event_main_name) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemNormal = super.getView(position, convertView, parent);
                TextView itemEventName = (TextView) itemNormal.findViewById(R.id.event_main_name);
                TextView itemEventAdmin = (TextView) itemNormal.findViewById(R.id.event_main_admin);
                TextView itemEventDesc = (TextView) itemNormal.findViewById(R.id.event_main_desc);
                TextView itemEventDay = (TextView) itemNormal.findViewById(R.id.event_main_day);
                TextView itemEventMonth = (TextView) itemNormal.findViewById(R.id.event_main_month);
                TextView itemEventYear = (TextView) itemNormal.findViewById(R.id.event_main_year);
                itemEventName.setText(this.getItem(position).getTitle());
                itemEventAdmin.setText(this.getItem(position).getS_id()+"");
                itemEventDesc.setText(this.getItem(position).getDescription());
                itemEventDay.setText(this.getItem(position).getDateBegin().getDayOfMonth()+"");
                itemEventMonth.setText(this.getItem(position).getDateBegin().getMonthOfYear()+"");
                itemEventYear.setText(this.getItem(position).getDateBegin().getYear()+"");
                return itemNormal;
            }
        };

        eventsListView = (ListView) eventsView.findViewById(R.id.listView_events);
        eventsListView.setAdapter(listEventsAdapter);
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                if (Integer.parseInt(db.getStudent().getId())!=listEventsAdapter.getItem(position).getS_id()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("EventID",listEventsAdapter.getItem(position).getEv_id());
                    Helpers.changeActivity(getActivity(), PropertiesEventActivity.class, false, null, bundle);
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("EventID",listEventsAdapter.getItem(position).getEv_id());
                    Helpers.changeActivity(getActivity(), PropertiesEventAdminActivity.class, false, null, bundle);
                }
            }
        });

        eventsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

             @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                 return true;
             }
        });

        FloatingActionButton fab = (FloatingActionButton) eventsView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpers.changeActivity(getActivity(), CreateEventActivity.class, false, null, null);
            }
        });

        checkIfUserChangedRequest();
        return eventsView;
    }

    /**
     * Public
     */
    public void getEventsPub() {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {
                RequestDetail.GET_PUB_EV_REQ.getApiMethod(),
                s_id,
                token
        };
        HttpNRequest request = new HttpNRequest(RequestDetail.GET_PUB_EV_REQ, params);
        new EventsAsync().execute(request);
    }

    /**
     * Private
     */
    public void getEventsPri() {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {
                RequestDetail.GET_PRI_EV_REQ.getApiMethod(),
                s_id,
                token
        };
        HttpNRequest request = new HttpNRequest(RequestDetail.GET_PRI_EV_REQ, params);
        new EventsAsync().execute(request);
    }

    /**
     * Friend
     */
    public void getEventsFri() {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {
                RequestDetail.GET_PRI_EV_REQ.getApiMethod(),
                s_id,
                token
        };
        HttpNRequest request = new HttpNRequest(RequestDetail.GET_PRI_EV_REQ, params);
        new EventsAsync().execute(request);
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
                                switch(actual){
                                    case 1: getEventsPri();
                                        break;
                                    case 2: getEventsFri();
                                        break;
                                    case 3: getEventsPub();
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

    private class EventsAsync extends AsyncTask<HttpNRequest, Void, Code> {
        private HttpNRequest request;
        private JSONClass json;
        private String jsonContent;
        private RequestDetail details;

        @Override
        protected void onPreExecute() {
            novoPedido = false;
            String eventsWait = getString(R.string.text_wait_dialog);
            progress = ProgressDialog.show(getActivity(), "",
                    eventsWait, true);
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
                    json = Helpers.jsonToObject(jsonContent, JResultEvent.class);
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

        //EM FALTA
        @Override
        protected void onPostExecute(Code code) {
            // region PRE NETWORK ACTIONS
            progress.dismiss();
            // endregion

            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), code, true);
            if (shown) return;

            // region POST NETWORK NOTIFICATION
            EventFinal[] events;
            switch (code) {
                case GET_EVENTS_PUB_SUC:
                    events = db.getEvents(3);
                    listEventsAdapter.clear();
                    listEventsAdapter.addAll(events);
                    break;
                case GET_EVENTS_PRI_SUC:
                    events = db.getEvents(1);
                    listEventsAdapter.clear();
                    listEventsAdapter.addAll(events);
                    break;
                case GET_EVENTS_FRI_SUC:
                    events = db.getEvents(2);
                    listEventsAdapter.clear();
                    listEventsAdapter.addAll(events);
                    break;
                default:
                    String eventsRecentImpossible = getString(R.string.events_recent_impossible);
                    String eventsRecentClose = getString(R.string.events_recent_close);
                    Snackbar.make(getActivity().getCurrentFocus(),eventsRecentImpossible,Snackbar.LENGTH_INDEFINITE)
                            .setAction(eventsRecentClose, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                    break;
            }
            // endregion
        }

        private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
            JResultEvent jresult;
            switch (req) {
                case GET_PUB_EV_REQ:
                    jresult = (JResultEvent) json;
                    if (!Helpers.checkJsonObject(jresult)) return Code.GET_EVENTS_PUB_ERR;
                    // add to database
                    db.insertEvents(HelpersDB.convertEvents(jresult));
                    return Code.GET_EVENTS_PUB_SUC;
                case GET_PRI_EV_REQ:
                    jresult = (JResultEvent) json;
                    if (!Helpers.checkJsonObject(jresult)) return Code.GET_EVENTS_PRI_ERR;
                    // add to database
                    db.insertEvents(HelpersDB.convertEvents(jresult));
                    return Code.GET_EVENTS_PRI_SUC;
                case GET_FRI_EV_REQ:
                    jresult = (JResultEvent) json;
                    if (!Helpers.checkJsonObject(jresult)) return Code.GET_EVENTS_FRI_ERR;
                    // add to database
                    db.insertEvents(HelpersDB.convertEvents(jresult));
                    return Code.GET_EVENTS_FRI_SUC;
                default:
                    return Code.COD_UNHANDLED_EXC;
            }
        }
    }

    public class MemberAsync {
        private Activity activity;
        private DataBase db;

        public MemberAsync(Activity activity) {
            this.activity = activity;
            db = new DataBase(activity);
            getInvitations();

        }

        public Activity getActivity() {
            return activity;
        }

        /**
         * By Renato
         */
        public void addMember(int remote_id, String number) {
            Student student = db.getStudent();
            // safety validation
            if (student == null) return;
            String s_id = student.getId();
            String token = student.getToken();
            String[] params = new String[] {RequestDetail.ADD_MEMBER_REQ.getApiMethod(),
                    s_id,
                    token,
                    String.valueOf(remote_id),
                    number};
            HttpNRequest request = new HttpNRequest(RequestDetail.ADD_MEMBER_REQ, params);
            new MemberAsyncTask().execute(request);
        }


        /**
         * By Renato
         */
        public void acpMember(int remote_id, String number) {
            Student student = db.getStudent();
            // safety validation
            if (student == null) return;
            String s_id = student.getId();
            String token = student.getToken();
            String[] params = new String[] {RequestDetail.ACP_MEMBER_REQ.getApiMethod(),
                    s_id,
                    token,
                    String.valueOf(remote_id),
                    number};
            HttpNRequest request = new HttpNRequest(RequestDetail.ACP_MEMBER_REQ, params);
            new MemberAsyncTask().execute(request);
        }

        /**
         * By Renato
         */
        public void getMembers(int remote_id) {
            Student student = db.getStudent();
            // safety validation
            if (student == null) return;
            String s_id = student.getId();
            String token = student.getToken();
            String[] params = new String[] {RequestDetail.GET_MEMBERS_REQ.getApiMethod(),
                    s_id,
                    token,
                    String.valueOf(remote_id)};
            HttpNRequest request = new HttpNRequest(RequestDetail.GET_MEMBERS_REQ, params);
            new MemberAsyncTask().execute(request);
        }

        /**
         * By Renato
         */
        public void getInvitations() {
            Student student = db.getStudent();
            // safety validation
            if (student == null) return;
            String s_id = student.getId();
            String token = student.getToken();
            String[] params = new String[] {RequestDetail.GET_MEM_INV_REQ.getApiMethod(),
                    s_id,
                    token};
            HttpNRequest request = new HttpNRequest(RequestDetail.GET_MEM_INV_REQ, params);
            new MemberAsyncTask().execute(request);
        }

        /**
         * By Renato
         */
        public void rmvMember(int ev_id, String number) {
            Student student = db.getStudent();
            // safety validation
            if (student == null) return;
            String s_id = student.getId();
            String token = student.getToken();
            String[] params = new String[] {RequestDetail.RMV_MEMBER_REQ.getApiMethod(),
                    s_id,
                    token,
                    String.valueOf(ev_id),
                    number};
            HttpNRequest request = new HttpNRequest(RequestDetail.RMV_MEMBER_REQ, params);
            new MemberAsyncTask().execute(request);
        }

        private class MemberAsyncTask extends AsyncTask<HttpNRequest, Void, Code> {
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

                    if (details == RequestDetail.GET_MEMBERS_REQ) {
                        json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JResultMember.class);
                    } else if (details == RequestDetail.GET_MEM_INV_REQ) {
                        json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JResultMemberInv.class);
                    } else {
                        json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JServerAbstract.class);
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
                boolean shown = Helpers.showNetworkDialogErrors(getActivity(), code, true);
                if (shown) return;
                String title = "";
                String msg = "";
                // region POST NETWORK NOTIFICATION
                switch (code) {
                    case GET_MEMBERS_SUC:
                        //updateFriendsList(false);
                        String temp = "";
                        for(Member cada : db.getMembers(Integer.parseInt(request.getParametersDetails()[3]))){
                            temp+=cada.getStudent().getName()+"\n";
                        }
                        String titleGuests = getString(R.string.event_title_guests);
                        String posGuests = getString(R.string.event_btn_ok);
                        AlertDialog.Builder winChoice = new AlertDialog.Builder(getContext());
                        winChoice
                                .setTitle(titleGuests)
                                .setMessage(temp)
                                .setPositiveButton(posGuests, null)
                                .show();
                        break;
                    case GET_MEMBERS_ERR:
                        title = "Ups!";
                        msg = "Não foi possível obter os membros deste evento";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case GET_MEM_INV_SUC:
                        //updateFriendsList(false);
                        MemberInvite[] membersinv =  db.getMemberInvites();
                        for (MemberInvite each: membersinv) {
                            Log.wtf("MEMBROS--INV: ", each.toString());
                        }
                        break;
                    case GET_MEM_INV_ERR:
                        title = "Ups!";
                        msg = "Não foi possível obter os convites deste evento";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ACP_MEMBER_SUC:

                        break;
                    case ACP_MEMBER_ERR:
                        title = "Não foi possível!";
                        msg = "De momento não pode aceitar o convite";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case RMV_MEMBER_SUC:
                        title = "Sucesso!";
                        msg = "Removeu com sucesso";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case RMV_MEMBER_ERR:
                        title = "Sem sucesso...";
                        msg = "De momento não pode remover";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_SAME_USER:
                        title = "Sem sucesso!";
                        msg = "Não pode adicionar adicionar-se a si próprio!";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_NO_EVENT:
                        title = "O evento não existe!";
                        msg = "Feche, abra a aplicação e tente novamente.";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_ALREADY_MEMBER:
                        title = "Sem sucesso!";
                        msg = "Esse membro já se encontra no evento.";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_NOT_ALLOW:
                        title = "Sem sucesso!";
                        msg = "Não pode fazer um convite a esse estudante.";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_NOT_FRIEND:
                        title = "Sem sucesso!";
                        msg = "O estudante tem de ser seu amigo para ser adicionado";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_ERR:
                        title = "Sem sucesso!";
                        msg = "De momento não pode remover";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                    case ADD_MEMBER_SUC:
                        title = "Sucesso!";
                        msg = "O convite foi enviado com sucesso!";
                        Helpers.showMsgBox(getActivity(), title, msg);
                        break;
                }
                // endregion
            }

            private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
                JServerAbstract jserver;
                JResultMember jmember;
                JResultMemberInv jmemberinv;
                switch (req) {
                    case ADD_MEMBER_REQ:
                        jserver = (JServerAbstract) json;
                        if (!Helpers.checkJsonObject(jserver)) return Code.ADD_MEMBER_ERR;
                        // extra conditions
                        if (jserver.state == 3) return Code.ADD_MEMBER_SAME_USER;
                        if (jserver.state == 4) return Code.ADD_MEMBER_NO_EVENT;
                        if (jserver.state == 5) return Code.ADD_MEMBER_NOT_ALLOW;
                        if (jserver.state == 6) return Code.ADD_MEMBER_ALREADY_MEMBER;
                        if (jserver.state == 7) return Code.ADD_MEMBER_NOT_FRIEND;
                        return Code.ADD_MEMBER_SUC;
                    case ACP_MEMBER_REQ:
                        jserver = (JServerAbstract) json;
                        if (!Helpers.checkJsonObject(jserver)) return Code.ACP_MEMBER_ERR;
                        return Code.ACP_MEMBER_SUC;
                    case GET_MEMBERS_REQ:
                        jmember = (JResultMember) json;
                        if (!Helpers.checkJsonObject(jmember)) return Code.GET_MEMBERS_ERR;
                        // add members to database
                        db.insertMembers(HelpersDB.convertMembers(jmember));
                        return Code.GET_MEMBERS_SUC;
                    case GET_MEM_INV_REQ:
                        jmemberinv = (JResultMemberInv) json;
                        if (!Helpers.checkJsonObject(jmemberinv)) return Code.GET_MEM_INV_ERR;
                        // add members inv to database
                        db.insertMemberInvs(HelpersDB.convertMembersInv(jmemberinv));
                        return Code.GET_MEM_INV_SUC;
                    case RMV_MEMBER_REQ:
                        jserver = (JServerAbstract) json;
                        if (!Helpers.checkJsonObject(jserver)) return Code.RMV_MEMBER_SUC;
                        return Code.RMV_MEMBER_ERR;
                    case CHK_STUDENT_REQ:
                        break;
                    default:
                        return Code.COD_UNHANDLED_EXC;
                }
                return null;
            }
        }
    }


    /*private void createAdapter(EventFinal[] events) {
        listEventsAdapter = new ArrayAdapter<EventFinal>(getActivity(), R.layout.custom_list_content, R.id.event_main_name,events) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemNormal = super.getView(position, convertView, parent);
                TextView itemName = (TextView) itemNormal.findViewById(R.id.event_main_name);
                TextView itemAdmin = (TextView) itemNormal.findViewById(R.id.event_main_admin);
                TextView itemDesc = (TextView) itemNormal.findViewById(R.id.event_main_desc);
                TextView itemDay = (TextView) itemNormal.findViewById(R.id.event_main_day);
                TextView itemMonth = (TextView) itemNormal.findViewById(R.id.event_main_month);
                TextView itemYear = (TextView) itemNormal.findViewById(R.id.event_main_year);

                itemName.setText(Helpers.renameFriend(getActivity(), this.getItem(position).getTitle()));
                itemAdmin.setText(this.getItem(position).getS_id());
                itemDesc.setText(Helpers.renameFriend(getActivity(), this.getItem(position).getDescription()));
                //itemDay.setText(Helpers.renameFriend(getActivity(), this.getItem(position).getDateBegin()));
                //itemMonth.setText(Helpers.renameFriend(getActivity(), this.getItem(position).getDateBegin()));
                //itemMonth.setText(Helpers.renameFriend(getActivity(), this.getItem(position).getDateBegin()));
                return itemNormal;
            }
        };
        eventsListView.setAdapter(listEventsAdapter);
    }*/
}