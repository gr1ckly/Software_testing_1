package org.example;

import java.util.Arrays;

class LeafNode<K extends Comparable<K>, V> implements Node<K, V> {
    private InternalNode<K, V> parent;
    private int maxNumPairs;
    private int minNumPairs;
    private int numPairs;
    private LeafNode<K, V> leftSibling;
    private LeafNode<K, V> rightSibling;
    private DictionaryPair<K, V>[] dictionary;

    @Override
    public InternalNode<K, V> getParent() {
        return parent;
    }

    @Override
    public void setParent(InternalNode<K, V> parent) {
        this.parent = parent;
    }

    public DictionaryPair<K, V>[] getDictionary() {
        return dictionary;
    }

    public void setDictionary(DictionaryPair<K, V>[] dictionary) {
        this.dictionary = dictionary;
    }

    public int getNumPairs() {
        return numPairs;
    }

    public void setNumPairs(int numPairs) {
        this.numPairs = numPairs;
    }

    public int getMaxNumPairs() {
        return maxNumPairs;
    }

    public int getMinNumPairs() {
        return minNumPairs;
    }

    public LeafNode<K, V> getLeftSibling() {
        return leftSibling;
    }

    public void setLeftSibling(LeafNode<K, V> leftSibling) {
        this.leftSibling = leftSibling;
    }

    public LeafNode<K, V> getRightSibling() {
        return rightSibling;
    }

    public void setRightSibling(LeafNode<K, V> rightSibling) {
        this.rightSibling = rightSibling;
    }

    void delete(int index) {
        this.dictionary[index] = null;
        setNumPairs(getNumPairs() - 1);
        for (int i = index; i < getNumPairs(); i++) {
            dictionary[i] = dictionary[i + 1];
        }
        dictionary[getNumPairs()] = null;
    }

    boolean insert(DictionaryPair dp) {
        for (int i = 0; i < getNumPairs(); i++) {
            if (this.dictionary[i] != null && this.dictionary[i].compareTo(dp) == 0) {
                this.dictionary[i].value = (V) dp.value;
                return true;
            }
        }

        if (this.isFull()) {
            return false;
        }

        this.dictionary[getNumPairs()] = (DictionaryPair<K, V>) dp;
        setNumPairs(getNumPairs() + 1);
        Arrays.sort(this.dictionary, 0, getNumPairs());
        return true;
    }

    boolean isDeficient() { return getNumPairs() < getMinNumPairs(); }

    boolean isFull() { return getNumPairs() == getMaxNumPairs(); }

    boolean isLendable() { return getNumPairs() > getMinNumPairs(); }

    boolean isMergeable() {
        return getNumPairs() == getMinNumPairs();
    }

    LeafNode(int m, DictionaryPair<K, V> dp) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int)(Math.ceil(m / 2.0) - 1);
        this.dictionary = (DictionaryPair<K, V>[]) new DictionaryPair[m];
        this.numPairs = 0;
        this.insert(dp);
    }

    LeafNode(int m, DictionaryPair<K, V>[] dps, InternalNode<K, V> parent) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int)(Math.ceil(m / 2.0) - 1);
        this.dictionary = dps;
        this.numPairs = BPlusTree.linearNullSearch(dps);
        this.parent = parent;
    }
}
