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

    private int prime = 109345121;
    Random rand = new Random();
	double scale = rand.nextInt( prime - 1 ) + 1;
	int shift = rand.nextInt( prime );


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
    public FileMap put(String word, FileMap fileMap) {

        MapEntry<String, FileMap> wordEntry = new MapEntry<>(word, fileMap);
        int j = findSlot( hashValue(word), word );
        if( j >= 0 ){ // TODO : if the word already exists in a file, just return the existing fileMap
            return fileMap; //table[j].setValue( file ); // associate word to new file
        }
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
        for( Entry<String,FileMap> e : this.entrySet2() ){
            buffer.add( e );
        }
        this.capacity = newCap;
        this.table = new MapEntry[this.capacity];
        this.n = 0; // wil be recomputed while reinserting entries
        for( Entry<String,FileMap> e : buffer ){
            put( e.getKey(), e.getValue() );}
    }

    private int hashValue( String key ) {
        return (int)( ( Math.abs( key.hashCode() * scale + shift ) % prime ) % capacity );
    }

    @Override
    public FileMap get(Object key) {
        String word = key.toString();
        int j = findSlot( hashValue(word), word );
        if( j < 0 ) return null; // no match found
        return table[j].getValue();
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
        Set<Entry<String, FileMap>> set = new HashSet<>();

        for (MapEntry<String, FileMap> entryList : table) {
            set.addAll((Collection<? extends Entry<String, FileMap>>) entryList);
        }
        return set;
    }

// return an iterable collection of all key-value entries of the map
public Iterable<Entry<String,FileMap>> entrySet2() {
    ArrayList<Entry<String,FileMap>> buffer = new ArrayList<>();
    for( int h = 0; h < this.capacity; h++ )
        if( !isAvailable( h ) ) buffer.add( table[h] );
    return buffer;
}

}
