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

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Entity class for the 'ad category, location, ad' de-normalized model entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
@Table("category_location_ad")
public class CategoryLocation_Ad {

    @PrimaryKeyColumn(name = "ad_category_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID adCategoryId;

    @PrimaryKeyColumn(name = "location_id", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private UUID locationId;

    @Column("ad_id")
    private UUID adId;

    public CategoryLocation_Ad(UUID adCategoryId, UUID locationId, UUID adId) {
        this.adCategoryId = adCategoryId;
        this.locationId = locationId;
        this.adId = adId;
    }

    public CategoryLocation_Ad() {
    }

    public UUID getAdCategoryId() {
        return adCategoryId;
    }

    public void setAdCategoryId(UUID adCategoryId) {
        this.adCategoryId = adCategoryId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public UUID getAdId() {
        return adId;
    }

    public void setAdId(UUID adId) {
        this.adId = adId;
    }
}
