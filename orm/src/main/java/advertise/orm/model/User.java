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


import org.springframework.data.cassandra.core.mapping.UserDefinedType;

/**
 * Entity class for the 'User' business entity.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
@UserDefinedType("user_type")
public class User {

    private String email;

    private String name;

    private String contactNo;

    public User(String email, String name, String contactNo) {
        this.email = email;
        this.name = name;
        this.contactNo = contactNo;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
