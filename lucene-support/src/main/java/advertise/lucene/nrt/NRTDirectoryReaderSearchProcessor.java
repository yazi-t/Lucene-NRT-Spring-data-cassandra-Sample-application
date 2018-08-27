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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.helper.SearchType;
import support.types.IdentifierTypeConverter;

import java.io.IOException;
import java.io.Serializable;

/**
 * This {@link LuceneSearchProcessor} implementation uses {@link DirectoryReader}
 * to access indexes in near-real-time. The {@link DirectoryReader} opens indexes
 * with {@link IndexWriter} and allows to read uncommitted indexes in the writer.
 *
 * Anyway an {@link IndexSearcher} is created each time a search is performed and
 * it has a performance cost.
 *
 * @param <ID_TYPE> Type of the identifier of the entity to index.
 * @param <E>       Entity class that indexed by lucene.
 *                  Needs to implement {@link LuceneIndexableEntity}} interface.
 *                  Should have ID_TYPE identical identifier field to be fetched.
 *
 * @author Yasitha Thilakaratne
 * @see LuceneSearchProcessor
 * @see NRTSearchProcessor
 * @see <a href="https://lucene.apache.org/core/4_6_1/core/org/apache/lucene/index/DirectoryReader.html">org.apache.lucene.index.DirectoryReader</a>
 * @since version 1.0.1
 */
public class NRTDirectoryReaderSearchProcessor<ID_TYPE extends Serializable, E extends LuceneIndexableEntity<ID_TYPE>> extends NRTSearchProcessor<ID_TYPE, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NRTDirectoryReaderSearchProcessor.class);

    private DirectoryReader directoryReader;

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
    public NRTDirectoryReaderSearchProcessor(LuceneIndexableEntityService<ID_TYPE, E> indexableEntityService, IdentifierTypeConverter<ID_TYPE> typeConverter,
                                             SearchType searchType, boolean sortByInsertionOrder, boolean sortInsertionOrderDesc) {
        super(indexableEntityService, typeConverter, searchType, sortByInsertionOrder, sortInsertionOrderDesc);
        this.typeConverter = typeConverter;
    }

    /**
     * Creates a new {@link IndexSearcher} instance with {@link DirectoryReader}
     * and returns.
     *
     * @return a new {@link IndexSearcher} instance.
     * @throws IOException
     */
    protected IndexSearcher getIndexSearcher() throws IOException {
        return new IndexSearcher(getDirectoryReader());
    }

    /**
     * Returns {@link DirectoryReader} instance. Creates if not created yet.
     *
     * @return the {@link DirectoryReader} instance
     * @throws IOException
     */
    private DirectoryReader getDirectoryReader() throws IOException {
        IndexWriter writer = getIndexWriter();
        if (directoryReader == null) {
            createDirectoryReader(writer);
        }
        return directoryReader;
    }

    /**
     * Creates {@link DirectoryReader} instance} if not created yet.
     *
     * @param writer {@link IndexWriter} to wrap by reader to read.
     * @throws IOException
     */
    private synchronized void createDirectoryReader(IndexWriter writer) throws IOException {
        if (directoryReader == null)
            directoryReader = DirectoryReader.open(writer, true);
    }
}
