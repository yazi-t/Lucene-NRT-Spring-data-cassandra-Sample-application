# Lucene-NRT, Spring data cassandra, ReactJs UI Sample application
A sample application with Lucene NRT, Spring data cassandra and React Js UI.

This is a sample advertising web application to demonstrate usage of Apache Lucene near-real-time features, Spring data cassandra, reactJs UI with a Spring application.

### What is Lucene?
 [Apache Lucene](http://lucene.apache.org/) is a free and open-source full text search library written in Java. It is
 [used by many search engine implementations including big names](https://wiki.apache.org/lucene-java/PoweredBy) such as LinkedIn, Twitter, and IBM. It is
 also the backbone of the popular projects such as Apache Solr and Elastic search.
 
 ### Lucene with ACID transactions
 Lucene supports ACID _(Atomicity, Consistency, Isolation, Durability)_ transactions like other powerful data management systems. Lucene requires to commit indexes before indexes are visible to the searches. 
 In this demo transactional commits are shown in the `advertise.lucene.legacy.LegacySearchProcessor` class in lucene-support module.
 
 _Indexing..._
 ```java
 Directory dir = MMapDirectory.open(Paths.get(resourcePath));
 Analyzer analyzer = new StandardAnalyzer();
 IndexWriterConfig conf = new IndexWriterConfig(analyzer);
 conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            
 IndexWriter writer = new IndexWriter(dir, conf);
 writer.addDocument(createDocument(id, content));
 writer.commit();
 ```
 _Reading..._
 ```java
 Directory directory = MMapDirectory.open(getPath());
 IndexReader reader = DirectoryReader.open(directory);
 IndexSearcher indexSearcher = new IndexSearcher(reader);
 
 QueryParser queryParser = new QueryParser(fieldName, new StandardAnalyzer());
 Query query = queryParser.parse(text); // param 'text' should be the text to search
 TopDocs topDocs = indexSearcher.search(query, maxSearchResults);
 List<ID_TYPE> searchResult = new ArrayList<>(topDocs.scoreDocs.length);
 for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
  Document document = indexSearcher.doc(scoreDoc.doc);
  searchResult.add(typeConverter.getValueInType(document.get(FIELD_NAME_ID)));
 return searchResult;
 ```
 
 ### What is lucene NRT?
 In high concurrent environment high intensity read operations can block read operations and that can reduce application throughput significantly. This is where NRT
 comes into play.
 
 Near-real-time denotes a small delay of data processing than real-time computing, but the delay is not significant. 
 
 Apache Lucene supports [near real time](https://en.wikipedia.org/wiki/Real-time_computing#Near_real-time) searching which gives high throughput minimizing
 the locking. Lucene NRT provides low latency, high concurrent throughput while having a ignorable delay / dirty reads as the trade off.
 
 #### DirectoryReader to open indexes
 `DirectoryReader` allows to read uncommitted changes which are in IndexWriter along with the committed indexes. It allows user to  read recently added uncommitted indexes but it introduces scalability issues as a trade off. 
In this demo _DirectoryReader_ can be found in the `advertise.lucene.nrt.NRTDirectoryReaderSearchProcessor` class in lucene-support module.

```java
 DirectoryReader directoryReader = DirectoryReader.open(writer, true);
 IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
 ```
 #### SearchManager to manage IndexSearchers
 The `SearcherManager` is a utility class that facilitates the sharing of IndexSearcher across multiple threads. It provides the facilities to acquire and release IndexSearcher, while allowing IndexSearcher to be reopened periodically. The IndexSearcher attribute can be refreshed by calling `maybeRefresh()`. If one thread calls `maybeRefresh()` only that thread will be waited until the indexes are refreshed. While refreshing following threads can continue searching using the IndexSearcher with previous index version/generation. 
 In this demo usage of _SearchManager_ can be found in the `advertise.lucene.nrt.NRTSearchManagerProcessor` class in lucene-support module.
 
 ```java
 IndexWriter writer = new IndexWriter(cachedFSDirectory, config);
 SearcherManager searcherManager = new SearcherManager(writer, true, new SearcherFactory());
 searcherManager.maybeRefresh();
 IndexSearcher indexSearcher = searcherManager.acquire();
 ```
 
 #### ControlledRealTimeReopenThread to periodically open idexes
 Taking step further a background thread called `ControlledRealTimeReopenThread` can be used to refresh indexes in `SearchManger`. 
 Demonstration usage of _ControlledRealTimeReopenThread_ can be found in the `advertise.lucene.nrt.ControlledRealTimeReopenThread` class in lucene-support module.
 
 ```java
 SearcherManager searcherManager = new SearcherManager(writer, true, new SearcherFactory());
 ControlledRealTimeReopenThread<IndexSearcher> reopenThread = new ControlledRealTimeReopenThread<>(trackingIndexWriter, searcherManager, 5, 0.01f);
 reopenThread.start();
 
 QueryParser queryParser = new QueryParser(fieldName, new StandardAnalyzer());
 Query query = queryParser.parse(text); // param 'text' should be the text to search
 TopDocs topDocs = indexSearcher.search(query, maxSearchResults);
 List<ID_TYPE> searchResult = new ArrayList<>(topDocs.scoreDocs.length);
 for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
  Document document = indexSearcher.doc(scoreDoc.doc);
  searchResult.add(typeConverter.getValueInType(document.get(FIELD_NAME_ID)));
 return searchResult;
 ```
#### Ensure index generation with TrackingIndexWriter
In `TrackingIndexWriter`, when an index changes, a new generation is created and can be used to open the index in that particular point in time. It returns a long version/generation of indexes when an index is created. So if the user requires specific version/generation to be included in the search results can search index with providing the version/generation number.

```java
IndexWriter indexWriter = new IndexWriter(cachedFSDirectory, config);
TrackingIndexWriter trackingIndexWriter = new TrackingIndexWriter(indexWriter);

ControlledRealTimeReopenThread<IndexSearcher> reopenThread = new ControlledRealTimeReopenThread<>(trackingIndexWriter, searcherManager, 5, 0.01f);
reopenThread.start();

long token = trackingIndexWriter.addDocument(createDocument(id, content));
reopenThread.waitForGeneration(token, 1000 /* timeout */);
IndexSearcher indexSearcher = searcherManager.acquire();

QueryParser queryParser = new QueryParser(fieldName, new StandardAnalyzer());
 Query query = queryParser.parse(text); // param 'text' should be the text to search
 TopDocs topDocs = indexSearcher.search(query, maxSearchResults);
 List<ID_TYPE> searchResult = new ArrayList<>(topDocs.scoreDocs.length);
 for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
  Document document = indexSearcher.doc(scoreDoc.doc);
  searchResult.add(typeConverter.getValueInType(document.get(FIELD_NAME_ID)));
 return searchResult;
```

### Spring Data Cassandra and Apache Cassandra Wide Column Store NoSQL Database
[Apache Cassandra](http://cassandra.apache.org/) is a free and open-source distributed wide column store NoSQL database management system designed to handle large amounts of data across many commodity servers, providing high availability with no single point of failure.
