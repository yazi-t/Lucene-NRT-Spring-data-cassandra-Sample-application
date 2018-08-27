package advertise.lucene;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implement this interface to behave as the {@link LuceneIndexableEntity} service.
 *
 * Should be allows to access all entities, entity by identifier from the entity store.
 *
 * @param <ID_TYPE> type of the identifier of the T
 * @param <T>       type of the {@link LuceneIndexableEntity}
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface LuceneIndexableEntityService<ID_TYPE extends Serializable, T extends LuceneIndexableEntity<ID_TYPE>> {

    /**
     * Implement to return all entities need to be indexed.
     *
     * @return a list of {@link LuceneIndexableEntity}
     */
    List<T> getIndexableEntities();

    /**
     * Implement to return entities in given identifier list.
     *
     * @param ids list of identifiers
     * @return list of entities belong to identifier
     * @throws NotImplementedException if cannot be implemented.
     */
    List<T> getEntitiesByIds(List<ID_TYPE> ids) throws NotImplementedException;

    /**
     * Implement to return entity by identifier
     *
     * @param id identifier of the required entity
     * @return the entity
     */
    Optional<T> getEntityById(ID_TYPE id);
}
