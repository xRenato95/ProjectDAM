package dam.projects.projectdam.json.server.login;

import dam.projects.projectdam.json.server.JServerAbstract;

/**
 * Created by Renato on 25/04/2016 : 01:33.
 */
public class JResultLogin extends JServerAbstract {
    public JLogin result;

    @Override
    public boolean isValid() {
        return super.isValid() && result != null && result.token != null;
    }
}
