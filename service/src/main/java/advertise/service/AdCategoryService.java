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

import advertise.orm.model.AdCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer class to handle {@link AdCategory} entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface AdCategoryService {

    /**
     * Creates a {@link AdCategory}
     *
     * @param adCategory entity to create
     * @return validation status of the entity
     */
    Result create(AdCategory adCategory);

    /**
     * Returns a {@link AdCategory} by identifier
     *
     * @param id identifier of the entity
     * @return entity
     */
    AdCategory getById(UUID id);

    /**
     * Returns all the {@link AdCategory} entities.
     *
     * @return list of {@link AdCategory}s
     */
    List<AdCategory> getAll();
}
