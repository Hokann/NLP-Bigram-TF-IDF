import java.util.Map;

public class MapEntry<K, V> implements Map.Entry<K, V> {
        private K k; // for the key
        private V v; // for the value
        public MapEntry( K key, V value ) {
            this.k = key;
            this.v = value;
        }
        // getters
        public K getKey() { return this.k; }
        public V getValue() { return this.v; }
        // developer's utilities
        protected void setKey( K key ) { this.k = key; }
        public V setValue(V value) {
            V old = v;
            this.v = value;
            return old;
        }
        public String toString() { return "<" + this.getKey() + ":" + this.getValue() + ">"; }
}

