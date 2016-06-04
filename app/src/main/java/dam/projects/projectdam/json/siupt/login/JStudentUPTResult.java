package dam.projects.projectdam.json.siupt.login;

import dam.projects.projectdam.json.JSONClass;

/**
 * Created by Renato on 23/04/2016 : 01:20.
 * Class that represents an object returned from SIUPT's API.
 * Contains the result of the call.
 * Mainly used to check if the credentials are invalid.
 */
public class JStudentUPTResult extends JSONClass {
    public String result;

    @Override
    public boolean isValid() {
        return result != null;
    }
}
