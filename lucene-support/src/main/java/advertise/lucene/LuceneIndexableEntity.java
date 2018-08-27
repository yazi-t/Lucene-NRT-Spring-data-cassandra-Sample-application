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
package advertise.lucene;

/**
 * Inplement this interface to be used as a index-able entity in {@link LuceneSearchProcessor}.
 *
 * @param <ID_TYPE> type of the identifier
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public interface LuceneIndexableEntity<ID_TYPE> {

    /**
     * Implement to return identifier of the entity
     *
     * @return identifier of the entity
     */
    ID_TYPE getID();

    /**
     * Implement to return index-able text of the entity
     *
     * @return text to index
     */
    String getText();
}
