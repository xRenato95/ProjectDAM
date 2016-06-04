package dam.projects.projectdam.objects;

/**
 * Created by Diogo on 16/05/2016 : 19:55.
 */
public class Event {
    private int e_id;
    private String name;
    private String admin;
    private String desc;
    private int day;
    private String month;
    private String year;

    public Event(String name, String admin, String desc, int day, String month, String year)
    {
        this.name = name;
        this.admin = admin;
        this.desc = desc;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Event(int id, String name, String admin, String desc, int day, String month, String year)
    {
        this.e_id = id;
        this.name = name;
        this.admin = admin;
        this.desc = desc;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getF_id() {
        return e_id;
    }

    public String getNameEvent()
    {
        return name;
    }
    public String getAdmin()
    {
        return admin;
    }
    public String getDesc()
    {
        return desc;
    }
    public int getDay()
    {
        return day;
    }
    public String getMonth()
    {
        return month;
    }
    public String getYear()
    {
        return year;
    }

    @Override
    public String toString() {
        return "Event{" +
                "f_id=" + e_id +
                ", name='" + name + '\'' +
                ", admin='" + admin + '\'' +
                ", desc='" + desc + '\'' +
                ", day='" + day + '\'' +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
