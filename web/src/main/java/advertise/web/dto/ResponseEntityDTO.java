package advertise.web.dto;

import java.io.Serializable;

/**
 * This class has been designed to be used as the general DTO to send stats based message.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public class ResponseEntityDTO implements Serializable {

    public enum Status {
        SUCCESS,
        ERROR
    }

    private Status type;
    private String message;
    private String [] extra;

    public static ResponseEntityDTO getResponse(Status type, String message, String ... extra) {
        return new ResponseEntityDTO(type, message, extra);
    }

    private ResponseEntityDTO(Status type, String message, String ... extra) {
        this.type = type;
        this.message = message;
        this.extra = extra;
    }

    public Status getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String [] getExtra() {
        return extra;
    }
}
