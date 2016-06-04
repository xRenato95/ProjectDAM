package dam.projects.projectdam.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.EventLog;
import android.util.Log;

import org.joda.time.DateTime;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import dam.projects.projectdam.helpers.Helpers;
import dam.projects.projectdam.helpers.HelpersDate;
import dam.projects.projectdam.objects.AcademicYear;
import dam.projects.projectdam.objects.EventFinal;
import dam.projects.projectdam.objects.EventType;
import dam.projects.projectdam.objects.Friend;
import dam.projects.projectdam.objects.Grade;
import dam.projects.projectdam.objects.Member;
import dam.projects.projectdam.objects.MemberInvite;
import dam.projects.projectdam.objects.ScheduleDay;
import dam.projects.projectdam.objects.Student;
import dam.projects.projectdam.objects.VisibilityType;
import dam.projects.projectdam.sqlite.tables.TEvent;
import dam.projects.projectdam.sqlite.tables.TFriends;
import dam.projects.projectdam.sqlite.tables.TGrade;
import dam.projects.projectdam.sqlite.tables.TGradeNotification;
import dam.projects.projectdam.sqlite.tables.TMember;
import dam.projects.projectdam.sqlite.tables.TMemberInv;
import dam.projects.projectdam.sqlite.tables.TSchedule;
import dam.projects.projectdam.sqlite.tables.TStudent;

/**
 * Created by Renato on 21/04/2016 : 23:04.
 * Class that represents SQLite database with methods to manipulate data inside the tables.
 */
public class DataBase extends android.database.sqlite.SQLiteOpenHelper {
    private static final String DB_NAME = "app_upt_db.s3db";
    private static final int DB_VERSION = 21;

    /*
     * Tables
     */
    private TStudent tStudent;
    private TFriends tFriends;
    private TSchedule tSchedule;
    private TGrade tGrade;
    private TEvent tEvent;
    private TMember tMember;
    private TMemberInv tMemberInv;
    private TGradeNotification tGradeNotification;

    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        tStudent = new TStudent();
        tFriends = new TFriends();
        tSchedule = new TSchedule();
        tGrade = new TGrade();
        tEvent = new TEvent();
        tMember = new TMember();
        tMemberInv = new TMemberInv();
        tGradeNotification = new TGradeNotification();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tStudent.getCreateString());
        db.execSQL(tFriends.getCreateString());
        db.execSQL(tSchedule.getCreateString());
        db.execSQL(tGrade.getCreateString());
        db.execSQL(tEvent.getCreateString());
        db.execSQL(tMember.getCreateString());
        db.execSQL(tMemberInv.getCreateString());
        db.execSQL(tGradeNotification.getCreateString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE FRIENDS");
        db.execSQL(tFriends.getCreateString());
        db.execSQL(tGrade.getCreateString());
        db.execSQL(tEvent.getCreateString());
        db.execSQL(tMember.getCreateString());
        db.execSQL(tMemberInv.getCreateString());
        db.execSQL(tGradeNotification.getCreateString());
    }

    //region Student Procedures
    public boolean insertStudent(String id, String name, String mail, String foto, String token) {
        if (checkStudent()) return false;
        this.getReadableDatabase().execSQL("INSERT INTO " +
                "" + tStudent.getTableName() + " " +
                "( " + tStudent.getColumns()[1] + ", " + tStudent.getColumns()[2] + ", " +
                "" + tStudent.getColumns()[3] + ", " + tStudent.getColumns()[4] + ", "+ tStudent.getColumns()[5]+" ) " +
                "VALUES ('"+id+"', '"+name+"', '"+mail+"', '"+foto+"', '"+token+"')");
        return true;
    }

    public boolean checkStudent() {
        Cursor query = this.getReadableDatabase()
                .rawQuery("SELECT * FROM "+tStudent.getTableName(), null);
        return query != null && query.getCount() > 0;
    }

    public Student getStudent() {
        Cursor query = this.getReadableDatabase()
                .rawQuery("SELECT * FROM "+tStudent.getTableName(), null);

        if (query != null && query.getCount() > 0 && query.moveToNext()) {
            int db_id = query.getInt(0);
            String id = query.getString(1);
            String name = query.getString(2);
            String mail = query.getString(3);
            String photo = query.getString(4);
            String token = query.getString(5);
            query.close();
            return new Student(db_id, id, name, mail, photo, token);
        } else {
            return null;
        }
    }

    public void deleteStudent() {
        this.getReadableDatabase().execSQL("DELETE FROM "+tStudent.getTableName());
    }

    //endregion

    // region Friends Procedures
    public synchronized boolean insertFriends(Friend[] friends, boolean isInvite) {
        deleteFriends(isInvite);
        String stringBitmap;
        for (Friend each : friends) {
            stringBitmap = Helpers.bitmapToString(each.getPhoto());
            this.getReadableDatabase().execSQL("INSERT INTO " +
                    "" + tFriends.getTableName() + " " +
                    "( " + tFriends.getColumns()[1] + ", " + tFriends.getColumns()[2] + ", " +
                    "" + tFriends.getColumns()[3] + ", " + tFriends.getColumns()[4] + ", " +
                    "" + tFriends.getColumns()[5] + ", " + tFriends.getColumns()[6] + ") " +
                    "VALUES ('" + each.getNumber() + "', '" + each.getName() + "', '" + each.getMail() + "', '" + each.getPhotoURL() + "', '" + (stringBitmap != null ? stringBitmap : "") + "', " + (each.isInvite() ? 1 : 0) + ")");
        }
        return true;
    }

    public synchronized void updateFriendPhoto(int student_number, Bitmap photo) {
        String stringBitmap = Helpers.bitmapToString(photo);
        if (stringBitmap != null) {
            this.getReadableDatabase().execSQL("UPDATE " + tFriends.getTableName() + " " +
                    "SET " + tFriends.getColumns()[5] + " ='"+stringBitmap+"' " +
                    "WHERE " + tFriends.getColumns()[1] + " ='"+student_number+"'");
        }
    }

    public synchronized Friend[] getFriends(boolean isInvite) {
        int db_id;
        String number,name,mail,photo;
        boolean invite;
        Bitmap bitmap;
        ArrayList<Friend> friends = new ArrayList<>();
        Cursor query = this.getReadableDatabase()
                .rawQuery("SELECT * FROM "+tFriends.getTableName()+ " " +
                        "WHERE "+tFriends.getColumns()[6]+" = "+(isInvite?1:0), null);

        if (query != null && query.getCount() > 0) {
            while (query.moveToNext()) {
                db_id = query.getInt(0);
                number = query.getString(1);
                name = query.getString(2);
                mail = query.getString(3);
                photo = query.getString(4);
                // done
                bitmap = Helpers.stringToBitMap(query.getString(5));
                invite = (query.getInt(6) == 1);
                // add friends
                friends.add(new Friend(db_id, name, number, mail, photo, bitmap, invite));
            }
            query.close();
        }
        return friends.toArray(new Friend[friends.size()]);
    }

    //FIXME
    /*public void deleteFriend(String number, boolean isInvite) {
        Log.e("Entrada: ", number + " " + isInvite);
        Log.e("DB111-", Arrays.toString(getFriends(true)));
        int a = this.getReadableDatabase().delete(tFriends.getTableName(),
                                        tFriends.getColumns()[1]+"=? and "+tFriends.getColumns()[6]+"=?",
                                        new String[] {number, (isInvite?"1":"0")});
        Log.e("test", a+"");
        Log.e("DB222-", Arrays.toString(getFriends(true)));
    }*/

    // Test mode
    public synchronized void deleteFriends(boolean isInvite) {
        this.getReadableDatabase().execSQL("DELETE FROM "+tFriends.getTableName()+ " " +
                "WHERE "+tFriends.getColumns()[6]+" = "+(isInvite?1:0));
    }

    public synchronized Friend[] getNewInvites(Friend[] remoteInvites) {
        ArrayList<Friend> newInvites = new ArrayList<>();
        Friend[] storedInvites = getFriends(true);
        if (remoteInvites.length != storedInvites.length) {
            // search for differences
            boolean exists;
            for (Friend each : remoteInvites) {
                exists = searchFriend(storedInvites, each);
                if (!exists) {
                    newInvites.add(each);
                }
            }
        }
        return newInvites.toArray(new Friend[newInvites.size()]);
    }

    private synchronized boolean searchFriend(Friend[] list, Friend friend) {
        for (int i = 0; i < list.length; ++i) {
            if (list[i].getNumber().equals(friend.getNumber())){
                return true;
            }
        }
        return false;
    }
    // endregion

    //region Schedule Procedures
    public void insertScheduleDay(ScheduleDay scheduleDay) {
        // avoids duplicated
        deleteSchedule(scheduleDay.getRemoteId());
        // insert
        ContentValues values = new ContentValues();
        values.put(tSchedule.getColumns()[1], scheduleDay.getCourseName());
        values.put(tSchedule.getColumns()[2], scheduleDay.getCourseTypeL());
        values.put(tSchedule.getColumns()[3], scheduleDay.getCourseTypeS());
        values.put(tSchedule.getColumns()[4], scheduleDay.getRoomName());
        values.put(tSchedule.getColumns()[5], scheduleDay.getClassName());
        values.put(tSchedule.getColumns()[6], HelpersDate.hourToString(scheduleDay.getTimeStart(), HelpersDate.HOUR_FORMAT_BIG));
        values.put(tSchedule.getColumns()[7], HelpersDate.hourToString(scheduleDay.getTimeEnd(), HelpersDate.HOUR_FORMAT_BIG));
        values.put(tSchedule.getColumns()[8], scheduleDay.getDuration()+"");
        values.put(tSchedule.getColumns()[9], scheduleDay.getDayOfTheWeek()+"");
        values.put(tSchedule.getColumns()[10], scheduleDay.getAcademicYear().toString());
        values.put(tSchedule.getColumns()[11], scheduleDay.getRemoteId());
        values.put(tSchedule.getColumns()[12], scheduleDay.getSemester());
        this.getReadableDatabase().insert(tSchedule.getTableName(), null, values);
    }

    public void insertScheduleDays(ScheduleDay[] scheduleDays) {
        for (ScheduleDay each : scheduleDays) {
            insertScheduleDay(each);
        }
    }

    public void deleteSchedule(String remoteId) {
        this.getReadableDatabase().delete(tSchedule.getTableName(),
                                        tSchedule.getColumns()[11]+" = ?",
                                        new String[]{remoteId});
    }

    public ScheduleDay[] getScheduleDays(AcademicYear academicYear, int semester) {
        ArrayList<ScheduleDay> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM " + tSchedule.getTableName() + " WHERE " +
                "" + tSchedule.getColumns()[10] + " = '"+ academicYear.toString() +"' AND " +
                "" + tSchedule.getColumns()[12] + " = '"+ semester+"' ",null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(new ScheduleDay(cursor.getInt(0), cursor.getString(11),
                                            cursor.getString(1), cursor.getString(2),
                                            cursor.getString(3), cursor.getString(4),
                                            cursor.getString(5), HelpersDate.stringToHour(cursor.getString(6)),
                                            HelpersDate.stringToHour(cursor.getString(7)),
                                            Integer.parseInt(cursor.getString(8)),
                                            Integer.parseInt(cursor.getString(9)), AcademicYear.toObject(cursor.getString(10)),
                                            Integer.parseInt(cursor.getString(12))));
                }
            }
            cursor.close();
        }
        return list.toArray(new ScheduleDay[list.size()]);
    }
    //endregion

    //region Grade Procedures
    private void insertGrade(Grade grade) {
        // insert
        ContentValues values = new ContentValues();
        values.put(tGrade.getColumns()[1], grade.getDateBegin() == null ? null : grade.getDateBegin().toString());
        values.put(tGrade.getColumns()[2], grade.getCourseName());
        values.put(tGrade.getColumns()[3], grade.getPvepName());
        values.put(tGrade.getColumns()[4], grade.getAviName());
        values.put(tGrade.getColumns()[5], grade.getEvaluationName());
        values.put(tGrade.getColumns()[6], grade.getAverageGrade());
        values.put(tGrade.getColumns()[7], grade.getPvnObservation());
        values.put(tGrade.getColumns()[8], grade.getPcObservation());
        values.put(tGrade.getColumns()[9], grade.getObservation());
        values.put(tGrade.getColumns()[10], grade.getGrade());
        values.put(tGrade.getColumns()[11], grade.getStudentStatute());
        values.put(tGrade.getColumns()[12], grade.getAssiduity());
        values.put(tGrade.getColumns()[13], grade.getEstAssiduidade());
        values.put(tGrade.getColumns()[14], grade.getMinimumGrade());
        values.put(tGrade.getColumns()[15], grade.getGradeFinalWeight());
        values.put(tGrade.getColumns()[16], grade.getEra());
        values.put(tGrade.getColumns()[17], grade.getState());
        values.put(tGrade.getColumns()[18], grade.getSemester());
        values.put(tGrade.getColumns()[19], grade.getAcademicYear().toString());
        this.getReadableDatabase().insert(tGrade.getTableName(), null, values);
    }

    public void insertGrades(Grade[] grades) {
        if (grades.length > 0) {
            deleteGrades(grades[0].getAcademicYear(), grades[0].getSemester());
            for (Grade each : grades) {
                insertGrade(each);
            }
        }
    }

    public void deleteGrades(AcademicYear academicYear, int semester) {
        this.getReadableDatabase().delete(tGrade.getTableName(),
                tGrade.getColumns()[18]+" = ? AND " + tGrade.getColumns()[19] + " = ?",
                new String[]{String.valueOf(semester), academicYear.toString()});
    }

    public Grade[] getGrades(AcademicYear academicYear, int semester) {
        ArrayList<Grade> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM " + tGrade.getTableName() + " WHERE " +
                "" + tGrade.getColumns()[19] + " = '"+ academicYear.toString() +"' AND " +
                "" + tGrade.getColumns()[18] + " = '"+ semester+"' ",null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(new Grade(
                            cursor.getInt(0),
                            cursor.getString(1) == null ? null : new DateTime(cursor.getString(1)),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10),
                            cursor.getString(11),
                            cursor.getString(12),
                            Integer.parseInt(cursor.getString(13)),
                            Integer.parseInt(cursor.getString(14)),
                            Integer.parseInt(cursor.getString(15)),
                            cursor.getString(16)==null?null:Integer.parseInt(cursor.getString(16)),
                            cursor.getString(17)==null?null:Integer.parseInt(cursor.getString(17)),
                            cursor.getString(18)==null?null:Integer.parseInt(cursor.getString(18)),
                            AcademicYear.toObject(cursor.getString(19))
                    ));
                }
            }
            cursor.close();
        }
        return list.toArray(new Grade[list.size()]);

        //TODO TESTE NOTAS NICE
        /*ArrayList<Grade> list = new ArrayList<>();

        list.add(new Grade(new DateTime(2016,06,01,21,10,00),"Algebra","Algebra","Época normal",
                "Prova Escrita – Exame Final","Teste 2","10,1",null,"horário e local para consulta de prova: ",
                "15,4","15,4",null,1,1,45,0,0,semester,academicYear));

        return list.toArray(new Grade[list.size()]);*/
    }

    public Grade[] getNewGrades(Grade[] remoteGrades) {
        ArrayList<Grade> newGrades = new ArrayList<>();
        LocalDateTime ldt = new LocalDateTime();
        int ano = ldt.getYear();
        int mes = ldt.getMonthOfYear();
        int semester;
        AcademicYear academicYear;
        if(mes>=9){
            academicYear = new AcademicYear(ano,ano+1);
            semester = 1;
        }
        else if(mes<3){
            academicYear = new AcademicYear(ano-1,ano);
            semester = 1;
        }
        else{
            academicYear = new AcademicYear(ano-1,ano);
            semester = 2;
        }
        Grade[] storedGrades = getGradesNotification();
        //TODO TESTE NOTAS NICE
        /*Grade[] temp = new Grade[storedGrades.length+1];
        int j = 0;
        for(int i = 0; i<storedGrades.length; i++){
            temp[i]=storedGrades[i];
            j++;
        }
        temp[j]= new Grade(new DateTime(),"Algebra","Algebra","Época normal",
                "Prova Escrita – Exame Final","Teste 2","10,1",null,"horário e local para consulta de prova: ",
                "15,4","15,4",null,1,1,45,0,0,semester,academicYear);

                if (remoteGrades.length != temp.length) {
            // search for differences
            for (Grade each : temp) {
                boolean exist = false;
                for(int i=0;i<remoteGrades.length;i++){
                    if(remoteGrades[i].toString().equals(each.toString())){
                        exist = true;
                    }
                }
                if(!exist){
                    newGrades.add(each);
                }
            }
        }*/
        if (remoteGrades.length != storedGrades.length) {
            // search for differences
            for (Grade each : storedGrades) {
                boolean exist = false;
                for(int i=0;i<remoteGrades.length;i++){
                    if(remoteGrades[i].toString().equals(each.toString())){
                        exist = true;
                    }
                }
                if(!exist){
                    newGrades.add(each);
                }
            }
        }
        deleteGradesNotification();
        insertGradesNotification(remoteGrades);
        return newGrades.toArray(new Grade[newGrades.size()]);
    }

    //endregion

    //region Event Procedures
    private void insertEvent(EventFinal eventFinal) {
        // insert
        ContentValues values = new ContentValues();
        values.put(tEvent.getColumns()[1], eventFinal.getRemote_id());
        values.put(tEvent.getColumns()[2], eventFinal.getEventType().getType_id());
        values.put(tEvent.getColumns()[3], eventFinal.getEventType().getType_code());
        values.put(tEvent.getColumns()[4], eventFinal.getVisibilityType().getVisibility_id());
        values.put(tEvent.getColumns()[5], eventFinal.getVisibilityType().getVisibility_code());
        values.put(tEvent.getColumns()[6], eventFinal.getS_id());
        values.put(tEvent.getColumns()[7], eventFinal.getTitle());
        values.put(tEvent.getColumns()[8], eventFinal.getDescription());
        values.put(tEvent.getColumns()[9], HelpersDate.dateToString(eventFinal.getDateBegin(), HelpersDate.DATE_FORMAT));
        values.put(tEvent.getColumns()[10], eventFinal.getHourBegin()!=null?HelpersDate.hourToString(eventFinal.getHourBegin(), HelpersDate.HOUR_FORMAT_BIG):null);
        values.put(tEvent.getColumns()[11], eventFinal.getDateEnd()!=null?HelpersDate.dateToString(eventFinal.getDateEnd(), HelpersDate.DATE_FORMAT):null);
        values.put(tEvent.getColumns()[12], eventFinal.getHourEnd()!=null?HelpersDate.hourToString(eventFinal.getHourEnd(), HelpersDate.HOUR_FORMAT_BIG):null);
        values.put(tEvent.getColumns()[13], eventFinal.getDateOfCreation()!=null?eventFinal.getDateOfCreation().toString():null);
        values.put(tEvent.getColumns()[14], eventFinal.getLocal());
        values.put(tEvent.getColumns()[15], eventFinal.getNumberMembers());
        values.put(tEvent.getColumns()[16], eventFinal.getAverageRating());
        values.put(tEvent.getColumns()[17], 0); // feature for next release

        this.getReadableDatabase().insert(tEvent.getTableName(), null, values);
    }


    public void insertEvents(EventFinal[] event) {
        if (event.length == 0) {
            removeEvents(1);
            removeEvents(2);
            removeEvents(3);
        } else {
            removeEvents(event[0].getVisibilityType().getVisibility_id());
            for (EventFinal each : event) {
                insertEvent(each);
            }
        }
    }

    public EventFinal getEvent(int ev_id) {
        EventFinal eventFinal = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM " + tEvent.getTableName() + " WHERE " +
                "" + tEvent.getColumns()[0] + " = "+ev_id +" ",null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    eventFinal = new EventFinal(
                            cursor.getInt(0),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(6)),
                            new EventType(Integer.parseInt(cursor.getString(2)), cursor.getString(3)),
                            new VisibilityType(Integer.parseInt(cursor.getString(4)), cursor.getString(5)),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(14),
                            HelpersDate.stringToDate(cursor.getString(9)),
                            cursor.getString(11)!=null?HelpersDate.stringToDate(cursor.getString(11)):null,
                            cursor.getString(10)!=null?HelpersDate.stringToHour(cursor.getString(10)):null,
                            cursor.getString(12)!=null?HelpersDate.stringToHour(cursor.getString(12)):null,
                            new DateTime(cursor.getString(13)),
                            Integer.parseInt(cursor.getString(15)),
                            Double.parseDouble(cursor.getString(16))
                    );
                }
            }
            cursor.close();
        }
        return eventFinal;
    }

    public EventFinal[] getEvents(int visibility) {
        ArrayList<EventFinal> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM " + tEvent.getTableName() + " WHERE " +
                "" + tEvent.getColumns()[4] + " = '"+ visibility+"' ",null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(new EventFinal(
                            cursor.getInt(0),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(6)),
                            new EventType(Integer.parseInt(cursor.getString(2)), cursor.getString(3)),
                            new VisibilityType(Integer.parseInt(cursor.getString(4)), cursor.getString(5)),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(14),
                            HelpersDate.stringToDate(cursor.getString(9)),
                            cursor.getString(11)!=null?HelpersDate.stringToDate(cursor.getString(11)):null,
                            cursor.getString(10)!=null?HelpersDate.stringToHour(cursor.getString(10)):null,
                            cursor.getString(12)!=null?HelpersDate.stringToHour(cursor.getString(12)):null,
                            new DateTime(cursor.getString(13)),
                            Integer.parseInt(cursor.getString(15)),
                            Double.parseDouble(cursor.getString(16))
                    ));
                }
            }
            cursor.close();
        }
        return list.toArray(new EventFinal[list.size()]);
    }

    public void removeEvents(int visibility) {
        this.getReadableDatabase().delete(tEvent.getTableName(),
                tEvent.getColumns()[4]+" = ?",
                new String[]{String.valueOf(visibility)});
    }
    //endregion

    //region Members Procedures
    public void insertMembers(Member[] members) {
        if (members.length <= 0) return;
        deleteMembers(members[0].getEvent_id());
        for (Member each : members) {
            insertMember(each);
        }
    }

    private void insertMember(Member members) {
        // insert
        ContentValues values = new ContentValues();
        values.put(tMember.getColumns()[1], members.getEvent_id());
        values.put(tMember.getColumns()[2], members.getM_id());
        values.put(tMember.getColumns()[4], members.getStudent().getId());
        values.put(tMember.getColumns()[5], members.getStudent().getName());
        values.put(tMember.getColumns()[6], members.getStudent().getMail());
        values.put(tMember.getColumns()[7], members.getStudent().getPhoto());
        values.put(tMember.getColumns()[8], members.isFavorite()?"1":"0");
        values.put(tMember.getColumns()[9], String.valueOf(members.getRating()));
        this.getReadableDatabase().insert(tMember.getTableName(), null, values);
    }

    public Member[] getMembers(int event_id) {
        ArrayList<Member> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM " + tMember.getTableName() + " WHERE " +
                "" + tMember.getColumns()[1] + " = '"+event_id+"' ",null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(new Member(
                            cursor.getInt(0),
                            Integer.parseInt(cursor.getString(2)),
                            Integer.parseInt(cursor.getString(1)),
                            Integer.parseInt(cursor.getString(9)),
                            new Student(
                                    -1,
                                    cursor.getString(4),
                                    cursor.getString(5),
                                    cursor.getString(6),
                                    cursor.getString(7)
                            ),
                            cursor.getString(0).equals("1")
                    ));
                }
            }
            cursor.close();
        }
        return list.toArray(new Member[list.size()]);
    }

    /* event_id = remote id */
    public void deleteMembers(int event_id) {
        this.getReadableDatabase().delete(tMember.getTableName(),
                tMember.getColumns()[1]+" = ?",
                new String[]{String.valueOf(event_id)});
    }

    //endregion

    //region Member invite Procedures
    public void insertMemberInvs(MemberInvite[] memberInvite) {
        deleteMemberInvs();
        for (MemberInvite each : memberInvite) {
            insertMemberInv(each);
        }
    }

    private void insertMemberInv(MemberInvite memberInvite) {
        // insert
        ContentValues values = new ContentValues();
        values.put(tMemberInv.getColumns()[1], memberInvite.getInviteMember().getMe_id());
        values.put(tMemberInv.getColumns()[2], memberInvite.getInviteMember().getEvent_id());
        values.put(tMemberInv.getColumns()[3], memberInvite.getInviteMember().getM_id());
        values.put(tMemberInv.getColumns()[5], memberInvite.getInviteMember().getStudent().getId());
        values.put(tMemberInv.getColumns()[6], memberInvite.getInviteMember().getStudent().getName());
        values.put(tMemberInv.getColumns()[7], memberInvite.getInviteMember().getStudent().getMail());
        values.put(tMemberInv.getColumns()[8], memberInvite.getInviteMember().getStudent().getPhoto());
        values.put(tMemberInv.getColumns()[9], memberInvite.getInviteMember().isFavorite()?"1":"0");
        values.put(tMemberInv.getColumns()[10], String.valueOf(memberInvite.getInviteMember().getRating()));
        values.put(tMemberInv.getColumns()[11], memberInvite.getEvent().getEv_id());
        values.put(tMemberInv.getColumns()[12], memberInvite.getEvent().getRemote_id());
        values.put(tMemberInv.getColumns()[13], memberInvite.getEvent().getEventType().getType_id());
        values.put(tMemberInv.getColumns()[14], memberInvite.getEvent().getEventType().getType_code());
        values.put(tMemberInv.getColumns()[15], memberInvite.getEvent().getVisibilityType().getVisibility_id());
        values.put(tMemberInv.getColumns()[16], memberInvite.getEvent().getVisibilityType().getVisibility_code());
        values.put(tMemberInv.getColumns()[17], memberInvite.getEvent().getS_id());
        values.put(tMemberInv.getColumns()[18], memberInvite.getEvent().getTitle());
        values.put(tMemberInv.getColumns()[19], memberInvite.getEvent().getDescription());
        values.put(tMemberInv.getColumns()[20], HelpersDate.dateToString(memberInvite.getEvent().getDateBegin(), HelpersDate.DATE_FORMAT));
        values.put(tMemberInv.getColumns()[21], memberInvite.getEvent().getHourBegin()==null?null:HelpersDate.hourToString(memberInvite.getEvent().getHourBegin(), HelpersDate.HOUR_FORMAT_BIG));
        values.put(tMemberInv.getColumns()[22], memberInvite.getEvent().getDateEnd()==null?null:HelpersDate.dateToString(memberInvite.getEvent().getDateEnd(), HelpersDate.DATE_FORMAT));
        values.put(tMemberInv.getColumns()[23], memberInvite.getEvent().getHourEnd()==null?null:HelpersDate.hourToString(memberInvite.getEvent().getHourEnd(), HelpersDate.HOUR_FORMAT_BIG));
        values.put(tMemberInv.getColumns()[24], memberInvite.getEvent().getDateOfCreation().toString());
        values.put(tMemberInv.getColumns()[25], memberInvite.getEvent().getLocal());
        values.put(tMemberInv.getColumns()[26], memberInvite.getEvent().getNumberMembers());
        values.put(tMemberInv.getColumns()[27], memberInvite.getEvent().getAverageRating());
        values.put(tMemberInv.getColumns()[28], 0); // feature for next release
        this.getReadableDatabase().insert(tMemberInv.getTableName(), null, values);
    }

    public void deleteMemberInvs() {
        this.getReadableDatabase().execSQL("DELETE FROM "+ tMemberInv.getTableName());
    }

    public MemberInvite[] getMemberInvites() {
        ArrayList<MemberInvite> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM " + tMemberInv.getTableName(),null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(new MemberInvite(
                            cursor.getInt(0),
                            new Member (
                                Integer.parseInt(cursor.getString(1)),
                                Integer.parseInt(cursor.getString(3)),
                                Integer.parseInt(cursor.getString(2)),
                                Integer.parseInt(cursor.getString(10)),
                                new Student(
                                        -1,
                                        cursor.getString(5),
                                        cursor.getString(6),
                                        cursor.getString(7),
                                        cursor.getString(8)
                                ),
                                cursor.getString(9).equals("1")),
                            new EventFinal(
                                    Integer.parseInt(cursor.getString(11)),
                                    Integer.parseInt(cursor.getString(12)),
                                    Integer.parseInt(cursor.getString(17)),
                                    new EventType(Integer.parseInt(cursor.getString(13)), cursor.getString(14)),
                                    new VisibilityType(Integer.parseInt(cursor.getString(15)), cursor.getString(16)),
                                    cursor.getString(18),
                                    cursor.getString(19),
                                    cursor.getString(25),
                                    cursor.getString(20)==null?null:HelpersDate.stringToDate(cursor.getString(20)),
                                    cursor.getString(22)==null?null:HelpersDate.stringToDate(cursor.getString(22)),
                                    cursor.getString(21)==null?null:HelpersDate.stringToHour(cursor.getString(21)),
                                    cursor.getString(23)==null?null:HelpersDate.stringToHour(cursor.getString(23)),
                                    new DateTime(cursor.getString(24)),
                                    Integer.parseInt(cursor.getString(26)),
                                    Double.parseDouble(cursor.getString(27))
                            )
                    ));
                }
            }
            cursor.close();
        }
        return list.toArray(new MemberInvite[list.size()]);
    }
    //endregion

    //region Grade Notification Procedure
    /**
     * If you want this to be public, this method inserts a new grade without deleting the existing
     * grades on the table.
     */
    private void insertGradeNotification(Grade grade) {
        // insert
        ContentValues values = new ContentValues();
        values.put(tGradeNotification.getColumns()[1], grade.getDateBegin() == null ? null : grade.getDateBegin().toString());
        values.put(tGradeNotification.getColumns()[2], grade.getCourseName());
        values.put(tGradeNotification.getColumns()[3], grade.getPvepName());
        values.put(tGradeNotification.getColumns()[4], grade.getAviName());
        values.put(tGradeNotification.getColumns()[5], grade.getEvaluationName());
        values.put(tGradeNotification.getColumns()[6], grade.getAverageGrade());
        values.put(tGradeNotification.getColumns()[7], grade.getPvnObservation());
        values.put(tGradeNotification.getColumns()[8], grade.getPcObservation());
        values.put(tGradeNotification.getColumns()[9], grade.getObservation());
        values.put(tGradeNotification.getColumns()[10], grade.getGrade());
        values.put(tGradeNotification.getColumns()[11], grade.getStudentStatute());
        values.put(tGradeNotification.getColumns()[12], grade.getAssiduity());
        values.put(tGradeNotification.getColumns()[13], grade.getEstAssiduidade());
        values.put(tGradeNotification.getColumns()[14], grade.getMinimumGrade());
        values.put(tGradeNotification.getColumns()[15], grade.getGradeFinalWeight());
        values.put(tGradeNotification.getColumns()[16], grade.getEra());
        values.put(tGradeNotification.getColumns()[17], grade.getState());
        values.put(tGradeNotification.getColumns()[18], grade.getSemester());
        values.put(tGradeNotification.getColumns()[19], grade.getAcademicYear().toString());
        this.getReadableDatabase().insert(tGradeNotification.getTableName(), null, values);
    }

    /**
     * Inserts an array of grades, deleting all the existing grades on the table
     */
    public void insertGradesNotification(Grade[] grades) {
        deleteGradesNotification();
        for (Grade each : grades) {
            insertGradeNotification(each);
        }
    }

    /**
     * Deletes all the existing grades on the table
     */
    public void deleteGradesNotification() {
        this.getReadableDatabase().execSQL("DELETE FROM "+tGradeNotification.getTableName());
    }

    public Grade[] getGradesNotification() {
        ArrayList<Grade> list = new ArrayList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("" +
                "SELECT * FROM "+tGradeNotification.getTableName(),null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(new Grade(
                            cursor.getInt(0),
                            cursor.getString(1) == null ? null : new DateTime(cursor.getString(1)),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8),
                            cursor.getString(9),
                            cursor.getString(10),
                            cursor.getString(11),
                            cursor.getString(12),
                            Integer.parseInt(cursor.getString(13)),
                            Integer.parseInt(cursor.getString(14)),
                            Integer.parseInt(cursor.getString(15)),
                            cursor.getString(16)==null?null:Integer.parseInt(cursor.getString(16)),
                            cursor.getString(17)==null?null:Integer.parseInt(cursor.getString(17)),
                            cursor.getString(18)==null?null:Integer.parseInt(cursor.getString(18)),
                            AcademicYear.toObject(cursor.getString(19))
                    ));
                }
            }
            cursor.close();
        }
        return list.toArray(new Grade[list.size()]);
    }
    //endregion
}
