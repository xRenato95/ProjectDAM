package dam.projects.projectdam.network;
@SuppressWarnings("unused")

/**
 * Created by Renato on 10/04/2016 : 17:24.
 * Class that represents and gathers all information related to HTTP requests.
 * @version 1.0
 */
public class HttpNRequest {
    private RequestDetail connectionDetails;
    // values to be associated with the key parameters
    private String[] parametersDetails;

    public HttpNRequest(RequestDetail connectionDetails, String[] parametersDetails) {
        this.connectionDetails = connectionDetails;
        this.parametersDetails = parametersDetails;
    }

    /**
     * Method that returns all the details about the connection.
     * By Renato
     * @return all the details about the connection
     */
    public RequestDetail getConnectionDetails() {
        return connectionDetails;
    }

    /**
     * Method that return an array of values to be associated with the key parameters to create
     * the final query string.
     * By Renato
     * @return array of values
     */
    public String[] getParametersDetails() {
        return parametersDetails;
    }
}
