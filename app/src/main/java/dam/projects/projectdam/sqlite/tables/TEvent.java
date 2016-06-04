package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 30/05/2016 : 01:59.
 */
public class TEvent implements ITable {
    private static final String DB_TABLE = "EVENT";
    private static final String[] COLUMNS = new String[]{
    /* 0*/    "ev_id",
    /* 1*/    "e_id",
    /* 2*/    "e_type_id",
    /* 3*/    "e_type_code",
    /* 4*/    "e_visibi_id",
    /* 5*/    "e_visibi_code",
    /* 6*/    "e_admin",
    /* 7*/    "e_title",
    /* 8*/    "e_desc",
    /* 9*/    "e_date_begin",
    /*10*/    "e_hour_begin",
    /*11*/    "e_date_end",
    /*12*/    "e_hour_end",
    /*13*/    "e_date_create",
    /*14*/    "e_local",
    /*15*/    "e_number_members",
    /*16*/    "e_average_rating",
    /*17*/    "e_old"
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
        sb.append(COLUMNS[17]+" TEXT NULL) ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
