package advertise.service;

public enum ErrorCode {

    AD_ENTITY_INVALID_TITLE_NOT_FOUND("Title is expected."),
    AD_ENTITY_INVALID_BODY_NOT_FOUND("Body is expected."),
    AD_ENTITY_INVALID_CATEGORY_NOT_FOUND("Ad category is expected."),
    AD_ENTITY_INVALID_LOCATION_NOT_FOUND("Location is expected."),
    LOCATION_ID_INVALID_NOT_FOUND("Invalid location ID"),
    AD_CATEGORY_ID_INVALID_NOT_FOUND("Invalid ad category ID"),

    AD_CATEGORY_ENTITY_INVAID_NAME_NOT_FOUND("Name is expected."),
    AD_CATEGORY_ENTITY_INVAID_DESCRIPTION_NOT_FOUND("Description is expected."),

    LOCATION_ENTITY_INVAID_NAME_NOT_FOUND("Name is expected."),
    LOCATION_ENTITY_INVAID_COUNTRY_NOT_FOUND("Country is expected.")
    ;

    private final String defaultMessage;
    private int resourceId;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    ErrorCode(String defaultMessage, int resourceId) {
        this.defaultMessage = defaultMessage;
        this.resourceId = resourceId;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public int getResourceId() {
        return resourceId;
    }
}
