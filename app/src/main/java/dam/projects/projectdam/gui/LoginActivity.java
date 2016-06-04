package dam.projects.projectdam.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

import dam.projects.projectdam.R;
import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.json.server.login.JResultLogin;
import dam.projects.projectdam.json.siupt.login.JStudentUPT;
import dam.projects.projectdam.json.siupt.login.JStudentUPTResult;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.HttpNResponse;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.network.SIUPTinfo;
import dam.projects.projectdam.sqlite.DataBase;


/**
 * A login screen that offers login via SIUPT.
 * By Renato
 * Last change: 2016-04-29
 * Added AsyncTask and data base connection.
 */
public class LoginActivity extends AppCompatActivity implements IActivity {
    private ProgressDialog progressDialog;
    private DataBase db;

    // region Activity Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get database object reference
        db = new DataBase(this);
        //db.deleteStudent();

        // if user is already logged in, skip this activity
        if (db.checkStudent()) {
            Helpers.changeActivity(LoginActivity.this, MenuActivity.class, true, null, null);
         }

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    // endregion

    public void btnLogin(View button) {
        AutoCompleteTextView eStudent = (AutoCompleteTextView) findViewById(R.id.field_stud_num);
        EditText ePassword = (EditText) findViewById(R.id.field_stud_pw);

        String studentNumber = eStudent.getText().toString();
        String studentPass = ePassword.getText().toString();

        String[] params = new String[] {
                SIUPTinfo.API_KEY.key(),
                RequestDetail.SIUPT_LOGIN.getApiMethod(),
                studentNumber,
                studentPass,
                SIUPTinfo.getTypeStudent()
        };

        HttpNRequest httpRequest = new HttpNRequest(RequestDetail.SIUPT_LOGIN, params);

        new LoginStudent().execute(httpRequest);
    }

    // region Interface Methods
    /*
       By Diogo
       Last change: 2016-04-25
       Altered to show text to the user
    */
    private void startProgressBar() {
        String titleLogIn = getString(R.string.title_login_in);
        String textLogIn = getString(R.string.text_login_in);
        progressDialog = Helpers.startProgressDialog(this, titleLogIn, textLogIn);
    }

    private void stopProgressBar() {
        Helpers.stopProgressDialog(progressDialog);
    }

    private void clearLabels() {
        AutoCompleteTextView eStudent = (AutoCompleteTextView) findViewById(R.id.field_stud_num);
        EditText ePassword = (EditText) findViewById(R.id.field_stud_pw);
        eStudent.setText(null);
        ePassword.setText(null);
    }

    // endregion

    // region AsyncTask Class
    /**
     * ----------------------ASYNC TASK--------------------------------------
     * By Renato
     * Last Change: 2016-04-29
     * Improved: Code encapsulation
     */
    private class LoginStudent extends AsyncTask<HttpNRequest, Void, HttpNResponse> {
        private int stateCode;
        private JStudentUPT jStudent;
        private Code connectionCode;

        @Override
        protected void onPreExecute() {
            stateCode = -2;
            connectionCode = Code.NET_GENERIC_ERR;
            startProgressBar();
        }

        @Override
        protected HttpNResponse doInBackground(HttpNRequest... params) {
            try {
                // region SIUPT LOGIN CHECK
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

                HttpNResponse response = new HttpNResponse(replyStatus, replyMsg, content, null);
                stateCode = checkUPTResponse(response);
                // endregion

                // region REMOTE API NOTIFICATION

                if (stateCode != 1) {
                    return response;
                }

                RequestDetail reqDetails = RequestDetail.LOGIN_REQUEST;

                String[] values = new String[]{reqDetails.getApiMethod(),
                        jStudent.id,
                        jStudent.name,
                        jStudent.mail,
                        jStudent.foto};

                HttpNRequest newRequest = new HttpNRequest(reqDetails, values);

                HttpURLConnection secondConn = Helpers.createConnection(newRequest);

                // establish connection
                secondConn.connect();
                // save connection code
                connectionCode = Helpers.checkConnectionSate(connection);

                // check connection reply
                String replyMsg2 = secondConn.getResponseMessage();
                int replyStatus2 = secondConn.getResponseCode();

                if (connectionCode != Code.NET_SUCCESS_COD) {
                    return new HttpNResponse(replyStatus2, replyMsg2, new Exception(replyMsg2));
                }

                // if the http connection was successful, get the content of the reply
                contents = Helpers.getConnectionContent(secondConn.getInputStream());
                String content2 = contents[0];

                // convert to json
                JResultLogin jResultLogin = (JResultLogin) Helpers.jsonToObject(content2, JResultLogin.class);
                if (!jResultLogin.isValid()) {
                    stateCode = -1;
                    return new HttpNResponse(replyStatus2, replyMsg2, new Exception(replyMsg2));
                }

                // add token to data base
                db.insertStudent(jStudent.id, jStudent.name, jStudent.mail, jStudent.foto, jResultLogin.result.token);
                // endregion
                return response;
            } catch (SocketTimeoutException e) {
                // internet time out!
                connectionCode = Code.NET_GENERIC_ERR;
                return new HttpNResponse(0, null, e);
            } catch (Exception e) {
                Log.wtf("BUG", e.toString());
                connectionCode = Code.COD_UNHANDLED_EXC;
                return new HttpNResponse(0, null, e);
            }
        }

        /*
           By Renato
           Last change: 2016-04-28
           Improved: Code encapsulated
        */
        @Override
        protected void onPostExecute(HttpNResponse httpResponse) {
            stopProgressBar();
            boolean shown = Helpers.showNetworkDialogErrors(getActivity(), connectionCode, false);

            if (!shown) {
                switch (stateCode) {
                    case -1:
                        String titleUnSer = getString(R.string.title_unavailable_service);
                        String textUnServ = getString(R.string.text_unavailable_service);
                        Helpers.showMsgBox(getActivity(), titleUnSer, textUnServ);
                        clearLabels();
                        break;
                    case 0:
                        String titleInCred = getString(R.string.title_invalid_credentials);
                        String textInCred = getString(R.string.text_invalid_credentials);
                        Helpers.showMsgBox(getActivity(), titleInCred, textInCred);
                        clearLabels();
                        break;
                    case 1:
                        Helpers.changeActivity(getActivity(), MenuActivity.class, true, null, null);
                        break;
                }
            }
        }

        private int checkUPTResponse(HttpNResponse response) {
            if (!response.isRequestSucceed()) return -2;
            // check if the the login was successful
            JStudentUPT jStudentUPT = (JStudentUPT) Helpers.jsonToObject(response.getResponseContent(), JStudentUPT.class);
            if (!jStudentUPT.isValid()) {
                // check if the password is valid
                JStudentUPTResult jStudentUPTResult = (JStudentUPTResult) Helpers.jsonToObject(response.getResponseContent(), JStudentUPTResult.class);
                if (!jStudentUPTResult.isValid()) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                jStudent = jStudentUPT;
                return 1;
            }
        }

    }
    // endregion
}

