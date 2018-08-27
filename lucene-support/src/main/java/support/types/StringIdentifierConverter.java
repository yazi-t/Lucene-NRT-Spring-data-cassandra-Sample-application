/*
 * This software is licensed under the Apache 2 license, quoted below.
 *
 * Copyright 2018 Yasitha Thilakaratne
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
package support.types;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

/**
 * The {@link IdentifierTypeConverter} implementation to be used when {@link String}
 * identifier is used.
 *
 * Converts in to {@link StringField}.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public class StringIdentifierConverter implements IdentifierTypeConverter<String> {

    /**
     * Converts stored {@link String} in the index to {@link String} identifier
     *
     * @param s {@link String} stored value.
     * @return String identifier
     */
    @Override
    public String getValueInType(String s) {
        return s;
    }

    /**
     * Converts given identifier to {@link StringField}
     *
     * @param idFieldName name of the {@link Field}
     * @param id          identifier to convert
     * @return {@link StringField} instance
     */
    @Override
    public Field getFieldInType(String idFieldName, String id) {
        return new StringField(idFieldName, id, Field.Store.YES);
    }
}
