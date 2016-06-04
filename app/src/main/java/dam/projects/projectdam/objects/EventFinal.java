package dam.projects.projectdam.objects;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * Created by Renato on 21/05/2016 : 18:07.
 */
public class EventFinal implements Parcelable {
    private int ev_id, remote_id;
    // student id
    private int s_id;
    // type event
    private EventType eventType;
    // visibility
    private VisibilityType visibilityType;
    // event
    private String title, description, local;
    private LocalDate dateBegin, dateEnd;
    private LocalTime hourBegin, hourEnd;

    private DateTime dateOfCreation;

    private int numberMembers;
    private double averageRating;

    // for db only
    public EventFinal(int ev_id, int remote_id, int s_id, EventType eventType, VisibilityType visibilityType, String title, String description, String local, LocalDate dateBegin, LocalDate dateEnd, LocalTime hourBegin, LocalTime hourEnd, DateTime dateOfCreation, int numberMembers, double averageRating) {
        this.ev_id = ev_id;
        this.remote_id = remote_id;
        this.s_id = s_id;
        this.eventType = eventType;
        this.visibilityType = visibilityType;
        this.title = title;
        this.description = description;
        this.local = local;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.hourBegin = hourBegin;
        this.hourEnd = hourEnd;
        this.dateOfCreation = dateOfCreation;
        this.numberMembers = numberMembers;
        this.averageRating = averageRating;
    }

    public EventFinal(int remote_id, int s_id, EventType eventType, VisibilityType visibilityType, String title, String description, String local, LocalDate dateBegin, LocalDate dateEnd, LocalTime hourBegin, LocalTime hourEnd, DateTime dateOfCreation, int numberMembers, double averageRating) {
        this.remote_id = remote_id;
        this.s_id = s_id;
        this.eventType = eventType;
        this.visibilityType = visibilityType;
        this.title = title;
        this.description = description;
        this.local = local;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.hourBegin = hourBegin;
        this.hourEnd = hourEnd;
        this.dateOfCreation = dateOfCreation;
        this.numberMembers = numberMembers;
        this.averageRating = averageRating;
    }

    public EventFinal(EventType eventType, VisibilityType visibilityType, String title, String description, String local, LocalDate dateBegin, LocalDate dateEnd, LocalTime hourBegin, LocalTime hourEnd) {
        this.eventType = eventType;
        this.visibilityType = visibilityType;
        this.title = title;
        this.description = description;
        this.local = local;
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.hourBegin = hourBegin;
        this.hourEnd = hourEnd;
    }

    protected EventFinal(Parcel in) {
        ev_id = in.readInt();
        remote_id = in.readInt();
        s_id = in.readInt();
        title = in.readString();
        description = in.readString();
        local = in.readString();
        numberMembers = in.readInt();
        averageRating = in.readDouble();

        dateBegin = (LocalDate) in.readSerializable();
        dateEnd = (LocalDate) in.readSerializable();
        hourBegin = (LocalTime) in.readSerializable();
        hourEnd = (LocalTime) in.readSerializable();
        dateOfCreation = (DateTime) in.readSerializable();

        eventType = (EventType) in.readSerializable();
        visibilityType = (VisibilityType) in.readSerializable();
    }

    public static final Creator<EventFinal> CREATOR = new Creator<EventFinal>() {
        @Override
        public EventFinal createFromParcel(Parcel in) {
            return new EventFinal(in);
        }

        @Override
        public EventFinal[] newArray(int size) {
            return new EventFinal[size];
        }
    };

    public int getEv_id() {
        return ev_id;
    }

    public int getRemote_id() {
        return remote_id;
    }

    public int getS_id() {
        return s_id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public VisibilityType getVisibilityType() {
        return visibilityType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocal() {
        return local;
    }

    public LocalDate getDateBegin() {
        return dateBegin;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public LocalTime getHourBegin() {
        return hourBegin;
    }

    public LocalTime getHourEnd() {
        return hourEnd;
    }

    public DateTime getDateOfCreation() {
        return dateOfCreation;
    }

    public int getNumberMembers() {
        return numberMembers;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setNumberMembers(int numberMembers) {
        this.numberMembers = numberMembers;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ev_id);
        dest.writeInt(remote_id);
        dest.writeInt(s_id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(local);
        dest.writeInt(numberMembers);
        dest.writeDouble(averageRating);

        dest.writeSerializable(dateBegin);
        dest.writeSerializable(dateEnd);
        dest.writeSerializable(hourBegin);
        dest.writeSerializable(hourEnd);
        dest.writeSerializable(dateOfCreation);

        dest.writeSerializable(eventType);
        dest.writeSerializable(visibilityType);
    }

    @Override
    public String toString() {
        return "EventFinal{" +
                "ev_id=" + ev_id +
                ", remote_id=" + remote_id +
                ", s_id=" + s_id +
                ", eventType=" + eventType +
                ", visibilityType=" + visibilityType +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", local='" + local + '\'' +
                ", dateBegin=" + dateBegin +
                ", dateEnd=" + dateEnd +
                ", hourBegin=" + hourBegin +
                ", hourEnd=" + hourEnd +
                ", dateOfCreation=" + dateOfCreation +
                ", numberMembers=" + numberMembers +
                ", averageRating=" + averageRating +
                '}';
    }
}
