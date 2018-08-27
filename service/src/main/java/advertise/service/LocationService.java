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

import advertise.orm.model.Location;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer class to handle {@link Location} entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface LocationService {

    /**
     * Creates a {@link Location}
     *
     * @param location entity to create
     * @return validation status of the entity
     */
    Result create(Location location);

    /**
     * Returns a {@link Location} by identifier
     *
     * @param id identifier of the entity
     * @return entity
     */
    Location getById(UUID id);

    /**
     * Returns all the {@link Location} entities.
     *
     * @return list of {@link Location}s
     */
    List<Location> getAll();
}
