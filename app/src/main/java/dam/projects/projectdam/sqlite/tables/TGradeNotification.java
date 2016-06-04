package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 04/06/2016 : 15:31.
 */
public class TGradeNotification implements ITable {
    private static final String DB_TABLE = "GRADE_NOTIFICATION";
    private static final String[] COLUMNS = new String[]{
    /*0 */        "gn_id",
    /*1 */        "gn_date",
    /*2 */        "gn_course_name",
    /*3 */        "gn_pvep_name",
    /*4 */        "gn_avi_name",
    /*5 */        "gn_evaluation_name",
    /*6 */        "gn_average_grade",
    /*7 */        "gn_pvn_observation",
    /*8 */        "gn_pc_observation",
    /*9 */        "gn_observation",
    /*10*/        "gn_grade",
    /*11*/        "gn_student_statute",
    /*12*/        "gn_assiduity",
    /*13*/        "gn_est_assiduidade",
    /*14*/        "gn_minimum_grade",
    /*15*/        "gn_grade_final_weight",
    /*16*/        "gn_era",
    /*17*/        "gn_state",
    /*18*/        "gn_semester",
    /*19*/        "gn_academic_year"
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
