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

/**
 * The {@link IdentifierTypeConverter} denotes a entity that can be used by
 * {@link advertise.lucene.LuceneSearchProcessor} to convert identifier type
 * to a relevant {@link Field} type to store.
 *
 * <p>eg. {@link Long} or long identifier can be stored on a
 * {@link org.apache.lucene.document.LongField}
 *
 * @param <T> type of the identifier
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface IdentifierTypeConverter<T> {

    /**
     * Implement to convert stored {@link String} in the index to identifier
     *
     * @param s {@link String} stored value.
     * @return converted identifier
     */
    T getValueInType(String s);

    /**
     * Implement to convert given identifier to {@link Field} attribute.
     * @param idFieldName name of the {@link Field}
     * @param id          identifier to convert
     * @return Field
     */
    Field getFieldInType(String idFieldName, T id);
}
