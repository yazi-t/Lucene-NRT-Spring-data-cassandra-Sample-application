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

import advertise.lucene.util.FileSystemUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import support.helper.SearchHelper;
import support.helper.SearchType;
import support.types.IdentifierTypeConverter;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This class implements full text indexing and searching capability of a entity
 * which has a unique identifier. Class has the basic implementation and does not
 * provide an implementation to acquire a {@link IndexSearcher} instance. So this
 * need to be extended and provide a mechanism to obtain a {@link IndexSearcher}
 * by legacy or near-real-time manner.
 * <p>
 * This class and all implementation initializes class level instances lazily.
 * <p>
 * Class takes {@link IdentifierTypeConverter} and {@link SearchHelper} instances
 * while initializing. typeConverter instance responsible to convert any identifier
 * type to {@link Field} that lucene document store and wise versa. searchHelper
 * defines the type of search going to be performed and builds the query as required.
 *
 *
 * @param <ID_TYPE> Type of the identifier of the entity to index.
 * @param <E>       Entity class that indexed by lucene.
 *                  Needs to implement {@link LuceneIndexableEntity}} interface.
 *                  Should have ID_TYPE identical identifier field to be fetched.
 *
 * @author Yasitha Thilakaratne
 * @since version 1.0.1
 */
public abstract class LuceneSearchProcessor<ID_TYPE extends Serializable, E extends LuceneIndexableEntity<ID_TYPE>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneSearchProcessor.class);

    protected static final String FIELD_NAME_ID = "i"; // i: id
    protected static final String FIELD_NAME_CONTENT = "c"; //c: content
    protected static final String FIELD_NAME_TIMESTAMP = "t"; // t: timestamp

    private static final FieldType LONG_FIELD_TYPE_STORED_SORTED = new FieldType();
    static {
        LONG_FIELD_TYPE_STORED_SORTED.setTokenized(true);
        LONG_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
        LONG_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
        LONG_FIELD_TYPE_STORED_SORTED.setNumericType(FieldType.NumericType.LONG);
        LONG_FIELD_TYPE_STORED_SORTED.setStored(true);
        LONG_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
        LONG_FIELD_TYPE_STORED_SORTED.freeze();
    }

    /**
     * This defines the size of the IndexWriter buffer in MB.
     */
    protected static final double RAM_BUFFER_SIZE = 256;

    /**
     * {@link Path} to indexes.
     */
    private Path path;

    /**
     * This instance is responsible to convert identifier type to relevant
     * {@link Field} type. By using this support instance same class is
     * allowed to be used to index multiple type of identifiers.
     */
    protected IdentifierTypeConverter<ID_TYPE> typeConverter;

    /**
     * This instance is responsible to determine what type of query needs
     * to be performed. eg. {@link WildcardQuery}, {@link PhraseQuery}
     */
    protected SearchHelper searchHelper;

    /**
     * Indexes will maintain sort order and search result will have sort
     * order when this flag is true.
     */
    protected boolean sortByInsertionOrder;

    /**
     * Indexes will be sorted in descending order when this flag is true.
     */
    protected boolean sortInsertionOrderDesc;

    /**
     * Upper bound to limit max number of results when searching
     */
    @Value("${max.search.result.size:100}")
    protected int maxSearchResults;

    /**
     * Path {@link String} to indexes.
     */
    @Value("${keyword.based.search.resource.path:/lucene}")
    protected String resourcePath;

    /**
     * The service that provides mechanism to fetch {@link LuceneIndexableEntity}s.
     */
    protected LuceneIndexableEntityService<ID_TYPE, E> indexableEntityService;

    /**
     * Initiates created instance with indexableEntityService, typeConverter,
     * searchType, sortByInsertionOrder, sortInsertionOrderDesc properties.
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
    public LuceneSearchProcessor(LuceneIndexableEntityService<ID_TYPE, E> indexableEntityService, IdentifierTypeConverter<ID_TYPE> typeConverter, SearchType searchType, boolean sortByInsertionOrder, boolean sortInsertionOrderDesc) {
        this.indexableEntityService = indexableEntityService;
        this.typeConverter = typeConverter;
        this.sortByInsertionOrder = sortByInsertionOrder;
        this.sortInsertionOrderDesc = sortInsertionOrderDesc;

        this.searchHelper = searchType.getSearchHelper();
    }

    /**
     * Searches indexes for given text and returns list of identifiers.
     * Will use the given implementation in concrete class to obtain a {@link IndexSearcher}
     *
     * @param text text to search
     * @return list of ID_TYPE of matching entities to given text.
     */
    public List<ID_TYPE> searchForIds(String text) {
        try {
            IndexSearcher indexSearcher = getIndexSearcher();

            return searchForIds(text, indexSearcher);
        } catch (IndexNotFoundException e) {
            LOGGER.error("Index was not found on given directory [{}]. Recreating indexes...", resourcePath);
            reIndexAsync();
        } catch (IndexFormatTooOldException e) {
            LOGGER.error("Index found on given directory [{}] are too old. Recreating indexes...", resourcePath);
            reIndexAsync();
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurred. ", e);
        }
        LOGGER.error("Returning empty result since previous Error...");
        return Collections.emptyList();
    }

    /**
     * Implement this method to return {@link IndexSearcher}.
     *
     * @return {@link IndexSearcher} from implemented method.
     * @throws IOException when thrown from internal library methods.
     */
    protected abstract IndexSearcher getIndexSearcher() throws IOException;

    protected List<ID_TYPE> searchForIds(String text, IndexSearcher indexSearcher) throws IOException, ParseException {
        Query query = searchHelper.buildQuery(FIELD_NAME_CONTENT, text);
        return getIdsForQuery(query, indexSearcher);
    }

    /**
     * Perform index search for the given {@link Query} using indexSearcher and
     * returns a list of ID_TYPE.
     *
     * @param query the query to perform
     * @param indexSearcher indexSearcher instance
     * @return list of ID_TYPE
     * @throws IOException when thrown from internal method calls.
     */
    private List<ID_TYPE> getIdsForQuery(Query query, IndexSearcher indexSearcher) throws IOException {
        TopDocs topDocs = sortByInsertionOrder ?
                indexSearcher.search(query, maxSearchResults, new Sort(new SortField(FIELD_NAME_TIMESTAMP, SortField.Type.LONG, sortInsertionOrderDesc))) :
                indexSearcher.search(query, maxSearchResults);
        List<ID_TYPE> searchResult = new ArrayList<>(topDocs.scoreDocs.length);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document document = indexSearcher.doc(scoreDoc.doc);
            searchResult.add(typeConverter.getValueInType(document.get(FIELD_NAME_ID)));
        }
        return searchResult;
    }

    /**
     * Perform index search for the given {@link Query} using indexSearcher and
     * returns a list of E (entities).
     * To get entities indexableEntityService will be used.
     *
     * @param text          the query to perform
     * @param indexSearcher indexSearcher instance
     * @return list of E
     * @throws IOException when thrown from internal method calls.
     */
    protected List<E> searchForEntities(String text, IndexSearcher indexSearcher) throws IOException, ParseException {
        Query query = searchHelper.buildQuery(FIELD_NAME_CONTENT, text);
        List<ID_TYPE> ids = getIdsForQuery(query, indexSearcher);

        List<E> searchResult;
        try {
            searchResult = indexableEntityService.getEntitiesByIds(ids);
        } catch (NotImplementedException nie) {
            searchResult = new ArrayList<>(ids.size());
            for (ID_TYPE id : ids) {
                indexableEntityService.getEntityById(id).ifPresent(searchResult::add);
            }
        }

        return searchResult;
    }

    /**
     * Implement this method to add index.
     *
     * @param id      identifier to index.
     * @param content text/content to index.
     *                This field will be used when querying.
     */
    public abstract void addIndex(ID_TYPE id, String content);

    /**
     * Implement this method to remove an index.
     *
     * @param id index to be removed
     */
    public abstract void deleteIndex(long id);

    /**
     * Will delete and recreate all indexes asynchronously.
     */
    public synchronized void reIndexAsync() {
        new Thread(this::reIndex).start(); //  Always one thread will be created and destroyed as soon as finished.
    }

    /**
     * Will delete and recreate all indexes synchronously.
     */
    public synchronized void reIndex() {
        IndexWriter writer = null;

        try {
            closeWriter(); // Close / destroy existing writer
            File indexDirFile = new File(String.valueOf(getPath()));

            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig confInit = new IndexWriterConfig(analyzer);

            if (FileSystemUtils.doesDirectoryExist(indexDirFile)) {
                FileSystemUtils.clean(getPath());
            }

            confInit.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            writer = new IndexWriter(new MMapDirectory(getPath()), confInit);

            List<E> entities = indexableEntityService.getIndexableEntities();
            for (int i = 0; i < entities.size(); i++) {
                E entity = entities.get(i);
                writer.addDocument(createDocument(entity.getID(), entity.getText()));
                debug(analyzer, entity.getText());
                if (i % 5 == 0)
                    writer.commit();
            }

            writer.commit();
        } catch (IOException e) {
            LOGGER.error("Problem occurred when trying to init indexes ", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("Problem occurred when trying to commit and close the writer of init indexes ", e);
                }
            }
        }
    }

    /**
     * Will print tokens when debug enabled.
     */
    private void debug(Analyzer analyzer, String text) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Debugging term [{}]  with [{}]. ", text, analyzer.getClass().getSimpleName());
            TokenStream tokenStream = analyzer.tokenStream(FIELD_NAME_CONTENT, new StringReader(text));
            try {
                CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    LOGGER.debug("Term: [{}]", term.toString());
                }
                tokenStream.end();
            } catch (Exception e) {
                LOGGER.error("Error while debugging analyzer", e);
            } finally {
                try {
                    tokenStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error while analyzer debug closing tokenStream", e);
                }
            }
        }
    }

    /**
     * Creates new document to index using data provided.
     * Uses typeConverter to convert id to a {@link Field}.
     * Adds timestamp field if insertion order based sorting is enabled.
     *
     * @param id identifier of the entity to index.
     * @param content text/content to index.
     * @return a {@link Document} instance
     */
    protected Document createDocument(ID_TYPE id, String content) {
        Document doc = new Document();

        doc.add(typeConverter.getFieldInType(FIELD_NAME_ID, id));
        if (sortByInsertionOrder) doc.add(new LongField(FIELD_NAME_TIMESTAMP , System.currentTimeMillis(), LONG_FIELD_TYPE_STORED_SORTED));
        doc.add(new TextField(FIELD_NAME_CONTENT, content, Field.Store.YES));

        return doc;
    }

    /**
     * Implement this method to return {@link IndexWriter} instance to write indexes.
     *
     * @return {@link IndexWriter} instance
     * @throws Exception
     */
    protected abstract IndexWriter getIndexWriter() throws Exception;

    /**
     * Closes created {@link IndexWriter} instance.
     */
    protected abstract void closeWriter();

    protected Path getPath() {
        if (path == null)
            createPath();
        return path;
    }

    private synchronized void createPath() {
        if (path == null)
            path = Paths.get(resourcePath);
    }

    /**
     * Releases and closes resources before destroy.
     */
    @PreDestroy
    protected void destroy() {
        closeWriter();
    }
}
