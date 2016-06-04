package dam.projects.projectdam.json.server;

import dam.projects.projectdam.json.JSONClass;

/**
 * Created by Renato on 25/04/2016 : 01:30.
 */
public class JServerAbstract extends JSONClass implements IJServer {
    public Integer state;

    @Override
    public boolean isValid() {
        return (state != null && state != 0);
    }

    @Override
    public boolean isValidStudent() {
        return (state != null && state != 2);
    }
}
