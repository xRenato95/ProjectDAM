package dam.projects.projectdam.json.server;

import java.util.List;

/**
 * Created by Renato on 06/05/2016 : 17:33.
 */
public class JServerResult<T> extends JServerAbstract {
    public List<T> result;

    @Override
    public boolean isValid() {
        return (super.isValid() && result != null);
    }
}
