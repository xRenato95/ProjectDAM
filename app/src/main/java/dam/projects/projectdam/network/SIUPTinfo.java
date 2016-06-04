package dam.projects.projectdam.network;

/**
 * Created by Renato on 22/04/2016 : 23:13.
 * Enumerator with information about SIUPT's API.
 */
public enum SIUPTinfo {
    API_KEY("insert api key here");

    private final String key;

    SIUPTinfo(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public static String getTypeStudent() {
        return "2";
    }
}
