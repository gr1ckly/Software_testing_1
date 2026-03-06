package org.example;

interface Node<K extends Comparable<K>, V> {
    InternalNode<K, V> getParent();
    void setParent(InternalNode<K, V> parent);
}
