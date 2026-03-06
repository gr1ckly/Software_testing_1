package org.example;

class DictionaryPair<K extends Comparable<K>, V> implements Comparable<DictionaryPair<K, V>> {
    K key;
    V value;

    DictionaryPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    K getKey() {
        return key;
    }

    void setKey(K key) {
        this.key = key;
    }

    V getValue() {
        return value;
    }

    void setValue(V value) {
        this.value = value;
    }

    @Override
    public int compareTo(DictionaryPair<K, V> o) {
        return this.key.compareTo(o.key);
    }
}
