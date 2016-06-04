package dam.projects.projectdam.objects;

import java.io.Serializable;

/**
 * Created by Renato on 21/05/2016 : 18:32.
 */
public class VisibilityType implements Serializable {
    private int visibility_id;
    private String visibility_code;

    public VisibilityType(int visibility_id, String visibility_code) {
        this.visibility_id = visibility_id;
        this.visibility_code = visibility_code;
    }

    public int getVisibility_id() {
        return visibility_id;
    }

    public String getVisibility_code() {
        return visibility_code;
    }
}
