package dam.projects.projectdam.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.joda.time.LocalDateTime;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dam.projects.projectdam.R;
import dam.projects.projectdam.exception.InvalidStudentException;
import dam.projects.projectdam.gui.LoginActivity;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.json.server.IJServer;
import dam.projects.projectdam.network.HttpNRequest;
import dam.projects.projectdam.network.HttpNResponse;
import dam.projects.projectdam.network.RequestDetail;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.EventType;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.VisibilityType;
import dam.projects.projectdam.sqlite.DataBase;

@SuppressWarnings("unused")

/**
 * Created by Renato on 12/04/2016 : 23:20.
 * Class with helpful methods.
 */
public class Helpers {

    public static Grade[] userGrades;

    // region NETWORK
    /**
     * [NETWORK]
     * Method that creates an http connection.
     * By Renato
     * @param request all details required to create an http connection
     * @return the connection
     * @throws IOException
     * @throws SocketTimeoutException if there is not access to the internet
     */
    public static HttpURLConnection createConnection(HttpNRequest request)
            throws IOException, SocketTimeoutException   {
        RequestDetail requestDetails = request.getConnectionDetails();

        // turn parameters into one query string
        String paramString = Helpers.getQueryString(Helpers.getKeyValueParam(request));

        URL url;
        if (requestDetails.getMethod().equals("GET")) {
            url = new URL(requestDetails.getUrl()+"?"+paramString);
        } else {
            url = new URL(requestDetails.getUrl());
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // set connection/read time out
        connection.setReadTimeout(10 * 1000);
        connection.setConnectTimeout(10 * 1000);

        if (requestDetails.getMethod().equals("POST")) {
            DataOutputStream dStream = null;

            // setting up POST header
            connection.setRequestMethod(requestDetails.getMethod());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            try {
                // set parameters
                dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(paramString);
            } catch (SocketTimeoutException e) {
                // no internet
                throw e;
            } finally {
                if (dStream != null) {
                    dStream.flush();
                    dStream.close();
                }
            }
        }
        return connection;
    }

    /**
     * // TODO TO BE CONTINUED AND IMPROVED TO RETURN MULTIPLE LINE STRINGS
     * [NETWORK]
     * Method that returns the content of the successful http connection.
     * By Renato
     * @param source input stream given by the connection
     * @return Array of lines, each index equals to one line of the content
     * @throws IOException
     */
    public static String[] getConnectionContent(InputStream source)
            throws IOException {
        BufferedReader bReader = null;
        String content;
        try {
            bReader = new BufferedReader(new InputStreamReader(source, "UTF-8"));
            content = bReader.readLine();
            return new String[] {content};
        } catch (IOException e) {
            throw e;
        } finally {
            if (bReader != null) try {
                bReader.close();
            } catch (IOException e) {
                //
            }
        }
    }

    /**
     * [NETWORK]
     * Method that returns a code according with the state of the connection.
     * By Renato
     * @param connection the http connection
     * @return the code
     */
    public static Code checkConnectionSate(HttpURLConnection connection) {
        HttpNResponse temp;
        try {
            int replyCode = connection.getResponseCode();
            // temporary
            temp = new HttpNResponse(replyCode,null,null);
            if (temp.isServerProblem()) {
                temp = null;
                return Code.NET_SERVER_ERR;
            } else if (temp.isProxyProblem()) {
                temp = null;
                return Code.NET_PROXY_ERR;
            } else if (!temp.isRequestSucceed()) {
                temp = null;
                return Code.NET_GENERIC_ERR;
            } else if (temp.isRequestSucceed()){
                temp = null;
                return Code.NET_SUCCESS_COD;
            } else {
                temp = null;
                return Code.COD_UNHANDLED_EXC;
            }
        } catch (IOException e) {
            temp = null;
            return Code.COD_UNHANDLED_EXC;
        }
    }

    /**
     * [NETWORK]
     * Method that show an error dialog according with the given code.
     * By Renato
     * @param current current activity
     * @param code one code related with network
     * @return true if shows one dialog, false if doesn't.
     */
    public static boolean showNetworkDialogErrors(Activity current, Code code, boolean discreteNotification) {
        switch (code) {
            case NET_SUCCESS_COD:
                return false;
            case NET_GENERIC_ERR:
                String titleNetProb = current.getString(R.string.title_net_problem);
                String textNetProb = current.getString(R.string.text_net_problem);
                if (!discreteNotification) {
                    Helpers.showMsgBox(current, titleNetProb, textNetProb);
                } else {
                    String closeDialogNetwork = current.getString(R.string.close_dialog_network);
                    Snackbar.make(current.getCurrentFocus(),titleNetProb,Snackbar.LENGTH_INDEFINITE)
                            .setAction(closeDialogNetwork, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }
                return true;

            case NET_SERVER_ERR:
                // server "maintenance" <- because servers don't crash (specially ours!):D shh!
                String titleServeMain = current.getString(R.string.title_server_maintenance);
                String textServeMain = current.getString(R.string.text_server_maintenance);
                if (!discreteNotification) {
                    Helpers.showMsgBox(current, titleServeMain, textServeMain);
                } else {
                    Toast.makeText(current, titleServeMain, Toast.LENGTH_LONG).show();
                }
                return true;

            case NET_PROXY_ERR:
                String titleProxyProb = current.getString(R.string.title_proxy_problem);
                String textProxyProb = current.getString(R.string.text_proxy_problem);
                if (!discreteNotification) {
                    Helpers.showMsgBox(current, titleProxyProb, textProxyProb);
                } else {
                    Toast.makeText(current, textProxyProb, Toast.LENGTH_LONG).show();
                }
                return true;

            case COD_UNHANDLED_EXC:
                String titleGlobProb = current.getString(R.string.title_global_problem);
                String textGlobProb = current.getString(R.string.text_global_problem);
                if (!discreteNotification) {
                    Helpers.showMsgBox(current, titleGlobProb, textGlobProb);
                } else {
                    Toast.makeText(current, textGlobProb, Toast.LENGTH_LONG).show();
                }
                return true;

            case INVALID_STUDENT:
                String textSessionAgain = current.getString(R.string.text_session_again);
                Toast.makeText(current, textSessionAgain, Toast.LENGTH_LONG).show();
                forceLogout(current);
                return true;
            default:
                return false;

        }
    }

    /**
     * [NETWORK]
     * Method that transforms an HashMap of keys and values into one query string.
     * By Renato
     * @param params HashMap with keys and values
     * @return query string used for communication purposes
     */
    public static String getQueryString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                if (entry.getValue() != null) {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } else {
                    result.append("null");
                }
            } catch (UnsupportedEncodingException e) {
                // just to avoid warnings
            } catch (Exception e) {
                int i = 0;
            }
        }
        return result.toString();
    }

    /**
     * [NETWORK]
     * Method that returns an HashMap with keys and values of the connection parameters.
     * By Renato
     * @param request request with all connection parameters
     * @return HashMap with keys and values
     */
    public static HashMap<String, String> getKeyValueParam(HttpNRequest request) {
        // get details about communication
        RequestDetail requestDetails = request.getConnectionDetails();
        String[] paramName = requestDetails.getParameters();
        String[] paramDetails = request.getParametersDetails();

        // create list of parameters
        HashMap<String, String> parameters = new HashMap<>();
        for (int i = 0; i < paramName.length; i++) {
            parameters.put(paramName[i], paramDetails[i]);
        }
        return parameters;
    }

    public static boolean checkInternetConnection(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // endregion

    // region JSON
    /**
     * [JSON]
     * Method that transforms one JSON string into a java object.
     * By Renato
     * @param json JSON string
     * @param jsonClass java class which JSON string will be converted
     * @return JSON object
     */
    public static JSONClass jsonToObject(String json, Class<? extends JSONClass> jsonClass) {
        StringReader string_reader = new StringReader(json);
        Gson json_response = new Gson();
        JSONClass out = json_response.fromJson(string_reader, jsonClass);
        string_reader.close();
        return out;
    }

    public static boolean checkJsonObject(JSONClass jsonObject) throws InvalidStudentException {
        // if the student isn't valid, FORCE LOGOUT!
        if (jsonObject instanceof IJServer && !((IJServer) jsonObject).isValidStudent()) {
            throw new InvalidStudentException();
        }
        return jsonObject.isValid();
    }

    // endregion

    // region INTERFACE
    /**
     * [INTERFACE]
     * Method that shows a simple alert dialog with one positive button.
     * By Renato
     * @param activity current activity where this dialog will be shown
     * @param title title of the dialog
     * @param msg message of the dialog
     */
    public static void showMsgBox(Activity activity, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * [INTERFACE]
     * Method that returns and shows a progress dialog.
     * By Renato
     * @param activity current activity where this dialog will be shown
     * @param title title of the dialog
     * @param msg message of the dialog
     * @return the progress dialog object reference
     */
    public static ProgressDialog startProgressDialog(Activity activity, String title, String msg) {
        return ProgressDialog.show(activity, title, msg, true);
    }

    /**
     * [INTERFACE]
     * Method that disables a progress dialog.
     * By Renato
     * @param dialog the progress dialog object reference
     */
    public static void stopProgressDialog(ProgressDialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    // endregion

    /**
     * Method that changes from one activity to another.
     * By Renato
     * @param current current activity
     * @param destination next activity
     * @param finishCurrent true - finishes the current activity; false - keeps current activity
     * @param resultCode to start an activity for result with the given code
     * @param extras extra parameters to pass trough the the intent
     */
    public static void changeActivity(Activity current, Class<? extends Activity> destination,
                                      boolean finishCurrent, Code resultCode, Bundle extras) {
        Intent intent = new Intent(current, destination);
        if (extras != null) {
            intent.putExtras(extras);
        }
        if (resultCode == null) {
            current.startActivity(intent);
        } else {
            current.startActivityForResult(intent, resultCode.code);
        }
        if (finishCurrent) {
            current.finish();
        }
    }

    /**
     * Method that ends the session of the student.
     * By Renato
     * @param current current activity
     */
    public static void forceLogout(Activity current) {
        new DataBase(current).deleteStudent();
        changeActivity(current, LoginActivity.class, true, null, null);
    }

    /**
     * Method that returns a formatted name to show on the ListView
     * By Diogo
     * @param activity
     * @param name
     * @return the final name formatted
     */
    public static String renameFriend(Activity activity, String name) {
        String firstName = "";
        String lastName = "";
        String lastNameFinal = "";
        String finalName = "";

        try {
            firstName = name.substring(0, name.indexOf(" "));
            int lastSpace = name.lastIndexOf(" ");
            lastName = name.substring(lastSpace + 1, name.length());
            finalName = firstName + " " + lastName;

            if (finalName.length() >= 18) {
                for (int i = 0; i < 4; i++) {
                    lastNameFinal = lastNameFinal + lastName.substring(i, i + 1);
                }
                for (int j = 0; j < 3; j++) {
                    lastNameFinal = lastNameFinal + ".";
                }
                finalName = firstName + " " + lastNameFinal;
            }
            return finalName;
        } catch (Exception e) {
            return name;
        }
    }

    public static EventType[] getEventTypes(Activity currentActivity) {
        ArrayList<EventType> result = new ArrayList<>();
        result.add(new EventType(1, "TP_000001"));
        result.add(new EventType(2, "TP_000002"));
        result.add(new EventType(3, "TP_000003"));
        result.add(new EventType(4, "TP_000004"));
        result.add(new EventType(5, "TP_000005"));
        return result.toArray(new EventType[result.size()]);
    }

    public static VisibilityType[] getVisibilityTypes(Activity currentActivity) {
        ArrayList<VisibilityType> result = new ArrayList<>();
        result.add(new VisibilityType(1, "PRIVATE"));
        result.add(new VisibilityType(2, "FRI_ONLY"));
        result.add(new VisibilityType(3, "PUBLIC"));
        return result.toArray(new VisibilityType[result.size()]);
    }

    public static String getEventTypeString(Activity currentActivity, String code) {
        String retVal = null;
        String eventType1 = currentActivity.getString(R.string.event_cats_1);
        String eventType2 = currentActivity.getString(R.string.event_cats_2);
        String eventType3 = currentActivity.getString(R.string.event_cats_3);
        String eventType4 = currentActivity.getString(R.string.event_cats_4);
        String eventType5 = currentActivity.getString(R.string.event_cats_5);
        switch (code) {
            case "TP_000001":
                retVal = eventType1;
                break;
            case "TP_000002":
                retVal = eventType2;
                break;
            case "TP_000003":
                retVal = eventType3;
                break;
            case "TP_000004":
                retVal = eventType4;
                break;
            case "TP_000005":
                retVal = eventType5;
                break;

        }
        return retVal;
    }

    public static String getVisibilityCodeString(Activity currentActivity, String code) {
        String retVal = null;
        String eventVisibilityPrivate = currentActivity.getString(R.string.event_visibility_private);
        String eventVisibilityFriends = currentActivity.getString(R.string.event_visibility_friends);
        String eventVisibilityPublic = currentActivity.getString(R.string.event_visibility_public);
        switch (code) {
            case "PRIVATE":
                retVal = eventVisibilityPrivate;
                break;
            case "FRI_ONLY":
                retVal = eventVisibilityFriends;
                break;
            case "PUBLIC":
                retVal = eventVisibilityPublic;
                break;
        }
        return retVal;
    }

    public static Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String bitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setUserGrades(Context context){
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
        userGrades = new DataBase(context).getGrades(academicYear,semester);
    }
}
