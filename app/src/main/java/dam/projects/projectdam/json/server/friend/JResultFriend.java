package dam.projects.projectdam.json.server.friend;

import java.util.List;

import dam.projects.projectdam.json.server.JServerAbstract;

/**
 * Created by Renato on 30/04/2016 : 16:56.
 */
public class JResultFriend extends JServerAbstract {
    public List<JFriend> result;

    @Override
    public boolean isValid() {
        return (super.isValid() && result != null);
    }
}
