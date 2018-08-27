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

import advertise.orm.model.AdCategory;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

/**
 * This interface is a special interface created to behave as a Cassandra
 * repository. Implementation is dynamically generating at the runtime.
 *
 * This repository handles {@link AdCategory} related CRUD methods.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface AdCategoryDao extends CassandraRepository<AdCategory, UUID> {

    /**
     * @param name  {@link AdCategory#name} to filter
     * @return {@link AdCategory} matching name
     */
    @AllowFiltering
    AdCategory findByName(String name);
}
