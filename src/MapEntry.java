import java.util.Map;

/**
 * A physical implementation of java's Entry interface. Allows us to create key-value pairs of any type
 * Code presented in class.
 * @author Francois Major
 * */
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

        // setters
        protected void setKey( K key ) { this.k = key; }
        public V setValue(V value) { //replace current value with new value
            V old = v;
            this.v = value;
            return old;
        }
        public String toString() { return "<" + this.getKey() + ":" + this.getValue() + ">"; }
}

