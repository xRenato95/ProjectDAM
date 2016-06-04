package dam.projects.projectdam.network;

import junit.framework.TestCase;

import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.json.server.JServerResult;
import dam.projects.projectdam.json.server.friend.JFriend;
import dam.projects.projectdam.json.server.friend.JResultFriend;

/**
 * Created by renat on 10/04/2016 : 20:43.
 */
public class HTTPConnectionTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testStartRequest() throws Exception {
        /*String[] param = new String[RequestDetail.GET_EVENT_TYPE.getNumberParameters()];
        param[0] = RequestDetail.GET_EVENT_TYPE.getApiMethod();
        param[1] = "3";

        HttpNRequest request = new HttpNRequest(RequestDetail.GET_EVENT_TYPE, param);

        HttpConnection conn = new HttpConnection();*/
        /*HttpNResponse responses = conn.startSyncRequest(request);

        assertEquals(200, responses.getResponseCode());
        Log.d("RESULT", responses.getResponseContent());

        JSONObject jsonObject = Helpers.stringToJSONObject(responses.getResponseContent());
        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String eg = iterator.next();
            Log.d("RESULT", eg);
        }

        Log.d("RESULT", "---");*/
        //TStudent tStudent = new TStudent();
        //Log.d("DB", tStudent.getCreateString());

        //Log.d("PROXY", System.getProperty( "http.proxyHost" ));

        JServerResult<JFriend> a = new JServerResult<>();
        Helpers.jsonToObject("Hello", JResultFriend.class);
        //Helpers.jsonToObject("Hello", );
    }
}