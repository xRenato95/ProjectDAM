package dam.projects.projectdam.objects;

import java.io.Serializable;

/**
 * Created by Renato on 21/05/2016 : 18:32.
 */
public class EventType implements Serializable {
    private int type_id;
    private String type_code;

    public EventType(int type_id, String type_code) {
        this.type_id = type_id;
        this.type_code = type_code;
    }

    public int getType_id() {
        return type_id;
    }

    public String getType_code() {
        return type_code;
    }
}
