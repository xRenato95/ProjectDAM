package dam.projects.projectdam.objects;

import java.util.ArrayList;

/**
 * Created by pedro on 29/05/2016.
 */
public class Course {
    private String course;
    private ArrayList<Grade> Items;

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) { this.course = course; }

    public ArrayList<Grade> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Grade> Items) {
        this.Items = Items;
    }
}