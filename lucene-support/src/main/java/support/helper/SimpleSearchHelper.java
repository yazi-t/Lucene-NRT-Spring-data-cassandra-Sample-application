package support.helper;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;


class SimpleSearchHelper implements SearchHelper {

    @Override
    public Query buildQuery(String fieldName, String text)  throws ParseException {
        QueryParser queryParser = new QueryParser(fieldName, new StandardAnalyzer());
        return queryParser.parse(text);
    }
}
