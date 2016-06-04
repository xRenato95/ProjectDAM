package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 20/05/2016 : 17:07.
 */
public class TSchedule implements ITable {
    private static final String DB_TABLE = "SCHEDULE";
    private static final String[] COLUMNS = new String[]{
    /*0 , 1*/        "sc_id", "sc_courseName",
    /*2 , 3*/        "sc_courseTypeL", "sc_courseTypeS",
    /*4 , 5*/        "sc_roomName", "sc_className",
    /*6 , 7*/        "sc_timeStart", "sc_timeEnd",
    /*8 , 9*/        "sc_duration", "sc_weekDay",
    /*10,11*/        "sc_academicYear", "sc_remoteId",
    /*12,--*/        "sc_semester",
    };

    @Override
    public String getTableName() {
        return DB_TABLE;
    }

    @Override
    public String getCreateString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ");
        sb.append(" ( "+COLUMNS[0]+" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COLUMNS[1]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[2]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[3]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[4]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[5]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[6]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[7]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[8]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[9]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[10]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[11]+" TEXT NOT NULL, ");
        sb.append(COLUMNS[12]+" TEXT NOT NULL) ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
