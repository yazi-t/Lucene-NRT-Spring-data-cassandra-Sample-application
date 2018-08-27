package advertise.service.util;

public interface DefaultInsertable<UNIQUE_FIELD_TYPE, ENTITY> {

    boolean isExist(UNIQUE_FIELD_TYPE fieldValue);

    void insert(ENTITY e);
}
