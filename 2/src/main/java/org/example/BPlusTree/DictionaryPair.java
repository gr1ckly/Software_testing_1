package org.example.BPlusTree;

class DictionaryPair implements Comparable<DictionaryPair> {
    int key;
    double value;

    DictionaryPair(int key, double value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(DictionaryPair o) {
        if (key == o.key) { return 0; }
        else if (key > o.key) { return 1; }
        else { return -1; }
    }
}
