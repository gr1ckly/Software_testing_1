package org.example.BPlusTree;

import java.util.Arrays;

class LeafNode extends Node {
    int maxNumPairs;
    int minNumPairs;
    int numPairs;
    LeafNode leftSibling;
    LeafNode rightSibling;
    DictionaryPair[] dictionary;

    void delete(int index) {
        this.dictionary[index] = null;
        numPairs--;
    }

    boolean insert(DictionaryPair dp) {
        if (this.isFull()) {
            return false;
        } else {
            this.dictionary[numPairs] = dp;
            numPairs++;
            Arrays.sort(this.dictionary, 0, numPairs);
            return true;
        }
    }

    boolean isDeficient() { return numPairs < minNumPairs; }

    boolean isFull() { return numPairs == maxNumPairs; }

    boolean isLendable() { return numPairs > minNumPairs; }

    boolean isMergeable() {
        return numPairs == minNumPairs;
    }

    LeafNode(int m, DictionaryPair dp) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int)(Math.ceil(m / 2.0) - 1);
        this.dictionary = new DictionaryPair[m];
        this.numPairs = 0;
        this.insert(dp);
    }

    LeafNode(int m, DictionaryPair[] dps, InternalNode parent) {
        this.maxNumPairs = m - 1;
        this.minNumPairs = (int)(Math.ceil(m / 2.0) - 1);
        this.dictionary = dps;
        this.numPairs = BPlusTree.linearNullSearch(dps);
        this.parent = parent;
    }
}
