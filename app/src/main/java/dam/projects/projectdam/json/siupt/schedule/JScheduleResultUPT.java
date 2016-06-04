package dam.projects.projectdam.json.siupt.schedule;

import java.util.List;

import dam.projects.projectdam.json.JSONClass;

/**
 * Created by Renato on 11/05/2016 : 12:20.
 */
public class JScheduleResultUPT extends JSONClass{
    public List<JScheduleUPT> result;

    @Override
    public boolean isValid() {
        return result != null;
    }
}
