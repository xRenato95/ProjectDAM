package dam.projects.projectdam.gui.events;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.JServerAbstract;
import dam.projects.projectdam.json.server.event.member.JMember;
import dam.projects.projectdam.json.server.event.member.JResultMember;
import dam.projects.projectdam.json.server.event.member.invite.JMemberInv;
import dam.projects.projectdam.json.server.event.member.invite.JResultMemberInv;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.objects.EventType;

import dam.projects.projectdam.objects.Member;
import dam.projects.projectdam.objects.MemberInvite;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.objects.VisibilityType;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by Renato on 01/06/2016 : 22:17.
 */
public class MemberAsync {
    private Activity activity;
    private DataBase db;
    private Member[] getMembersResult = null;

    public MemberAsync(Activity activity) {
        this.activity = activity;
        db = new DataBase(activity);
        getInvitations();

    }

    public Activity getActivity() {
        return activity;
    }

    public Member[] getMembersResult() {
        return getMembersResult;
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
                    getMembersResult =  db.getMembers(Integer.parseInt(request.getParametersDetails()[3]));
                    break;
                case GET_MEMBERS_ERR:
                    String titleMemberError1 = getActivity().getString(R.string.title_member_error1);
                    String textMemberError1 = getActivity().getString(R.string.text_member_error1);
                    title = titleMemberError1;
                    msg = textMemberError1;
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
                    String titleMemberError2 = getActivity().getString(R.string.title_member_error2);
                    String textMemberError2 = getActivity().getString(R.string.text_member_error2);
                    title = titleMemberError2;
                    msg = textMemberError2;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ACP_MEMBER_SUC:

                    break;
                case ACP_MEMBER_ERR:
                    String titleMemberError3 = getActivity().getString(R.string.title_member_error3);
                    String textMemberError3 = getActivity().getString(R.string.text_member_error3);
                    title = titleMemberError3;
                    msg = textMemberError3;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case RMV_MEMBER_SUC:
                    String titleMemberSuccess1 = getActivity().getString(R.string.title_member_success1);
                    String textMemberSuccess1 = getActivity().getString(R.string.text_member_success1);
                    title = titleMemberSuccess1;
                    msg = textMemberSuccess1;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case RMV_MEMBER_ERR:
                    String titleMemberError4 = getActivity().getString(R.string.title_member_error4);
                    String textMemberError4 = getActivity().getString(R.string.text_member_error4);
                    title = titleMemberError4;
                    msg = textMemberError4;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_SAME_USER:
                    String titleMemberError5 = getActivity().getString(R.string.title_member_error5);
                    String textMemberError5 = getActivity().getString(R.string.text_member_error5);
                    title = titleMemberError5;
                    msg = textMemberError5;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_NO_EVENT:
                    String titleMemberError6 = getActivity().getString(R.string.title_member_error6);
                    String textMemberError6 = getActivity().getString(R.string.text_member_error6);
                    title = titleMemberError6;
                    msg = textMemberError6;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_ALREADY_MEMBER:
                    String titleMemberError7 = getActivity().getString(R.string.title_member_error7);
                    String textMemberError7 = getActivity().getString(R.string.text_member_error7);
                    title = titleMemberError7;
                    msg = textMemberError7;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_NOT_ALLOW:
                    String titleMemberError8 = getActivity().getString(R.string.title_member_error8);
                    String textMemberError8 = getActivity().getString(R.string.text_member_error8);
                    title = titleMemberError8;
                    msg = textMemberError8;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_NOT_FRIEND:
                    String titleMemberError9 = getActivity().getString(R.string.title_member_error9);
                    String textMemberError9 = getActivity().getString(R.string.text_member_error9);
                    title = titleMemberError9;
                    msg = textMemberError9;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_ERR:
                    String titleMemberError10 = getActivity().getString(R.string.title_member_error10);
                    String textMemberError10 = getActivity().getString(R.string.text_member_error10);
                    title = titleMemberError10;
                    msg = textMemberError10;
                    Helpers.showMsgBox(getActivity(), title, msg);
                    break;
                case ADD_MEMBER_SUC:
                    String titleMemberSuccess2 = getActivity().getString(R.string.title_member_success2);
                    String textMemberSuccess2 = getActivity().getString(R.string.text_member_success2);
                    title = titleMemberSuccess2;
                    msg = textMemberSuccess2;
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
                    db.insertMembers(convertMembers(jmember));
                    return Code.GET_MEMBERS_SUC;
                case GET_MEM_INV_REQ:
                    jmemberinv = (JResultMemberInv) json;
                    if (!Helpers.checkJsonObject(jmemberinv)) return Code.GET_MEM_INV_ERR;
                    // add members inv to database
                    db.insertMemberInvs(convertMembersInv(jmemberinv));
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

        private Member[] convertMembers(JResultMember json) {
            ArrayList<Member> list = new ArrayList<>();
            for (JMember each : json.result) {
                list.add(new Member(
                        each.m_id,
                        each.e_id,
                        each.m_rating,
                        new Student(
                                0,
                                each.m_student_number,
                                each.m_student_name,
                                each.m_student_mail,
                                each.m_student_photo
                        ),
                        each.m_favorite == 1
                ));
            }
            return list.toArray(new Member[list.size()]);
        }

        private MemberInvite[] convertMembersInv(JResultMemberInv json) {
            ArrayList<MemberInvite> list = new ArrayList<>();
            for (JMemberInv each : json.result) {
                list.add(new MemberInvite(
                        each.m_id,
                        new Member(
                                each.m_id,
                                each.event_id,
                                each.m_rating,
                                new Student(
                                        0,
                                        each.m_student_number,
                                        each.m_student_name,
                                        each.m_student_mail,
                                        each.m_student_photo
                                ),
                                each.m_favorite == 1),
                        new EventFinal(
                                each.event_id,
                                Integer.parseInt(each.e_admin),
                                new EventType(each.e_type_id, each.e_type_code),
                                new VisibilityType(each.e_visibi_id, each.e_visibi_code),
                                each.e_title,
                                each.e_desc,
                                each.e_local,
                                HelpersDate.stringToDate(each.e_date_begin),
                                each.e_date_end != null ? HelpersDate.stringToDate(each.e_date_end) : null,
                                each.e_hour_begin != null ? HelpersDate.stringToHour(each.e_hour_begin) : null,
                                each.e_hour_end != null ? HelpersDate.stringToHour(each.e_hour_end) : null,
                                HelpersDate.stringToDateTimeUPT(each.e_date_create),
                                each.e_number_members,
                                each.e_average_rating
                        )));
            }
            return list.toArray(new MemberInvite[list.size()]);
        }
    }
}
