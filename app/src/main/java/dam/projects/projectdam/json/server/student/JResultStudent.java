package dam.projects.projectdam.json.server.student;

import java.util.List;

import dam.projects.projectdam.json.server.JServerAbstract;

/**
 * Created by Renato on 05/05/2016 : 23:16.
 */
public class JResultStudent extends JServerAbstract {
    public List<JStudent> result;

    @Override
    public boolean isValid() {
        return (super.isValid() && result != null);
    }
}
