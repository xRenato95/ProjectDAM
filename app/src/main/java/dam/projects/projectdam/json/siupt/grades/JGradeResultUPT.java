package dam.projects.projectdam.json.siupt.grades;

import java.util.List;

import dam.projects.projectdam.json.JSONClass;

/**
 * Created by Renato on 11/05/2016 : 12:25.
 */
public class JGradeResultUPT extends JSONClass {
    public List<JGradeUPT> result;

    @Override
    public boolean isValid() {
        return result != null;
    }
}
