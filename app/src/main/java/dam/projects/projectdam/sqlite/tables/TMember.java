package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 01/06/2016 : 00:24.
 */
public class TMember implements ITable {
    private static final String DB_TABLE = "MEMBER";
    private static final String[] COLUMNS = new String[] {
    /*00*/        "me_id",
    /*01*/        "m_e_id",
    /*02*/        "m_id",
    /*03*/        "m_student_id",
    /*04*/        "m_student_number",
    /*05*/        "m_student_name",
    /*06*/        "m_student_mail",
    /*07*/        "m_student_photo",
    /*08*/        "m_favorite",
    /*09*/        "m_rating"
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
        sb.append(COLUMNS[1]+" TEXT NULL, ");
        sb.append(COLUMNS[2]+" TEXT NULL, ");
        sb.append(COLUMNS[3]+" TEXT NULL, ");
        sb.append(COLUMNS[4]+" TEXT NULL, ");
        sb.append(COLUMNS[5]+" TEXT NULL, ");
        sb.append(COLUMNS[6]+" TEXT NULL, ");
        sb.append(COLUMNS[7]+" TEXT NULL, ");
        sb.append(COLUMNS[8]+" TEXT NULL, ");
        sb.append(COLUMNS[9]+" TEXT NULL) ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
