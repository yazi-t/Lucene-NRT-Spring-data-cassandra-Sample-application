package support.helper;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

public class PhraseSearchHelper implements SearchHelper {

    @Override
    public Query buildQuery(String fieldName, String text) throws ParseException {
        String[] words = text.split(" ");
        PhraseQuery.Builder builder = new PhraseQuery.Builder();
        for (int i = 0; i < words.length; i++) {
            builder.add(new Term(fieldName, words[i]), 0);
        }
        builder.setSlop(0);
        return builder.build();
    }
}
