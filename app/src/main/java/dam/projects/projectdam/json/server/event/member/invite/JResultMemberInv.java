package dam.projects.projectdam.json.server.event.member.invite;

import java.util.List;

import dam.projects.projectdam.json.server.JServerAbstract;

/**
 * Created by Renato on 30/05/2016 : 00:54.
 */
public class JResultMemberInv extends JServerAbstract {
    public List<JMemberInv> result;

    @Override
    public boolean isValid() { return (super.isValid() && result != null); }
}
