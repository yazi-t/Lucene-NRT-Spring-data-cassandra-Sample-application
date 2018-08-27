package support.helper;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;

public class FuzzySearchHelper implements SearchHelper {

    @Override
    public Query buildQuery(String fieldName, String text) throws ParseException {
        return new FuzzyQuery(new Term(fieldName, text));
    }
}
