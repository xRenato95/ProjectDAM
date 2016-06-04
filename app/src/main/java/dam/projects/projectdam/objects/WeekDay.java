package dam.projects.projectdam.objects;

import java.util.ArrayList;

/**
 * Created by Diogo on 20/05/2016 : 23:32.
 */
public class WeekDay {
    private String weekDay;
    private ArrayList<ScheduleDay> Items;

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }

    public ArrayList<ScheduleDay> getItems() {
        return Items;
    }

    public void setItems(ArrayList<ScheduleDay> Items) {
        this.Items = Items;
    }
}
