package dam.projects.projectdam.json.siupt;

import dam.projects.projectdam.json.JSONClass;

/**
 * Created by Renato on 23/04/2016 : 17:50.
 * Class that represents an object returned from SIUPT's API.
 * Contains all info about generic API errors.
 */
public class JErrorUPT extends JSONClass {
    public JErrorContent err;

    @Override
    public boolean isValid() {
        return err != null;
    }
}
