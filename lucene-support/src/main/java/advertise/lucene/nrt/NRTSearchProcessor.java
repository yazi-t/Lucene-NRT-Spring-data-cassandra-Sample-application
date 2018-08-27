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

import advertise.lucene.LuceneIndexableEntityService;
import advertise.lucene.LuceneSearchProcessor;
import advertise.lucene.LuceneIndexableEntity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NRTCachingDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import support.helper.SearchType;
import support.types.IdentifierTypeConverter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This {@link LuceneSearchProcessor} implementation uses lucene Near-real-time
 * features for optimal throughput and minimal latency. Can be used in
 * distributed environment also since <code>SearcherManager</code> refresh
 * mechanisms has been used.
 *
 * <p>Although the NRT feature is not exactly (or claimed to be) real-time,
 * the latency between updates and searches can be set close enough so that
 * searches will appear to the users as real-time.
 *
 * <p>This implementation uses <code>NRTCachingDirectory</code>. Cache size
 * has been defined in <code>NRT_CACHING_DIR_SIZE</code> constant.</p>
 *
 *
 * @author Yasitha Thilakaratne
 * @see    advertise.lucene.legacy.LegacySearchProcessor
 * @see    <a href="https://lucene.apache.org/core/4_6_0/core/org/apache/lucene/search/ReferenceManager.html">org.apache.lucene.search.ReferenceManager</a>
 * @see    <a href="https://lucene.apache.org/core/4_6_0/core/org/apache/lucene/search/ControlledRealTimeReopenThread.html">org.apache.lucene.search.ControlledRealTimeReopenThread</a>
 * @since  mca-mtn-1.1.24
 */
public abstract class NRTSearchProcessor<ID_TYPE extends Serializable, E extends LuceneIndexableEntity<ID_TYPE>> extends LuceneSearchProcessor<ID_TYPE, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NRTSearchProcessor.class);

    private static final double NRT_CACHING_DIR_SIZE = 128;

    private static final int PENDING_COMMIT_THRESHOLD = 20;

    private IndexWriter w;

    private NRTCachingDirectory cachedFSDirectory;

    private SearcherManager searcherManager;

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
    public NRTSearchProcessor(LuceneIndexableEntityService<ID_TYPE, E> indexableEntityService, IdentifierTypeConverter<ID_TYPE> typeConverter,
                              SearchType searchType, boolean sortByInsertionOrder, boolean sortInsertionOrderDesc) {
        super(indexableEntityService, typeConverter, searchType, sortByInsertionOrder, sortInsertionOrderDesc);
        this.typeConverter = typeConverter;
    }

    private AtomicInteger threadsInWritingBlock = new AtomicInteger();
    private AtomicInteger pendingCommits = new AtomicInteger();

    /**
     * Adds index with given id and content.
     *
     * @param id      identifier to index.
     * @param content text/content to index.
     */
    @Override
    public void addIndex(ID_TYPE id, String content) {
        try {
            IndexWriter writer = getIndexWriter();

            notifyAboutToIndexUpdate();

            writer.addDocument(createDocument(id, content));

            commitAfterWrittenOrInIntervals();
        } catch (IOException e) {
            LOGGER.error("Unexpected error occurred. ", e);
        }
    }

    /**
     * Deletes index by given identifier.
     *
     * @param id index to be removed
     */
    @Override
    public void deleteIndex(long id) {
        try {
            IndexWriter writer = getIndexWriter();

            Query query = NumericRangeQuery.newLongRange(FIELD_NAME_ID, id, id, true, true);

            notifyAboutToIndexUpdate();

            writer.deleteDocuments(query);

            commitAfterWrittenOrInIntervals();
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred. ", e);
        }
    }

    private void notifyAboutToIndexUpdate() { //TODO
        threadsInWritingBlock.incrementAndGet();
    }

    private void commitAfterWrittenOrInIntervals() throws IOException { //TODO
        if (threadsInWritingBlock.decrementAndGet() == 0 || pendingCommits.incrementAndGet() > PENDING_COMMIT_THRESHOLD) {
            pendingCommits.set(0);
            w.commit();
        }
    }

    /**
     * Returns shared {@link IndexWriter} instance. If not created yet creates the instance.
     *
     * @return the shared {@link IndexWriter} instance
     * @throws Exception
     */
    protected IndexWriter getIndexWriter() throws IOException {
        if (w == null)
            createIndexWriter();
        return w;
    }

    /**
     * Creates shared {@link IndexWriter} from cashed directory if not created yet.
     *
     * @throws IOException
     */
    private synchronized void createIndexWriter() throws IOException {
        initDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setRAMBufferSizeMB(RAM_BUFFER_SIZE);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        w = new IndexWriter(cachedFSDirectory, config);
    }

    /**
     * Initializes cachedFSDirectory. Need to be called before use if near-real-time
     * is being used.
     *
     * @throws IOException
     */
    private void initDirectory() throws IOException {
        if (cachedFSDirectory == null) {
            synchronized (this) {
                if (cachedFSDirectory == null) {
                    Directory directory = FSDirectory.open(getPath());
                    cachedFSDirectory = new NRTCachingDirectory(directory, 5.0, NRT_CACHING_DIR_SIZE);
                }
            }
        }
    }

    /**
     * Returns {@link SearcherManager} instance. If not created yet creates the instance.
     *
     * The SearcherManager is a utility class that facilitates the sharing of IndexSearcher
     * across multiple threads.
     *
     * @return {@link SearcherManager} instance
     * @throws IOException
     */
    protected SearcherManager getSearcherManager() throws IOException {
        if (searcherManager == null) {
            createSearchManager();
        }
        return searcherManager;
    }

    /**
     * Creates {@link SearcherManager} if not created yet.
     *
     * @throws IOException
     */
    private synchronized void createSearchManager() throws IOException {
        if (searcherManager == null) {
            IndexWriter writer = getIndexWriter();
            searcherManager = new SearcherManager(writer, true, new SearcherFactory());
        }
    }

    /**
     * Closes shared {@link IndexWriter} instance.
     */
    @Override
    protected synchronized void closeWriter() {
        if (w != null) {
            try {
                w.close();
                w = null;
            } catch (IOException e) {
                LOGGER.error("Error occurred while closing the writer. ", e);
            }
        }
    }
}
