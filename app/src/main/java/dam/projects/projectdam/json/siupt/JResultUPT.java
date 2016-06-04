package dam.projects.projectdam.json.siupt;

import dam.projects.projectdam.json.JSONClass;

/**
 * Created by renat on 29/05/2016 : 22:08.
 */
public class JResultUPT extends JSONClass {
    public boolean result;

    @Override
    public boolean isValid() {
        return result;
    }
}
