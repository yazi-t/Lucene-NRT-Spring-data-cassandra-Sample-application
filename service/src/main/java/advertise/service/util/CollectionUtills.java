package advertise.service.util;

import advertise.orm.model.Ad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionUtills {

    public static <T> List<T> iterableToList(Iterable<T> itr) {
        if (itr instanceof List) {
            return (List<T>) itr;
        } else {
            List<T> result = new ArrayList<>();
            itr.forEach(result::add);
            return result;
        }
    }

    public static <T> ArrayList<T> arrayListOf(T ... elements) {
        ArrayList<T> result = new ArrayList<>(elements.length);
        Collections.addAll(result, elements);
        return result;
    }
}
