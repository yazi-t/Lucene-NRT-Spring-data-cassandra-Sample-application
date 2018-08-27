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

import advertise.lucene.LuceneIndexableEntity;
import com.datastax.driver.core.DataType;
import org.springframework.data.cassandra.core.mapping.*;


import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Entity class for the 'Ad' business entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
@Table("ad")
public class Ad implements LuceneIndexableEntity<UUID> {

    @PrimaryKey
    private UUID id;

    @Indexed
    @Column("title")
    private String title;

    @Column("body")
    private String body;

    @Column("postedDate")
    private Date postedDate;

    @Column("expireDate")
    private Date expireDate;

    @Indexed
    @Column("adCategory_id")
    private UUID adCategoryId;

    @Column("adCategory")
    private String adCategory;

    @Column("salesArea")
    private SalesArea salesArea;

    @Indexed
    @Column("location_id")
    private UUID locationId;

    @Column("location")
    private String location;

    @CassandraType(type = DataType.Name.UDT, userTypeName = "user_type")
    private User user;

    @Column("price")
    private String price;

    @CassandraType(type = DataType.Name.LIST, typeArguments = DataType.Name.VARCHAR)
    private List<String> imgs;

    public Ad(String title, String body, Date postedDate, Date expireDate, UUID adCategoryId, String adCategory, SalesArea salesArea, UUID locationId, String location, User user, String price, List<String> imgs) {
        this.title = title;
        this.body = body;
        this.postedDate = postedDate;
        this.expireDate = expireDate;
        this.adCategoryId = adCategoryId;
        this.adCategory = adCategory;
        this.salesArea = salesArea;
        this.locationId = locationId;
        this.location = location;
        this.user = user;
        this.price = price;
        this.imgs = imgs;
    }

    public Ad(String title, String body, Date postedDate, Date expireDate, UUID adCategoryId, String adCategory, SalesArea salesArea, UUID locationId, String location, User user, String price) {
        this.title = title;
        this.body = body;
        this.postedDate = postedDate;
        this.expireDate = expireDate;
        this.adCategoryId = adCategoryId;
        this.adCategory = adCategory;
        this.salesArea = salesArea;
        this.locationId = locationId;
        this.location = location;
        this.user = user;
        this.price = price;
    }

    public Ad() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public UUID getAdCategoryId() {
        return adCategoryId;
    }

    public void setAdCategoryId(UUID adCategoryId) {
        this.adCategoryId = adCategoryId;
    }

    public String getAdCategory() {
        return adCategory;
    }

    public void setAdCategory(String adCategory) {
        this.adCategory = adCategory;
    }

    public SalesArea getSalesArea() {
        return salesArea;
    }

    public void setSalesArea(SalesArea salesArea) {
        this.salesArea = salesArea;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public UUID getID() {
        return id;
    }

    public String getText() {
        return this.body;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }
}
