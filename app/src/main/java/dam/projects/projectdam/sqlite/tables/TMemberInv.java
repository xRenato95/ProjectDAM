package dam.projects.projectdam.sqlite.tables;

/**
 * Created by renat on 01/06/2016 : 01:07.
 */
public class TMemberInv implements ITable {
    private static final String DB_TABLE = "MEMBER_INV";
    private static final String[] COLUMNS = new String[] {
    /*00*/        "mi_id",
    /*01*/        "me_id",
    /*02*/        "m_e_id",
    /*03*/        "m_id",
    /*04*/        "m_student_id",
    /*05*/        "m_student_number",
    /*06*/        "m_student_name",
    /*07*/        "m_student_mail",
    /*08*/        "m_student_photo",
    /*09*/        "m_favorite",
    /*10*/        "m_rating",
    /*11*/    "ev_id",
    /*12*/    "e_id",
    /*13*/    "e_type_id",
    /*14*/    "e_type_code",
    /*15*/    "e_visibi_id",
    /*16*/    "e_visibi_code",
    /*17*/    "e_admin",
    /*18*/    "e_title",
    /*19*/    "e_desc",
    /*20*/    "e_date_begin",
    /*21*/    "e_hour_begin",
    /*22*/    "e_date_end",
    /*23*/    "e_hour_end",
    /*24*/    "e_date_create",
    /*25*/    "e_local",
    /*26*/    "e_number_members",
    /*27*/    "e_average_rating",
    /*28*/    "e_old"
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
        sb.append(COLUMNS[9]+" TEXT NULL, ");
        sb.append(COLUMNS[10]+" TEXT NULL, ");
        sb.append(COLUMNS[11]+" TEXT NULL, ");
        sb.append(COLUMNS[12]+" TEXT NULL, ");
        sb.append(COLUMNS[13]+" TEXT NULL, ");
        sb.append(COLUMNS[14]+" TEXT NULL, ");
        sb.append(COLUMNS[15]+" TEXT NULL, ");
        sb.append(COLUMNS[16]+" TEXT NULL, ");
        sb.append(COLUMNS[17]+" TEXT NULL, ");
        sb.append(COLUMNS[18]+" TEXT NULL, ");
        sb.append(COLUMNS[19]+" TEXT NULL, ");
        sb.append(COLUMNS[20]+" TEXT NULL, ");
        sb.append(COLUMNS[21]+" TEXT NULL, ");
        sb.append(COLUMNS[22]+" TEXT NULL, ");
        sb.append(COLUMNS[23]+" TEXT NULL, ");
        sb.append(COLUMNS[24]+" TEXT NULL, ");
        sb.append(COLUMNS[25]+" TEXT NULL, ");
        sb.append(COLUMNS[26]+" TEXT NULL, ");
        sb.append(COLUMNS[27]+" TEXT NULL, ");
        sb.append(COLUMNS[28]+" TEXT NULL) ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
