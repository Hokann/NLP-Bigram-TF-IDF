import java.util.*;

public class FileMap implements Map<ArrayList<String>[], ArrayList<Integer>[]> {

    private int capacity;
    private int n;

    public FileMap(){

    }

    @Override
    public int size() {
        return 0;
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
    public ArrayList<Integer>[] get(Object key) {
        return new ArrayList[0];
    }

    @Override
    public ArrayList<Integer>[] put(ArrayList<String>[] key, ArrayList<Integer>[] value) {
        return new ArrayList[0];
    }

    @Override
    public ArrayList<Integer>[] remove(Object key) {
        return new ArrayList[0];
    }

    @Override
    public void putAll(Map<? extends ArrayList<String>[], ? extends ArrayList<Integer>[]> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<ArrayList<String>[]> keySet() {
        return null;
    }

    @Override
    public Collection<ArrayList<Integer>[]> values() {
        return null;
    }

    @Override
    public Set<Entry<ArrayList<String>[], ArrayList<Integer>[]>> entrySet() {
        return null;
    }
}
