package dam.projects.projectdam.gui.friends;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.IActivity;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDB;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.JServerAbstract;
import dam.projects.projectdam.json.server.friend.JFriend;
import dam.projects.projectdam.json.server.friend.JResultFriend;
import dam.projects.projectdam.json.server.student.JResultStudent;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.objects.Friend;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.sqlite.DataBase;

public class FriendsActivity extends Fragment {

    View friendsView;
    private ArrayAdapter<Friend> listFriendsAdapter;
    private ArrayAdapter<Friend> listInvitationsAdapter;
    private DataBase db;
    private ProgressDialog progress;

    /*
       By Diogo
       Last change: 2016-05-02
       Added code to control the TabHost and to add a new list adapter for invitations
    */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        friendsView = inflater.inflate(R.layout.friends_layout, container, false);

        TabHost tab = (TabHost) friendsView.findViewById(R.id.tabHost);
        tab.setup();

        String textFriends = getString(R.string.tab_friends);
        String textInvitations = getString(R.string.tab_invitations);

        TabHost.TabSpec spec1 = tab.newTabSpec(textFriends);
        spec1.setIndicator(textFriends);
        spec1.setContent(R.id.layout1);
        tab.addTab(spec1);

        TabHost.TabSpec spec2 = tab.newTabSpec(textInvitations);
        spec2.setIndicator(textInvitations);
        spec2.setContent(R.id.layout2);
        tab.addTab(spec2);

        listFriendsAdapter = new ArrayAdapter<Friend>(getActivity(), R.layout.custom_list_friends_content, R.id.friend_name) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemNormal = super.getView(position, convertView, parent);
                ImageView itemImage = (ImageView) itemNormal.findViewById(R.id.friend_image);
                TextView itemName = (TextView) itemNormal.findViewById(R.id.friend_name);
                TextView itemNumber = (TextView) itemNormal.findViewById(R.id.friend_number);
                if(this.getItem(position).getPhoto()!=null) {
                    itemImage.setImageBitmap(this.getItem(position).getPhoto());
                }
                itemName.setText(Helpers.renameFriend(getActivity(), this.getItem(position).getName()));
                itemNumber.setText(this.getItem(position).getNumber());
                return itemNormal;
            }
        };

        listInvitationsAdapter = new ArrayAdapter<Friend>(getActivity(), R.layout.custom_list_friends_content, R.id.friend_name) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View itemNormal = super.getView(position, convertView, parent);
                ImageView itemImage = (ImageView) itemNormal.findViewById(R.id.friend_image);
                TextView itemName = (TextView) itemNormal.findViewById(R.id.friend_name);
                TextView itemNumber = (TextView) itemNormal.findViewById(R.id.friend_number);
                if(this.getItem(position).getPhoto()!=null) {
                    itemImage.setImageBitmap(this.getItem(position).getPhoto());
                }
                itemName.setText(/*Helpers.renameFriend(getActivity(), */this.getItem(position).getName()/*)*/);
                itemNumber.setText(this.getItem(position).getNumber());
                return itemNormal;
            }
        };

        ListView friendsListView = (ListView) friendsView.findViewById(R.id.listview_friends);
        friendsListView.setAdapter(listFriendsAdapter);

        ListView invitationsListView = (ListView) friendsView.findViewById(R.id.listview_invitations);
        invitationsListView.setAdapter(listInvitationsAdapter);

        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder winChoice = new AlertDialog.Builder(parent.getContext());
                final Friend temp = (Friend) parent.getItemAtPosition(position);

                String titleFriDelete = getString(R.string.title_friend_delete);
                String negFriDelete = getString(R.string.negative_friend_delete);
                String posFriDelete = getString(R.string.positive_friend_delete);

                winChoice.setTitle(titleFriDelete)
                        .setMessage(temp.getNumber()+" - "+temp.getName())
                        .setNegativeButton(negFriDelete, null) // FIXME: 05/05/2016 add dimiss dialog
                        .setPositiveButton(posFriDelete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface self, int what) {
                                // start request to delete a friend
                                rmvFriend(temp.getNumber());
                            }
                        })
                        .show();

                return true;
            }
        });

        invitationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder winChoice = new AlertDialog.Builder(parent.getContext());

                String titleFriAccept = getString(R.string.title_friend_accept);
                String negFriAccept = getString(R.string.negative_friend_accept);
                String posFriAccept = getString(R.string.positive_friend_accept);
                String questFriAccept = getString(R.string.question_friend_accept);

                winChoice.setTitle(titleFriAccept)
                        .setMessage(questFriAccept)
                        .setNegativeButton(negFriAccept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // start request to decline a friend request
                                dclFriend(((Friend)parent.getItemAtPosition(position)).getNumber());
                            }
                        })
                        .setPositiveButton(posFriAccept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface self, int what) {
                                // start request to accept a friend
                                acpFriend(((Friend)parent.getItemAtPosition(position)).getNumber());
                            }
                        })
                        .show();
            }
        });

        // search bar
        SearchView searchView = (SearchView) friendsView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                chkStudent(query.trim());
                return true;
            }
        });

        db = new DataBase(getActivity());
        //Log.wtf("APK", "2.3");

        return friendsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateFriendsList(false);
        updateFriendsList(true);

        getFriends();
        getInvitations();
    }

    /**
     * By Renato
     */
    public void addFriend(String number) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.ADD_FRIEND_REQ.getApiMethod(),
                s_id,
                token,
                number};
        HttpNRequest request = new HttpNRequest(RequestDetail.ADD_FRIEND_REQ, params);
        new FriendsAsync().execute(request);
    }
    /**
     * By Renato
     */
    public void chkStudent(String number) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.CHK_STUDENT_REQ.getApiMethod(),
                s_id,
                token,
                number};
        HttpNRequest request = new HttpNRequest(RequestDetail.CHK_STUDENT_REQ, params);
        String textSearch = getString(R.string.text_search);
        progress = Helpers.startProgressDialog(getActivity(), "", textSearch);
        new FriendsAsync().execute(request);
    }

    /**
     * By Renato
     * @param friend_number
     */
    public void acpFriend(String friend_number) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.ACP_FRIEND_REQ.getApiMethod(),
                s_id,
                token,
                friend_number};
        HttpNRequest request = new HttpNRequest(RequestDetail.ACP_FRIEND_REQ, params);
        new FriendsAsync().execute(request);
    }

    /**
     * By Renato
     */
    public void getFriends() {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.GET_FRIENDS_REQ.getApiMethod(),
                s_id,
                token};
        HttpNRequest request = new HttpNRequest(RequestDetail.GET_FRIENDS_REQ, params);
        new FriendsAsync().execute(request);
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
        String[] params = new String[] {RequestDetail.GET_FRI_INV_REQ.getApiMethod(),
                s_id,
                token};
        HttpNRequest request = new HttpNRequest(RequestDetail.GET_FRI_INV_REQ, params);
        new FriendsAsync().execute(request);
    }

    /**
     * By Renato
     * @param friend_number
     */
    public void rmvFriend(String friend_number) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.RMV_FRIEND_REQ.getApiMethod(),
                s_id,
                token,
                friend_number};
        HttpNRequest request = new HttpNRequest(RequestDetail.RMV_FRIEND_REQ, params);
        new FriendsAsync().execute(request);
    }

    /**
     * dcl = decline
     * By Renato
     * @param friend_number
     */
    public void dclFriend(String friend_number) {
        Student student = db.getStudent();
        // safety validation
        if (student == null) return;
        String s_id = student.getId();
        String token = student.getToken();
        String[] params = new String[] {RequestDetail.DCL_FRIEND_REQ.getApiMethod(),
                s_id,
                token,
                friend_number};
        HttpNRequest request = new HttpNRequest(RequestDetail.DCL_FRIEND_REQ, params);
        new FriendsAsync().execute(request);
    }

    /*
       By Diogo
       Last change: 2016-05-02
       Added code to control the TabHost and to add a new list adapter for invitations
       @param src URL
       @return Bitmap Image

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    /**
     * By Renato
     * @param json bla
     * @param isInvite bla
     * @return bla
     */


    /**
     * By Renato
     */
    private void updateFriendsList(boolean isInvite) {
        if (!isInvite) {
            Friend[] friends = db.getFriends(false);
            listFriendsAdapter.clear();
            listFriendsAdapter.addAll(friends);
        } else {
            Friend[] friends = db.getFriends(true);
            listInvitationsAdapter.clear();
            listInvitationsAdapter.addAll(friends);
        }
    }

    /**
     * By Renato
     */
    private void clearSearchBox() {
        SearchView searchView = (SearchView) friendsView.findViewById(R.id.searchView);
        searchView.setQuery(null, false);
    }

    private class FriendsAsync extends AsyncTask<HttpNRequest, Void, Code> {
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

                if (details == RequestDetail.GET_FRIENDS_REQ || details == RequestDetail.GET_FRI_INV_REQ) {
                    json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JResultFriend.class);
                } else if (details == RequestDetail.CHK_STUDENT_REQ) {
                    json = Helpers.jsonToObject(Helpers.getConnectionContent(conn.getInputStream())[0], JResultStudent.class);
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
            // region PRE NETWORK NOTIFICATION
            switch (code) {
                case ACP_FRIEND_SUC:
                    getFriends();
                    getInvitations();
                    // FIXME remove from data base CODE: 1
                    //db.deleteFriend(request.getParametersDetails()[3], true);
                    //updateFriendsList(true);
                    //
                    String titleInvAccept = getString(R.string.title_invitation_accept);
                    String textInvAccept = getString(R.string.text_invitation_accept);
                    Helpers.showMsgBox(getActivity(), titleInvAccept, textInvAccept);
                    break;
                case RMV_FRIEND_SUC:
                    getFriends();
                    String titleFriRemoved = getString(R.string.title_friend_removed);
                    String textFriRemoved = getString(R.string.text_friend_removed);
                    Helpers.showMsgBox(getActivity(), titleFriRemoved, textFriRemoved);
                    break;
                case DCL_FRIEND_SUC:
                    getInvitations();
                    // FIXME remove from data base CODE: 1
                    //db.deleteFriend(request.getParametersDetails()[3], true);
                    //updateFriendsList(true);
                    //
                    String titleInvRemoved = getString(R.string.title_invitation_removed);
                    String textInvRemoved = getString(R.string.text_invitation_removed);
                    Helpers.showMsgBox(getActivity(), titleInvRemoved, textInvRemoved);
                    break;
                case CHK_STUDENT_SUC:
                    Helpers.stopProgressDialog(progress);
                    break;
                case CHK_STUDENT_ERR:
                    Helpers.stopProgressDialog(progress);
                    break;
            }
            // endregion

            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), code, true);
            if (shown) return;

            // region POST NETWORK NOTIFICATION
            switch (code) {
                case GET_FRIENDS_SUC:
                    updateFriendsList(false);
                    break;
                case GET_FRI_INV_SUC:
                    updateFriendsList(true);
                    break;
                case ACP_FRIEND_ERR:
                    String titleInvReject = getString(R.string.title_invitation_reject);
                    String textInvReject = getString(R.string.text_invitation_reject);
                    Helpers.showMsgBox(getActivity(), titleInvReject, textInvReject);
                    break;
                case RMV_FRIEND_ERR:
                    String titleFriReject = getString(R.string.title_friend_reject);
                    String textFriReject = getString(R.string.text_friend_reject);
                    Helpers.showMsgBox(getActivity(), titleFriReject, textFriReject);
                    break;
                case DCL_FRIEND_ERR:
                    String titleInvImp = getString(R.string.title_invitation_impossible);
                    String textInvImp = getString(R.string.text_invitation_impossible);
                    Helpers.showMsgBox(getActivity(), titleInvImp, textInvImp);
                    break;
                case CHK_STUDENT_SUC:
                    checkStudentConfirmation();
                    break;
                case CHK_STUDENT_ERR:
                    clearSearchBox();
                    String titleNoStud = getString(R.string.title_no_student);
                    String textNoStud = getString(R.string.text_no_student);
                    Helpers.showMsgBox(getActivity(), titleNoStud, textNoStud);
                    break;
                case ADD_FRIEND_SUC:
                    clearSearchBox();
                    String titleSucInv = getString(R.string.title_success_invitation);
                    String textSucInv = getString(R.string.text_success_invitation);
                    Helpers.showMsgBox(getActivity(), titleSucInv, textSucInv);
                    break;
                case ADD_FRIEND_ERR:
                    clearSearchBox();
                    String titleUnaInv = getString(R.string.title_unable_invitation);
                    String textUnaInv = getString(R.string.text_unable_invitation);
                    Helpers.showMsgBox(getActivity(), titleUnaInv, textUnaInv);
                    break;
                case ADD_ALREADY_FRI:
                    clearSearchBox();
                    String titleStudExist = getString(R.string.title_student_exist);
                    String textStudExist = getString(R.string.text_student_exist);
                    Helpers.showMsgBox(getActivity(), titleStudExist, textStudExist);
                    break;
                case ADD_ALREADY_INV:
                    clearSearchBox();
                    String titleInvSent = getString(R.string.title_invitation_sent);
                    String textInvSent = getString(R.string.text_invitation_sent);
                    Helpers.showMsgBox(getActivity(), titleInvSent, textInvSent);
                    break;
                case ADD_SAME_USER:
                    clearSearchBox();
                    String titleInvUnable = getString(R.string.title_invitation_unable);
                    String textInvUnable = getString(R.string.text_invitation_unable);
                    Helpers.showMsgBox(getActivity(), titleInvUnable, textInvUnable);
                    break;
            }
            // endregion
        }

        private Code operation(RequestDetail req, JSONClass json, DataBase db) throws InvalidStudentException {
            JServerAbstract jserver;
            JResultFriend jfriend;
            JResultStudent jstudent;
            switch (req)
            {
                case SIUPT_LOGIN:
                    break;
                case LOGIN_REQUEST:
                    break;
                case LOGOUT_REQUEST:
                    break;
                case LOGOUT_ALL_REQ:
                    break;
                case ADD_FRIEND_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.ADD_FRIEND_ERR;
                    // extra conditions
                    if (jserver.state == 3) return Code.ADD_ALREADY_INV;
                    if (jserver.state == 4) return Code.ADD_ALREADY_FRI;
                    if (jserver.state == 5) return Code.ADD_SAME_USER;
                    return Code.ADD_FRIEND_SUC;
                case ACP_FRIEND_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.ACP_FRIEND_ERR;
                    return Code.ACP_FRIEND_SUC;
                case GET_FRIENDS_REQ:
                    jfriend = (JResultFriend) json;
                    if (!Helpers.checkJsonObject(jfriend)) return Code.GET_FRIENDS_ERR;
                    // add to friends to db
                    Friend[] friends = HelpersDB.jsonIntoFriends(jfriend, false);
                    db.insertFriends(friends, false);
                    if(friends.length > 0) {
                        //db.insertFriends(friends, false);
                        new PhotoAsync().execute(friends);
                    }
                    return Code.GET_FRIENDS_SUC;
                case GET_FRI_INV_REQ:
                    jfriend = (JResultFriend) json;
                    if (!Helpers.checkJsonObject(jfriend)) return Code.GET_FRI_INV_ERR;
                    // add friends to db
                    friends = HelpersDB.jsonIntoFriends(jfriend, true);
                    db.insertFriends(friends, true);
                    if(friends.length > 0) {
                        //db.insertFriends(friends, true);
                        new PhotoAsync().execute(friends);
                    }
                    return Code.GET_FRI_INV_SUC;
                case RMV_FRIEND_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.RMV_FRIEND_ERR;
                    return Code.RMV_FRIEND_SUC;
                case DCL_FRIEND_REQ:
                    jserver = (JServerAbstract) json;
                    if (!Helpers.checkJsonObject(jserver)) return Code.DCL_FRIEND_ERR;
                    return Code.DCL_FRIEND_SUC;
                case CHK_STUDENT_REQ:
                    jstudent = (JResultStudent) json;
                    if (!Helpers.checkJsonObject(jstudent) || jstudent.result.size() == 0) return Code.CHK_STUDENT_ERR;
                    return Code.CHK_STUDENT_SUC;
                case SIUPT_SCHED_REQ:
                    break;
                default:
                    return Code.COD_UNHANDLED_EXC;
            }
            return null;
        }

        private void checkStudentConfirmation() {
            final JResultStudent jstudent = (JResultStudent) json;
            AlertDialog.Builder winChoice = new AlertDialog.Builder(getActivity());

            String titleAddStud = getString(R.string.title_add_student);
            String negAddStud = getString(R.string.negative_add_student);
            String posAddStud = getString(R.string.positive_add_student);
            String questFriAccept = jstudent.result.get(0).student_name;

            winChoice.setTitle(titleAddStud)
                    .setMessage(questFriAccept)
                    .setNegativeButton(negAddStud, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(posAddStud, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface self, int what) {
                            // start request to send an invite
                            addFriend(jstudent.result.get(0).student_id);
                        }
                    })
                    .show();
        }
    }

    private class PhotoAsync extends AsyncTask<Friend[], Void, Void> {

        @Override
        protected Void doInBackground(Friend[]... params) {
            try {
                for(int i = 0;i < params[0].length;i++) {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(params[0][i].getPhotoURL()).getContent());
                    db.updateFriendPhoto(Integer.parseInt(params[0][i].getNumber()), bitmap);
                }
            }
            catch(Exception e){
                Log.i("BITMAP",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateFriendsList(true);
            updateFriendsList(false);
        }
    }
}
