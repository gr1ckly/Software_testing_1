package org.example;

class InternalNode<K extends Comparable<K>, V> implements Node<K, V> {
    private InternalNode<K, V> parent;
    private int maxDegree;
    private int minDegree;
    private int degree;
    private InternalNode<K, V> leftSibling;
    private InternalNode<K, V> rightSibling;
    private K[] keys;
    private Node<K, V>[] childPointers;

    @Override
    public InternalNode<K, V> getParent() {
        return parent;
    }

    @Override
    public void setParent(InternalNode<K, V> parent) {
        this.parent = parent;
    }

    public K[] getKeys() {
        return keys;
    }

    public void setKeys(K[] keys) {
        this.keys = keys;
    }

    public Node<K, V>[] getChildPointers() {
        return childPointers;
    }

    public void setChildPointers(Node<K, V>[] childPointers) {
        this.childPointers = childPointers;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public int getMinDegree() {
        return minDegree;
    }

    public InternalNode<K, V> getLeftSibling() {
        return leftSibling;
    }

    public void setLeftSibling(InternalNode<K, V> leftSibling) {
        this.leftSibling = leftSibling;
    }

    public InternalNode<K, V> getRightSibling() {
        return rightSibling;
    }

    public void setRightSibling(InternalNode<K, V> rightSibling) {
        this.rightSibling = rightSibling;
    }

    void appendChildPointer(Node<K, V> pointer) {
        this.getChildPointers()[getDegree()] = pointer;
        this.setDegree(getDegree() + 1);
    }

    int findIndexOfPointer(Node<K, V> pointer) {
        for (int i = 0; i < getChildPointers().length; i++) {
            if (getChildPointers()[i] == pointer) { return i; }
        }
        return -1;
    }

    void insertChildPointer(Node<K, V> pointer, int index) {
        for (int i = getDegree() - 1; i >= index ;i--) {
            getChildPointers()[i + 1] = getChildPointers()[i];
        }
        this.getChildPointers()[index] = pointer;
        this.setDegree(getDegree() + 1);
    }

    boolean isDeficient() {
        return this.getDegree() < this.getMinDegree();
    }

    boolean isLendable() { return this.getDegree() > this.getMinDegree(); }

    boolean isMergeable() { return this.getDegree() == this.getMinDegree(); }

    boolean isOverfull() {
        return this.getDegree() == getMaxDegree() + 1;
    }

    void prependChildPointer(Node<K, V> pointer) {
        for (int i = getDegree() - 1; i >= 0 ;i--) {
            getChildPointers()[i + 1] = getChildPointers()[i];
        }
        this.getChildPointers()[0] = pointer;
        this.setDegree(getDegree() + 1);
    }

    void removeKey(int index) {
        for (int i = index; i < getKeys().length - 1; i++) {
            getKeys()[i] = getKeys()[i + 1];
        }
        getKeys()[getKeys().length - 1] = null;
    }

    void removePointer(int index) {
        for (int i = index; i < getChildPointers().length - 1; i++) {
            getChildPointers()[i] = getChildPointers()[i + 1];
        }
        getChildPointers()[getChildPointers().length - 1] = null;
        this.setDegree(getDegree() - 1);
    }

    void removePointer(Node<K, V> pointer) {
        for (int i = 0; i < getChildPointers().length; i++) {
            if (getChildPointers()[i] == pointer) {
                removePointer(i);
                return;
            }
        }
    }

    InternalNode(int m, K[] keys) {
        this.maxDegree = m;
        this.minDegree = (int)Math.ceil(m/2.0);
        this.degree = 0;
        this.keys = keys;
        this.childPointers = new Node[this.maxDegree + 1];
    }

    InternalNode(int m, K[] keys, Node<K, V>[] pointers) {
        this.maxDegree = m;
        this.minDegree = (int)Math.ceil(m/2.0);
        this.degree = BPlusTree.linearNullSearch(pointers);
        this.keys = keys;
        this.childPointers = pointers;
    }
}
