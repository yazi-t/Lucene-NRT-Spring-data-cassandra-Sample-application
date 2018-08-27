package advertise.web.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * This class has been designed to be used as a DTO to transfer any paged entity list
 * which the total count of entities are unknown. Can find whether a next page or previous
 * page available by boolean flags.
 *
 * Note: Designed to handle Apache Cassandra pages results.
 *
 * @param <T> the type of the entities list is holding
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public class CountLessPagedResponseDTO<T> implements Serializable {

    private List<T> content;
    private int currentPageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    private String error;

    public CountLessPagedResponseDTO(List<T> content, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.currentPageSize = content.size();
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> CountLessPagedResponseDTO<T> buildDTO(List<T> content, boolean hasNext, boolean hasPrevious) {
        return new CountLessPagedResponseDTO<>(content, hasNext, hasPrevious);
    }

    public static <T> CountLessPagedResponseDTO<T> buildErrorDTO(String error) {
        CountLessPagedResponseDTO<T> result = new CountLessPagedResponseDTO<>(Collections.emptyList(), false, false);
        result.error = error;
        return result;
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPageSize() {
        return currentPageSize;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }
}
