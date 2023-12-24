import java.util.*;

public class FileMap implements Map<String, ArrayList<Integer>> {

    private int capacity;
    private int prime = 109345121;
    Random rand = new Random();
    double scale = rand.nextInt( prime - 1 ) + 1;
    int shift = rand.nextInt( prime );

    private double MAX_LOAD = 0.75;
    private int n;
    private MapEntry<String, ArrayList<Integer>>[] table; // fixed array of entries, this will be our internal storage
    private MapEntry<String, ArrayList<Integer>> DEFUNCT = new MapEntry<>( null, null ); // sentinel


    public FileMap(){
        this.capacity = 16;
        this.n = 0;
        this.table = new MapEntry[this.capacity];
    }


    @Override
    public ArrayList<Integer> put(String filename, ArrayList<Integer> position) {
        MapEntry<String, ArrayList<Integer>> entry = new MapEntry<>(filename, position);
        int j = findSlot( hashValue(filename), filename );
        if( j >= 0 ){ // TODO : if filename already exists, add position to the existing list
            ArrayList<Integer> positions = table[j].getValue();
            Integer positionValue = position.get(0);
            positions.add(positionValue);
            return table[j].setValue(positions);
        }
        table[-(j+1)] = entry; // convert to proper index
        this.n++;

        double loadFactor = (double) n / capacity;
        if (loadFactor > MAX_LOAD){
            resize(2 * capacity + 1);
        }
        return null; // new entry added successfully
    }
    private int hashValue( String key ) {
        return (int)( ( Math.abs( key.hashCode() * scale + shift ) % prime ) % capacity );
    }
    public void resize(int newCap){
        ArrayList<Entry<String,ArrayList<Integer>>> buffer = new ArrayList<>();
        for( Entry<String,ArrayList<Integer>> e : this.entrySet2() ){
            buffer.add( e );
        }
        this.capacity = newCap;
        this.table = new MapEntry[this.capacity];
        this.n = 0; // wil be recomputed while reinserting entries
        for( Entry<String,ArrayList<Integer>> e : buffer ){
            put( e.getKey(), e.getValue() );}
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
    public int size() {
        return n;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public ArrayList<Integer> get(Object key) {
        return null;
    }



    @Override
    public ArrayList<Integer> remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends ArrayList<Integer>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<ArrayList<Integer>> values() {
        return null;
    }

    @Override
    public Set<Entry<String, ArrayList<Integer>>> entrySet() {
        return null;
    }

    public Iterable<Entry<String,ArrayList<Integer>>> entrySet2() {
        ArrayList<Entry<String,ArrayList<Integer>>> buffer = new ArrayList<>();
        for( int h = 0; h < this.capacity; h++ )
            if( !isAvailable( h ) ) buffer.add( table[h] );
        return buffer;
    }


}
