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
package advertise.orm.model;

import com.datastax.driver.core.DataType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Entity class for an 'Location' business entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
@Table("location")
public class Location {

    @PrimaryKey("id")
    private UUID id;

    @Column("name")
    private String name;

    @CassandraType(type = DataType.Name.UDT, userTypeName = "country_type")
    private Country country;

    public Location(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public Location() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
