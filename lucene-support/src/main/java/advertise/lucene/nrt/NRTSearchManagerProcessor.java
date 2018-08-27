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
package advertise.lucene.nrt;

import advertise.lucene.LuceneIndexableEntity;
import advertise.lucene.LuceneIndexableEntityService;
import advertise.lucene.LuceneSearchProcessor;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.helper.SearchType;
import support.types.IdentifierTypeConverter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * This {@link advertise.lucene.LuceneSearchProcessor} implementation uses
 * {@link SearcherManager} to share {@link IndexSearcher} across multiple threads
 * to perform searches simultaneously. To refresh the shared {@link IndexSearcher}
 * can call {@link SearcherManager#maybeRefresh()} method before acquire. Only
 * {@link SearcherManager#maybeRefresh()} calling thread will get blocked and other
 * threads can perform searches with existing code until {@link SearcherManager}
 * is refreshed.
 *
 *
 * @param <ID_TYPE> Type of the identifier of the entity to index.
 * @param <E>       Entity class that indexed by lucene.
 *                  Needs to implement {@link LuceneIndexableEntity}} interface.
 *                  Should have ID_TYPE identical identifier field to be fetched.
 *
 * @author Yasitha Thilakaratne
 * @see advertise.lucene.LuceneSearchProcessor
 * @see NRTSearchProcessor
 * @see <a href="https://lucene.apache.org/core/4_6_0/core/index.html?org/apache/lucene/search/SearcherManager.html">org.apache.lucene.search.SearcherManager</a>
 * @since version 1.0.1
 */
public class NRTSearchManagerProcessor<ID_TYPE extends Serializable, E extends LuceneIndexableEntity<ID_TYPE>> extends NRTSearchProcessor<ID_TYPE, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NRTSearchManagerProcessor.class);

    /**
     * Initiates created instance with indexableEntityService, typeConverter,
     * searchType, sortByInsertionOrder, sortInsertionOrderDesc properties
     * in super level.
     * Initializing these are compulsory for {@link LuceneSearchProcessor} so
     * this is the only constructor the class has.
     *
     * @param indexableEntityService a service instance to fetch {@link LuceneIndexableEntity}
     *                               when required.
     * @param typeConverter          a {@link IdentifierTypeConverter} instance
     *                               to convert identifier type to relevant
     *                               {@link Field} type.
     * @param searchType             type of the search to perform. Related query
     *                               to given option will be performed.
     * @param sortByInsertionOrder   true to maintain insertion order while querying.
     *                               Note: this flag need to be set while creating
     *                               indexes if required to fetch in order.
     * @param sortInsertionOrderDesc true to sort insertion order descending. Last
     *                               inserted entity will be fetched first.
     */
    public NRTSearchManagerProcessor(LuceneIndexableEntityService<ID_TYPE, E> indexableEntityService, IdentifierTypeConverter<ID_TYPE> typeConverter,
                                     SearchType searchType, boolean sortByInsertionOrder, boolean sortInsertionOrderDesc) {
        super(indexableEntityService, typeConverter, searchType, sortByInsertionOrder, sortInsertionOrderDesc);
        this.typeConverter = typeConverter;
    }

    /**
     * Searches indexes for given text and returns list of identifiers.
     * Will obtain {@link IndexSearcher} by {@link SearcherManager} and once done
     * {@link IndexSearcher} will be returned by calling {@link SearcherManager#release(Object)}
     *
     * @param text text to search
     * @return list of ID_TYPE of matching entities to given text.
     */
    @Override
    public List<ID_TYPE> searchForIds(String text) {
        try {
            IndexSearcher indexSearcher = getIndexSearcher();
            try {
                return super.searchForIds(text, indexSearcher);
            } finally {
                getSearcherManager().release(indexSearcher);
            }
        } catch (IndexNotFoundException e) {
            LOGGER.info("Index was not found on given directory [{}]. Recreating indexes...", resourcePath);
            reIndexAsync();
            return Collections.emptyList();
        } catch (IndexFormatTooOldException e) {
            LOGGER.info("Index found on given directory [{}] are too old. Recreating indexes...", resourcePath);
            reIndexAsync();
            return Collections.emptyList();
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred. ", e);
            return Collections.emptyList();
        }
    }

    /**
     * Will return {@link IndexSearcher} instance acquired by {@link SearcherManager}.
     * Note: {@link IndexSearcher} must be released to avoid leaks.
     *
     * @return {@link IndexSearcher} instance
     * @throws IOException
     */
    protected IndexSearcher getIndexSearcher() throws IOException {
        SearcherManager searcherManager = getSearcherManager();
        searcherManager.maybeRefresh();
        return searcherManager.acquire();
    }
}
