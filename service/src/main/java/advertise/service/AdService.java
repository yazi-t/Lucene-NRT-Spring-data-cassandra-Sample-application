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
package advertise.service;

import advertise.orm.model.Ad;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

/**
 * Service layer class to handle {@link Ad} entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface AdService {

    /**
     * Returns a {@link Ad} by identifier
     *
     * @param id identifier of the entity
     * @return entity
     */
    Ad getAdById(UUID id);

    /**
     * Returns paginated ad list.
     *
     * @param pageRequest page request instance. Can create by
     *                     {@link org.springframework.data.cassandra.core.query.CassandraPageRequest#of(int, int)}
     *                    and continue with {@link Pageable#next()}
     * @return slice of {@link Ad}s
     */
    Slice<Ad> getAds(Pageable pageRequest);

    /**
     * Searches entities by given search criteria. (ad category, location, title)
     * Results will be paginated.
     *
     * @param pageRequest  page request instance. Can create by
     *                     {@link org.springframework.data.cassandra.core.query.CassandraPageRequest#of(int, int)}
     * @param adCategoryId adCategoryId to filter. null to ignore
     * @param locationId   locationId to filter. null to ignore
     * @param title        title to search
     * @return slice of {@link Ad}s
     */
    Slice<Ad> getSearchResult(Pageable pageRequest, UUID adCategoryId, UUID locationId, String title);

    /**
     * Creates a {@link Ad}
     *
     * @param ad entity to create
     * @return validation status of the entity
     */
    Result create(Ad ad);

    /**
     * Returns all the {@link Ad} entities.
     *
     * @return list of {@link Ad}s
     */
    List<Ad> getAll();

    /**
     * Will reindex all {@link Ad}s.
     */
    void reIndex();
}
