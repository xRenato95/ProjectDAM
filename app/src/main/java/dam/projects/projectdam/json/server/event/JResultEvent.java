package dam.projects.projectdam.json.server.event;

import java.util.List;

import dam.projects.projectdam.json.server.JServerAbstract;

/**
 * Created by Renato on 30/05/2016 : 00:15.
 */
public class JResultEvent extends JServerAbstract {
    public List<JEvent> result;

    @Override
    public boolean isValid() { return (super.isValid() && result != null); }
}
