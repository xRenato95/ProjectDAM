package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 29/05/2016 : 03:55.
 */
public class TGrade implements ITable {
    private static final String DB_TABLE = "GRADE";
    private static final String[] COLUMNS = new String[]{
    /*0 */        "g_id",
    /*1 */        "g_date",
    /*2 */        "g_course_name",
    /*3 */        "g_pvep_name",
    /*4 */        "g_avi_name",
    /*5 */        "g_evaluation_name",
    /*6 */        "g_average_grade",
    /*7 */        "g_pvn_observation",
    /*8 */        "g_pc_observation",
    /*9 */        "g_observation",
    /*10*/        "g_grade",
    /*11*/        "g_student_statute",
    /*12*/        "g_assiduity",
    /*13*/        "g_est_assiduidade",
    /*14*/        "g_minimum_grade",
    /*15*/        "g_grade_final_weight",
    /*16*/        "g_era",
    /*17*/        "g_state",
    /*18*/        "g_semester",
    /*19*/        "g_academic_year"
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
        sb.append(COLUMNS[19]+" TEXT NULL) ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
