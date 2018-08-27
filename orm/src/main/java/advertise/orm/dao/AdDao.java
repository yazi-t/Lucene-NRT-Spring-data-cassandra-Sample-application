/*
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2018 Yasitha THilakaratne
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package advertise.orm.dao;

import advertise.orm.model.Ad;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
/*import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.Repository;*/

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * This interface is a special interface created to behave as a Cassandra
 * repository. Implementation is dynamically generating at the runtime.
 *
 * This repository handles {@link Ad} related CRUD methods.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface AdDao extends CassandraRepository<Ad, UUID> {

    /**
     * Persists given entity.
     *
     * @param entity {@link Ad} to persist
     * @return persisted entity
     */
    <S extends Ad> S save(S entity);

    /**
     * @return count of the {@link Ad}s in the repository.
     */
    long count();

    /**
     * Deletes given entity from the repository.
     * @param entity entity to delete.
     */
    void delete(Ad entity);

    /**
     * @param pageable {@link org.springframework.data.cassandra.core.query.CassandraPageRequest}
     *                 for pagination purpose.
     * @param ids identifier list
     * @return Ads in given identifier list
     */
    Slice<Ad> findAllByIdIn(Pageable pageable, List<UUID> ids);

    /**
     * @param pageable     {@link org.springframework.data.cassandra.core.query.CassandraPageRequest}
     *                     for pagination purpose.
     * @param adCategoryId {@link advertise.orm.model.Ad#adCategoryId} to filer
     *                     {@link advertise.orm.model.AdCategory#id} is referred.
     * @return Ads with given adCategoryId
     */
    Slice<Ad> findByAdCategoryId(Pageable pageable, UUID adCategoryId);

    /**
     * @param pageable   {@link org.springframework.data.cassandra.core.query.CassandraPageRequest}
     *                   for pagination purpose.
     * @param locationId {@link advertise.orm.model.Ad#locationId} to filer
     *                   {@link advertise.orm.model.Location#id} is referred.
     * @return Ads with given locationId
     */
    Slice<Ad> findByLocationId(Pageable pageable, UUID locationId);

    /**
     * @param pageable     {@link org.springframework.data.cassandra.core.query.CassandraPageRequest}
     *                     for pagination purpose.
     * @param adCategoryId {@link advertise.orm.model.Ad#adCategoryId} to filer
     *                     {@link advertise.orm.model.AdCategory#id} is referred.
     * @param locationId   {@link advertise.orm.model.Ad#locationId} to filer
     *                     {@link advertise.orm.model.Location#id} is referred.
     * @return Ads with given locationId and adCategoryId
     */
    @AllowFiltering
    Slice<Ad> findByAdCategoryIdAndLocationId(Pageable pageable, UUID adCategoryId, UUID locationId);

    /**
     * @param title {@link Ad#title} ro check
     * @return if an Ad is containing with the same title
     */
    Optional<UUID> existsByTitle(String title);
}
