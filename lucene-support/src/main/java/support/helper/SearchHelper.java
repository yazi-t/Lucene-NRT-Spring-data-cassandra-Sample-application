package support.helper;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.ParseException;

public interface SearchHelper {

    Query buildQuery(String fieldName, String text) throws ParseException;

}
