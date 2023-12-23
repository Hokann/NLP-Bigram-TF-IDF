import com.google.protobuf.*;
import edu.stanford.nlp.ling.*;

import java.util.*;

//
public class WordMap implements Map<String, FileMap> {
    private MapEntry<String, FileMap>[] table; // fixed array of entries, this will be our internal storage
    private MapEntry<String,FileMap> DEFUNCT = new MapEntry<>( null, null ); // sentinel

    private final double MAX_LOAD = 0.75;
    private Map<String, FileMap> wordMap;
    private int capacity;
    private int n;

    public WordMap(){ // default constructor
       this.capacity = 16;
       this.n = 0;
       this.table = new MapEntry[this.capacity];
    }
    private boolean isAvailable( int j ) { return ( table[j] == null || table[j] == DEFUNCT ); }
    private int findSlot( int h, String k ) {
        int avail = -1; // no slot available (thus far)
        int j = h; // index for scanning the table
        do {
            if( isAvailable( j ) ) { // may be either empty or defunct
                if( avail == -1 ) avail = j; // first available slot
                if( table[j] == null ) break; // if empty, search fails immediately
            } else if( table[j].getKey().equals( k ) )
                return j; // successful search
            j = (j + 1) % this.capacity; // keep looking (cyclically)
        } while( j != h ); // stop if we return to the start
        return -(avail + 1); // search has failed
    }

    @Override
    public FileMap put(String word, FileMap file) {

        MapEntry<String, FileMap> wordEntry = new MapEntry<>(word, file);
        int j = findSlot( word.hashCode(), word );
        if( j >= 0 ) // TODO : if the word already exists in a file, we need to update the reference
            return file; //table[j].setValue( file ); // associate word to new file
        table[-(j+1)] = wordEntry; // convert to proper index
        this.n++;

        double loadFactor = (double) n / capacity;
        if (loadFactor > MAX_LOAD){
            resize(2 * capacity + 1);
        }
        return null; // new entry added successfully
    }

    public void resize(int newCap){
        ArrayList<Entry<String,FileMap>> buffer = new ArrayList<>();
        for( Entry<String,FileMap> e : this.entrySet() )
            buffer.add( e );
        this.capacity = newCap;
        this.table = new MapEntry[this.capacity];
        this.n = 0; // wil be recomputed while reinserting entries
        for( Entry<String,FileMap> e : buffer )
            put( e.getKey(), e.getValue() );
    }

    @Override
    public int size() { return n; }
    @Override
    public boolean isEmpty() { return n == 0; }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public FileMap get(Object key) {
        return null;
    }



    @Override
    public FileMap remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends FileMap> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<FileMap> values() {
        return null;
    }

    @Override
    public Set<Entry<String, FileMap>> entrySet() {
        return null;
    }
}
