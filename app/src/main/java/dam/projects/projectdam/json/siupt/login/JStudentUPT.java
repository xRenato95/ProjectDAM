package dam.projects.projectdam.json.siupt.login;

import dam.projects.projectdam.json.JSONClass;

@SuppressWarnings("unused")
/**
 * Created by Renato on 23/04/2016 : 00:40.
 * Class that represents an object returned from SIUPT's API.
 * Contains all info about the student.
 */
public class JStudentUPT extends JSONClass {
    public String id, name, mail, foto;

    @Override
    public boolean isValid() {
        return id != null && name != null && mail != null && foto != null;
    }
}
