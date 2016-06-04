package dam.projects.projectdam.network;

import dam.projects.projectdam.json.JSONClass;

@SuppressWarnings("unused")

/**
 * Created by Renato on 10/04/2016 : 17:04.
 * Class that represents and gather all information related to HTTP responses.
 * @version 1.0.1
 */
public class HttpNResponse {
    private int responseCode;
    private String responseMessage;
    private String responseContent;
    private JSONClass jsonContent;
    private Exception errorDetails;

    public static final int SUCCESS_CODE = 200;
    // http reply code for server failures
    public static final int SERVER_ERROR = 500;
    public static final int USE_PROXY = 305;
    public static final int AUTH_PROXY = 407;

    public HttpNResponse(int responseCode, String responseMessage, String responseContent, JSONClass jsonContent) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.responseContent = responseContent;
        this.jsonContent = jsonContent;
        errorDetails = null;
    }

    public HttpNResponse(int responseCode, String responseMessage, Exception errorDetails) {
        this.responseCode = responseCode;
        this.errorDetails = errorDetails;
        this.responseMessage = responseMessage;
        responseContent = null;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public JSONClass getJsonContent() {
        return jsonContent;
    }

    public Exception getErrorDetails() {
        return errorDetails;
    }

    public boolean isRequestSucceed() {
        return responseCode == SUCCESS_CODE;
    }

    public boolean isServerProblem() {
        return responseCode >= SERVER_ERROR && responseCode < 600;
    }

    public boolean isProxyProblem() {
        return responseCode == USE_PROXY || responseCode == AUTH_PROXY;
    }
}
