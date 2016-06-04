package dam.projects.projectdam.sqlite.tables;

/**
 * Created by Renato on 21/04/2016 : 22:32.
 * Interface that contains all required methods for SQLite tables.
 */
public interface ITable {
    /**
     * Method that returns the table's name.
     * @return table's name
     */
    public String getTableName();

    /**
     * Method that returns the SQL instruction to create the table.
     * @return SQL instruction to create the table
     */
    public String getCreateString();

    /**
     * Method that returns the columns of the table (name).
     * @return Array with the name of the columns
     */
    public String[] getColumns();
}
