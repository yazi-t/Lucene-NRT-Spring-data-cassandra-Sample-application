package support.helper;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;

public class WildcardSearchHelper implements SearchHelper {

    @Override
    public Query buildQuery(String fieldName, String text) throws ParseException {
        int start = 0;
        while (start < text.length() && (text.charAt(start) == '*' || text.charAt(start) == '?'))
            start++;
        text = text.substring(start);
        return new WildcardQuery(new Term(fieldName, text + '*'));
    }
}
