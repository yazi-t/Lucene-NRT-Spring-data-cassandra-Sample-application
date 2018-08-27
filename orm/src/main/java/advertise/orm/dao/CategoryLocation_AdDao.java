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

import advertise.orm.model.CategoryLocation_Ad;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

/**
 * This interface is a special interface created to behave as a Cassandra
 * repository. Implementation is dynamically generating at the runtime.
 *
 * This repository stores {@link advertise.orm.model.Ad},
 * {@link advertise.orm.model.Location}, {@link advertise.orm.model.AdCategory}
 * combined de-normalized data.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface CategoryLocation_AdDao extends CassandraRepository<CategoryLocation_Ad, UUID> {

    /**
     * @param pageable     {@link org.springframework.data.cassandra.core.query.CassandraPageRequest}
     *                     for pagination purpose.
     * @param adCategoryId {@link advertise.orm.model.Ad#adCategoryId} to filer
     *                     {@link advertise.orm.model.AdCategory#id} is referred.
     * @param locationId   {@link advertise.orm.model.Ad#locationId} to filer
     *                     {@link advertise.orm.model.Location#id} is referred.
     * @return CategoryLocation_Ad with given locationId and adCategoryId
     */
    Slice<CategoryLocation_Ad> findByAdCategoryIdAndLocationId(Pageable pageable, UUID adCategoryId, UUID locationId);
}
