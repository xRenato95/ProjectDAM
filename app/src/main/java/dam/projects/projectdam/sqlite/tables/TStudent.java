package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 21/04/2016 : 22:31.
 * SQLite student table.
 */
public class TStudent implements ITable {
    private static final String DB_TABLE = "STUDENT";
    private static final String[] COLUMNS = new String[]{
            "s_id", "s_number", "s_name", "s_mail", "s_foto", "s_token"
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
        sb.append(COLUMNS[5]+" TEXT NULL) ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
