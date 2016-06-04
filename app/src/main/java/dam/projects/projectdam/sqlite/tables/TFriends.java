package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 04/05/2016 : 00:24.
 * SQLite friends table.
 */
public class TFriends implements ITable {
    private static final String DB_TABLE = "FRIENDS";
    private static final String[] COLUMNS = new String[]{
            "f_id", "f_number", "f_name", "f_mail", "f_photo", "f_bitmap", "f_invite"
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
        sb.append(COLUMNS[6]+" INTEGER NOT NULL DEFAULT '-1') ");
        return sb.toString();
    }

    @Override
    public String[] getColumns() {
        return COLUMNS;
    }
}
