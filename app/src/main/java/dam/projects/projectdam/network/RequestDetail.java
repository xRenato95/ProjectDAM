package dam.projects.projectdam.network;
@SuppressWarnings("unused")

/**
 * Created by Renato on 10/04/2016 : 02:51.
 * Enumerator used to store all the information related to HTTP requests and API calls.
 * @version 1.0
 */
public enum RequestDetail {
    // region LOGIN REQUEST DETAILS
    SIUPT_LOGIN     ("http://siupt.upt.pt/api/users.php", "GET", "pessoa", new String[] {"key", "func", "user", "pass", "type"}),
    LOGIN_REQUEST   ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "login_student", new String[] {"method", "s_id", "s_name", "s_mail", "s_photo"}),
    LOGOUT_REQUEST  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "logout_student", new String[] {"method", "s_id", "token"}),
    LOGOUT_ALL_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "logout_all_student", new String[] {"method", "s_id"}),
    // endregion

    // region FRIEND REQUEST DETAILS
    ADD_FRIEND_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "add_friend", new String[] {"method", "s_id", "token", "s_id_friend"}),
    ACP_FRIEND_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "acp_friend", new String[] {"method", "s_id", "token", "s_id_friend"}),
    GET_FRIENDS_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_friends", new String[] {"method", "s_id", "token"}),
    GET_FRI_INV_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_friends_inv", new String[] {"method", "s_id", "token"}),
    RMV_FRIEND_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "rmv_friend", new String[] {"method", "s_id", "token", "s_id_friend"}),
    DCL_FRIEND_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "dcl_friend", new String[] {"method", "s_id", "token", "s_id_friend"}),
    // endregion

    CHK_STUDENT_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "chk_student", new String[] {"method", "s_id", "token", "s_id_check"}),

    // region SCHEDULE REQUEST DETAILS
    SIUPT_SCHED_REQ ("http://siupt.upt.pt/api/core.php", "GET", "horario", new String[] {"key", "func", "aluno", "anolecti", "semestre"}),
    // endregion

    // region EVENT REQUEST DETAILS
    ADD_EVENT_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "add_event", new String[] {"method", "s_id", "token", "type_e_id", "visib_e_id", "title", "descr", "date_begin", "time_begin", "date_end", "time_end", "local"}),
    RMV_EVENT_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "rmv_event", new String[] {"method", "s_id", "token", "event_id"}),
    GET_PRI_EV_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_pri_events", new String[] {"method", "s_id", "token"}),
    GET_FRI_EV_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_fri_events", new String[] {"method", "s_id", "token"}),
    GET_PUB_EV_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_pub_events", new String[] {"method", "s_id", "token"}),
    RATE_EVENT_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "rate_event", new String[] {"method", "s_id", "token", "event_id", "rate"}),
    UNRATE_EVENT_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "unrate_event", new String[] {"method", "s_id", "token", "event_id"}),
    FAV_EVENT_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "fav_event", new String[] {"method", "s_id", "token", "event_id"}),
    UNFAV_EVENT_REQ  ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "unfav_event", new String[] {"method", "s_id", "token", "event_id"}),
    // endregion

    // region MEMBERS EVENT REQUEST DETAILS
    ADD_MEMBER_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "add_member", new String[] {"method", "s_id", "token", "event_id", "s_member"}),
    ACP_MEMBER_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "acp_member", new String[] {"method", "s_id", "token", "event_id", "s_member"}),
    GET_MEMBERS_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_members", new String[] {"method", "s_id", "token", "event_id"}),
    GET_MEM_INV_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "get_members_inv", new String[] {"method", "s_id", "token"}),
    RMV_MEMBER_REQ ("http://alunos.uportu.pt/~35650/api/api.php", "POST", "rmv_member", new String[] {"method", "s_id", "token", "event_id", "s_member"}),
    // endregion

    // region MARKS REQUEST DETAILS
    SIUPT_GRADE_REQ ("http://siupt.upt.pt/api/core.php", "GET", "provas", new String[] {"key", "func", "aluno", "anolecti", "semestre", "epoca", "estado"}),
    // endregion
    ;

    // resource address
    private final String url;
    // HTTP method
    private final String method;
    // API method used to specify one specific data request
    private final String apiMethod;
    // keys parameters
    private final String[] parameters;


    RequestDetail(String url, String method, String apiMethod, String[] parameters) {
        this.url = url;
        this.method = method;
        this.apiMethod = apiMethod;
        this.parameters = parameters;
    }

    /**
     * Method that returns the resource's address (URL).
     * By Renato
     * @return url - resource's address (URL)
     */
    public String getUrl() {
        return url;
    }

    /**
     * Method that returns the HTTP method.
     * By Renato
     * @return method - HTTP method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Method that returns the API method used to specify one specific data request on the API.
     * By Renato
     * @return apiMethod - API method
     */
    public String getApiMethod() {
        return apiMethod;
    }

    /**
     * Method that returns all the key parameters requested for the query.
     * These key parameters need to be associated with values to construct the query string.
     * By Renato
     * @return parameters - All key parameters
     */
    public String[] getParameters() {
        return parameters;
    }

    /**
     * Method that returns the number of key parameters.
     * By Renato
     * @return number of key parameters
     */
    public int getNumberParameters() { return parameters.length; }
}
