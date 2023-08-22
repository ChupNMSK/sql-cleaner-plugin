package plugin.parser;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SQLQueryIterator implements Iterable<String> {
    private String[] queryArray;

    public SQLQueryIterator(String queries) {
        queryArray = queries.split(";");
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < queryArray.length;
            }

            @Override
            public String next() {
                if(!hasNext()) {
                    throw new NoSuchElementException();
                }
                String query = queryArray[currentIndex].trim();
                currentIndex++;
                return query;
            }
        };
    }

    public static Stream<String> streamOf(String sql) {
        return StreamSupport.stream(new SQLQueryIterator(sql).spliterator(), false);
    }
}

