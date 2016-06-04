package dam.projects.projectdam.json;
@SuppressWarnings("unused")
/**
 * Created by Renato on 23/04/2016 : 00:34.
 * Abstract class that represents all JSON related objects.
 */
public abstract class JSONClass {
    /**
     * Method that checks if the json's string was successfully converted into an object.
     * @return true if it was successfully converted, false otherwise.
     */
    public abstract boolean isValid();
}
