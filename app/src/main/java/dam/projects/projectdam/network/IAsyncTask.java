package dam.projects.projectdam.network;

import dam.projects.projectdam.helpers.Code;
import dam.projects.projectdam.json.JSONClass;
import dam.projects.projectdam.sqlite.DataBase;

/**
 * Created by Renato on 15/04/2016 : 00:08.
 * Interface with necessary attributes for asynchronous network tasks.
 */
public interface IAsyncTask {
    public Code operation(RequestDetail req, Class<? extends JSONClass> json, DataBase db);
}
