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
package advertise.lucene.legacy;

import advertise.lucene.LuceneIndexableEntityService;
import advertise.lucene.LuceneSearchProcessor;
import advertise.lucene.LuceneIndexableEntity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.helper.SearchType;
import support.types.IdentifierTypeConverter;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This {@link LuceneSearchProcessor} implementation will perform search in legacy
 * transactional manner. This guarantees the existence of indexes and make sure to
 * include latest indexed result also in result.
 * Even though the results are consistent this implementation has a performance
 * penalty.
 *
 * @param <ID_TYPE> Type of the identifier of the entity to index.
 * @param <E>       Entity class that indexed by lucene.
 *                  Needs to implement {@link LuceneIndexableEntity}} interface.
 *                  Should have ID_TYPE identical identifier field to be fetched.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public class LegacySearchProcessor<ID_TYPE extends Serializable, E extends LuceneIndexableEntity<ID_TYPE>> extends LuceneSearchProcessor<ID_TYPE, E> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LegacySearchProcessor.class);

    /**
     * The {@link IndexWriter} instance. This instance is thread safe.
     */
    protected IndexWriter w;

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
    public LegacySearchProcessor(LuceneIndexableEntityService<ID_TYPE, E> indexableEntityService, IdentifierTypeConverter<ID_TYPE> typeConverter,
                                 SearchType searchType, boolean sortByInsertionOrder, boolean sortInsertionOrderDesc) {
        super(indexableEntityService, typeConverter, searchType, sortByInsertionOrder, sortInsertionOrderDesc);
        this.typeConverter = typeConverter;
    }

    /**
     * Returns ids of all indexed entities. Suitable for debugging purpose.
     *
     * @return list of identifiers
     */
    public List<ID_TYPE> getAllIndexed() {
        try {
            IndexReader reader = getIndexReader();

            List<ID_TYPE> searchResult = new ArrayList<>(reader.maxDoc());
            for (int i = 0; i < reader.maxDoc(); i++) {
                Document document = reader.document(i);
                searchResult.add(typeConverter.getValueInType(document.get(FIELD_NAME_ID)));
            }
            return searchResult;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Creates and returns {@link IndexReader} instance.
     *
     * @return a new {@link IndexReader}
     * @throws IOException
     */
    private IndexReader getIndexReader() throws IOException {
        Directory directory = MMapDirectory.open(getPath());
        return DirectoryReader.open(directory);
    }

    /**
     * Creates and returns new {@link IndexSearcher} instance.
     *
     * @return a new {@link IndexSearcher}
     * @throws IOException
     */
    protected IndexSearcher getIndexSearcher() throws IOException {
        return new IndexSearcher(getIndexReader());
    }

    /**
     * Adds index with given id and content. Makes sure to commit before return.
     *
     * @param id      identifier to index.
     * @param content text/content to index.
     */
    public void addIndex(ID_TYPE id, String content) {
        try {
            IndexWriter writer = getIndexWriter();
            writer.addDocument(createDocument(id, content));
            writer.commit();
        } catch (Exception e) {
            LOGGER.error("Exception while indexing classified ad with id [{}]: \n", id, e);
        }
    }

    /**
     * Deletes index by given identifier.
     *
     * @param id index to be removed
     */
    public synchronized void deleteIndex(long id) {
        try {
            IndexWriter writer = getIndexWriter();
            Query query = NumericRangeQuery.newLongRange(FIELD_NAME_ID, id, id, true, true);
            writer.deleteDocuments(query);
            writer.commit();
        } catch (Exception e) {
            LOGGER.error("Problem occurred when deleting document from MapDirectory : \n", e);
        }
    }

    /**
     * Returns shared {@link IndexWriter} instance. If not created yet creates the instance.
     *
     * @return the shared {@link IndexWriter} instance
     * @throws Exception
     */
    protected IndexWriter getIndexWriter() throws Exception {
        if (w == null)
            createIndexWriter();
        return w;
    }

    /**
     * Creates shared {@link IndexWriter} if not created yet.
     *
     * @throws Exception
     */
    private synchronized void createIndexWriter() throws Exception {
        if (w == null) {
            Directory dir = MMapDirectory.open(getPath());

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig conf = new IndexWriterConfig(analyzer);
            conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            w = new IndexWriter(dir, conf);
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