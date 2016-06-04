package dam.projects.projectdam.objects;

import org.joda.time.LocalTime;

/**
 * Created by Renato on 20/05/2016 : 16:35.
 * Class that represents a day on the schedule.
 */
public class ScheduleDay {
    private int sc_id;
    private String remoteId;
    private String courseName, courseTypeL, courseTypeS, roomName, className;
    private LocalTime timeStart, timeEnd;
    private int duration;
    private int dayOfTheWeek;
    private AcademicYear academicYear;
    private int semester;

    public ScheduleDay(int sc_id, String remoteId, String courseName,
                       String courseTypeL, String courseTypeS,
                       String roomName, String className,
                       LocalTime timeStart, LocalTime timeEnd,
                       int duration, int dayOfTheWeek, AcademicYear academicYear, int semester) {
        this.sc_id = sc_id;
        this.remoteId = remoteId;
        this.courseName = courseName;
        this.courseTypeL = courseTypeL;
        this.courseTypeS = courseTypeS;
        this.roomName = roomName;
        this.className = className;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        this.dayOfTheWeek = dayOfTheWeek;
        this.academicYear = academicYear;
        this.semester = semester;
    }

    public ScheduleDay(String remoteId, String courseName, String courseTypeL,
                       String courseTypeS, String roomName,
                       String className, LocalTime timeStart,
                       LocalTime timeEnd, int duration,
                       int dayOfTheWeek, AcademicYear academicYear, int semester) {
        this.remoteId = remoteId;
        this.courseName = courseName;
        this.courseTypeL = courseTypeL;
        this.courseTypeS = courseTypeS;
        this.roomName = roomName;
        this.className = className;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
        this.dayOfTheWeek = dayOfTheWeek;
        this.academicYear = academicYear;
        this.semester = semester;
    }

    public int getSemester() {
        return semester;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public int getSc_id() {
        return sc_id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseTypeL() {
        return courseTypeL;
    }

    public String getCourseTypeS() {
        return courseTypeS;
    }

    public String getRoomName() {
        return roomName;
    }

    /**
     * "Nome da turma".
     */
    public String getClassName() {
        return className;
    }

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public LocalTime getTimeEnd() {
        return timeEnd;
    }

    public int getDuration() {
        return duration;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    @Override
    public String toString() {
        return "ScheduleDay{" +
                "sc_id=" + sc_id +
                ", remoteId='" + remoteId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseTypeL='" + courseTypeL + '\'' +
                ", courseTypeS='" + courseTypeS + '\'' +
                ", roomName='" + roomName + '\'' +
                ", className='" + className + '\'' +
                ", timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", duration=" + duration +
                ", dayOfTheWeek=" + dayOfTheWeek +
                ", academicYear=" + academicYear +
                ", semester=" + semester +
                '}';
    }
}
