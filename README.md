# Lucene-NRT, Spring data cassandra, ReactJs UI Sample application
A sample application with Lucene NRT, Spring data cassandra and React Js UI.

This is a sample advertising web application to demonstrate usage of Apache Lucene near-real-time features, Spring data cassandra, reactJs UI with a Spring application.

### What is Lucene?
 [Apache Lucene](http://lucene.apache.org/) is a free and open-source full text search library written in Java. It is
 [used by many search engine implementations including big names](https://wiki.apache.org/lucene-java/PoweredBy) such as LinkedIn, Twitter, and IBM. It is
 also the backbone of the popular projects such as Apache Solr and Elastic search.
 
 ### What is lucene NRT?
 
 In high concurrent environment high intensity read operations can block read operations and that can reduce application throughput significantly. This is where NRT
 comes into play.
 
 Near-real-time denotes a small delay of data processing than real-time computing, but the delay is not significant. 
 
 Apache Lucene supports [near real time](https://en.wikipedia.org/wiki/Real-time_computing#Near_real-time) searching which gives high throughput minimizing
 the locking. Lucene NRT provides low latency, high concurrent throughput while having a ignorable delay / dirty reads as the trade off.
 
 #### DirectoryReader to open indexes
 
 Directory reader allows to read uncommitted changes which are in IndexWriter along with the committed indexes. It allows user to  read recently added uncommitted indexes but it introduces scalability issues as a trade off. 
