package dam.projects.projectdam.json.server.event.member;

import java.util.List;

import dam.projects.projectdam.json.server.JServerAbstract;

/**
 * Created by Renato on 30/05/2016 : 00:26.
 */
public class JResultMember extends JServerAbstract {
    public List<JMember> result;

    @Override
    public boolean isValid() { return (super.isValid() && result != null); }
}
