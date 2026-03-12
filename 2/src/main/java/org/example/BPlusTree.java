package org.example;
import java.util.*;

public class BPlusTree<K extends Comparable<K>, V> {
    private static final int MIN_DEGREE = 3;
    private static final int MAX_DEGREE = 7;
    int m;
    public InternalNode<K, V> root;
    public LeafNode<K, V> firstLeaf;

    public BPlusTree(int m) {
        if (m < MIN_DEGREE || m > MAX_DEGREE) {
            throw new IllegalArgumentException(
                    "Tree degree must be between " + MIN_DEGREE + " and " + MAX_DEGREE
            );
        }
        this.m = m;
        this.root = null;
    }

    private int binarySearch(DictionaryPair<K, V>[] dps, int numPairs, K t) {
        Comparator<DictionaryPair<K, V>> c = new Comparator<DictionaryPair<K, V>>() {
            @Override
            public int compare(DictionaryPair<K, V> o1, DictionaryPair<K, V> o2) {
                return o1.key.compareTo(o2.key);
            }
        };
        return Arrays.binarySearch(dps, 0, numPairs, new DictionaryPair<K, V>(t, null), c);
    }

    private LeafNode<K, V> findLeafNode(K key) {
        Node<K, V> current = this.root;

        while (current instanceof InternalNode) {
            InternalNode<K, V> node = (InternalNode<K, V>) current;
            K[] keys = node.getKeys();
            Node<K, V>[] children = node.getChildPointers();
            int i;

            for (i = 0; i < node.getDegree() - 1; i++) {
                if (keys[i] == null) break;
                if (key.compareTo(keys[i]) < 0) { break; }
            }

            while (i < children.length && children[i] == null) {
                i++;
            }

            if (i >= children.length) {
                throw new IllegalStateException("Could not find valid child pointer in internal node");
            }

            current = children[i];
        }

        if (current instanceof LeafNode) {
            return (LeafNode<K, V>) current;
        }

        throw new IllegalStateException("Expected leaf node when traversing B+ tree");
    }

    private int findIndexOfPointer(Node<K, V>[] pointers, LeafNode<K, V> node) {
        int i;
        for (i = 0; i < pointers.length; i++) {
            if (pointers[i] == node) { break; }
        }
        return i;
    }

    private int getMidpoint() {
        return (int)Math.ceil((this.m + 1) / 2.0) - 1;
    }

    private void handleDeficiency(InternalNode<K, V> in) {
        InternalNode<K, V> sibling;
        InternalNode<K, V> parent = in.getParent();

        if (this.root == in) {
            for (int i = 0; i < in.getChildPointers().length; i++) {
                if (in.getChildPointers()[i] != null) {
                    if (in.getChildPointers()[i] instanceof InternalNode) {
                        this.root = (InternalNode<K, V>)in.getChildPointers()[i];
                        this.root.setParent(null);
                    } else if (in.getChildPointers()[i] instanceof LeafNode) {
                        this.root = null;
                    }
                }
            }
        }

        else if (in.getLeftSibling() != null &&
                in.getLeftSibling().getParent() == in.getParent() &&
                in.getLeftSibling().isLendable()) {
            sibling = in.getLeftSibling();
            int parentPointerIndex = parent.findIndexOfPointer(in);
            K borrowedKey = sibling.getKeys()[sibling.getDegree() - 2];
            Node<K, V> pointer = sibling.getChildPointers()[sibling.getDegree() - 1];
            int inDegree = in.getDegree();

            for (int i = inDegree; i > 0; i--) {
                in.getChildPointers()[i] = in.getChildPointers()[i - 1];
            }
            in.getChildPointers()[0] = pointer;
            pointer.setParent(in);

            for (int i = inDegree - 1; i > 0; i--) {
                in.getKeys()[i] = in.getKeys()[i - 1];
            }
            in.getKeys()[0] = parent.getKeys()[parentPointerIndex - 1];

            parent.getKeys()[parentPointerIndex - 1] = borrowedKey;

            sibling.removeKey(sibling.getDegree() - 2);
            sibling.removePointer(sibling.getDegree() - 1);
            
            in.setDegree(inDegree + 1);
        } else if (in.getRightSibling() != null &&
                in.getRightSibling().getParent() == in.getParent() &&
                in.getRightSibling().isLendable()) {
            sibling = in.getRightSibling();

            K borrowedKey = sibling.getKeys()[0];
            Node<K, V> pointer = sibling.getChildPointers()[0];

            in.getKeys()[in.getDegree() - 1] = parent.getKeys()[parent.findIndexOfPointer(in)];
            in.getChildPointers()[in.getDegree()] = pointer;
            pointer.setParent(in);

            parent.getKeys()[parent.findIndexOfPointer(in)] = borrowedKey;

            shiftDown(sibling.getChildPointers(), 1);
            for (int i = 0; i < sibling.getKeys().length - 1; i++) {
                sibling.getKeys()[i] = sibling.getKeys()[i + 1];
            }
            sibling.getKeys()[sibling.getKeys().length - 1] = null;
            sortKeys(sibling.getKeys());
            
            in.setDegree(in.getDegree() + 1);
            sibling.setDegree(sibling.getDegree() - 1);
        }

        else if (in.getLeftSibling() != null &&
                in.getLeftSibling().getParent() == in.getParent() &&
                in.getLeftSibling().isMergeable()) {
            sibling = in.getLeftSibling();
            int pointerIndex = parent.findIndexOfPointer(in);
            if (sibling.getDegree() > 0) {
                sibling.getKeys()[sibling.getDegree() - 1] = parent.getKeys()[pointerIndex - 1];
            }

            int siblingKeyPos = sibling.getDegree();
            for (int i = 0; i < in.getDegree() - 1; i++) {
                if (in.getKeys()[i] != null) {
                    sibling.getKeys()[siblingKeyPos] = in.getKeys()[i];
                    siblingKeyPos++;
                }
            }

            for (int i = 0; i < in.getDegree(); i++) {
                if (in.getChildPointers()[i] != null) {
                    sibling.appendChildPointer(in.getChildPointers()[i]);
                    in.getChildPointers()[i].setParent(sibling);
                }
            }

            parent.removeKey(pointerIndex - 1);
            parent.removePointer(pointerIndex);
            sortKeys(parent.getKeys());

            sibling.setRightSibling(in.getRightSibling());
        } else if (in.getRightSibling() != null &&
                in.getRightSibling().getParent() == in.getParent() &&
                in.getRightSibling().isMergeable()) {
            sibling = in.getRightSibling();
            int pointerIndex = parent.findIndexOfPointer(in);
            if (in.getDegree() > 0) {
                in.getKeys()[in.getDegree() - 1] = parent.getKeys()[pointerIndex];
            }

            int inKeyPos = in.getDegree();
            for (int i = 0; i < sibling.getDegree() - 1; i++) {
                if (sibling.getKeys()[i] != null) {
                    in.getKeys()[inKeyPos] = sibling.getKeys()[i];
                    inKeyPos++;
                }
            }

            for (int i = 0; i < sibling.getDegree(); i++) {
                if (sibling.getChildPointers()[i] != null) {
                    in.appendChildPointer(sibling.getChildPointers()[i]);
                    sibling.getChildPointers()[i].setParent(in);
                }
            }

            parent.removeKey(pointerIndex);
            parent.removePointer(pointerIndex + 1);
            sortKeys(parent.getKeys());

            in.setRightSibling(sibling.getRightSibling());
        }

        if (parent != null && parent.isDeficient()) {
            handleDeficiency(parent);
        }
    }

    private boolean isEmpty() {
        return firstLeaf == null;
    }

    static <K extends Comparable<K>, V> int linearNullSearch(DictionaryPair<K, V>[] dps) {
        for (int i = 0; i <  dps.length; i++) {
            if (dps[i] == null) { return i; }
        }
        return -1;
    }

    static <K extends Comparable<K>, V> int linearNullSearch(Node<K, V>[] pointers) {
        for (int i = 0; i <  pointers.length; i++) {
            if (pointers[i] == null) { return i; }
        }
        return -1;
    }

    private void shiftDown(Node<K, V>[] pointers, int amount) {
        for (int i = 0; i < pointers.length - amount; i++) {
            pointers[i] = pointers[i + amount];
        }
        for (int i = pointers.length - amount; i < pointers.length; i++) {
            pointers[i] = null;
        }
    }

    private void sortDictionary(DictionaryPair<K, V>[] dictionary) {
        Arrays.sort(dictionary, new Comparator<DictionaryPair<K, V>>() {
            @Override
            public int compare(DictionaryPair<K, V> o1, DictionaryPair<K, V> o2) {
                if (o1 == null && o2 == null) { return 0; }
                if (o1 == null) { return 1; }
                if (o2 == null) { return -1; }
                return o1.compareTo(o2);
            }
        });
    }

    private void sortKeys(K[] keys) {
        Arrays.sort(keys, new Comparator<K>() {
            @Override
            public int compare(K o1, K o2) {
                if (o1 == null && o2 == null) { return 0; }
                if (o1 == null) { return 1; }
                if (o2 == null) { return -1; }
                return o1.compareTo(o2);
            }
        });
    }

    private Node<K, V>[] splitChildPointers(InternalNode<K, V> in, int split) {
        Node<K, V>[] pointers = in.getChildPointers();
        Node<K, V>[] halfPointers = new Node[this.m + 1];

        for (int i = split + 1; i < pointers.length; i++) {
            halfPointers[i - split - 1] = pointers[i];
            pointers[i] = null;
        }
        in.setDegree(split + 1);

        return halfPointers;
    }

    private DictionaryPair<K, V>[] splitDictionary(LeafNode<K, V> ln, int split) {
        DictionaryPair<K, V>[] dictionary = ln.getDictionary();

        DictionaryPair<K, V>[] halfDict = new DictionaryPair[this.m];

        for (int i = split; i < dictionary.length; i++) {
            halfDict[i - split] = dictionary[i];
            dictionary[i] = null;
        }

        ln.setNumPairs(split);
        return halfDict;
    }

    private void splitInternalNode(InternalNode<K, V> in) {
        InternalNode<K, V> parent = in.getParent();

        int midpoint = getMidpoint();
        K newParentKey = in.getKeys()[midpoint];
        K[] halfKeys = splitKeys(in.getKeys(), midpoint);
        Node<K, V>[] halfPointers = splitChildPointers(in, midpoint);

        in.setDegree(linearNullSearch(in.getChildPointers()));

        InternalNode<K, V> sibling = new InternalNode<K, V>(this.m, halfKeys, halfPointers);
        for (Node<K, V> pointer : halfPointers) {
            if (pointer != null) { pointer.setParent(sibling); }
        }

        sibling.setRightSibling(in.getRightSibling());
        if (sibling.getRightSibling() != null) {
            sibling.getRightSibling().setLeftSibling(sibling);
        }
        in.setRightSibling(sibling);
        sibling.setLeftSibling(in);

        if (parent == null) {
            K[] keys = (K[]) new Comparable[this.m];
            keys[0] = newParentKey;
            InternalNode<K, V> newRoot = new InternalNode<K, V>(this.m, keys);
            newRoot.appendChildPointer(in);
            newRoot.appendChildPointer(sibling);
            this.root = newRoot;

            in.setParent(newRoot);
            sibling.setParent(newRoot);
        } else {
            parent.getKeys()[parent.getDegree() - 1] = newParentKey;
            Arrays.sort(parent.getKeys(), 0, parent.getDegree());

            int pointerIndex = parent.findIndexOfPointer(in) + 1;
            parent.insertChildPointer(sibling, pointerIndex);
            sibling.setParent(parent);
        }
    }

    private K[] splitKeys(K[] keys, int split) {
        K[] halfKeys = (K[]) new Comparable[this.m];

        keys[split] = null;

        for (int i = split + 1; i < keys.length; i++) {
            halfKeys[i - split - 1] = keys[i];
            keys[i] = null;
        }

        return halfKeys;
    }

    public void delete(K key) {
        if (!isEmpty()) {
            LeafNode<K, V> ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);
            int dpIndex = binarySearch(ln.getDictionary(), ln.getNumPairs(), key);

            if (dpIndex >= 0) {
                ln.delete(dpIndex);
                if (ln.isDeficient()) {
                    LeafNode<K, V> sibling;
                    InternalNode<K, V> parent = ln.getParent();

                    if (ln.getLeftSibling() != null &&
                            ln.getLeftSibling().getParent() == ln.getParent() &&
                            ln.getLeftSibling().isLendable()) {
                        sibling = ln.getLeftSibling();
                        DictionaryPair<K, V> borrowedDP = sibling.getDictionary()[sibling.getNumPairs() - 1];

                        ln.insert(borrowedDP);
                        sortDictionary(ln.getDictionary());
                        sibling.delete(sibling.getNumPairs() - 1);
                        sortDictionary(sibling.getDictionary());

                        int pointerIndex = findIndexOfPointer(parent.getChildPointers(), ln);
                        parent.getKeys()[pointerIndex - 1] = ln.getDictionary()[0].key;
                    } else if (ln.getRightSibling() != null &&
                            ln.getRightSibling().getParent() == ln.getParent() &&
                            ln.getRightSibling().isLendable()) {
                        sibling = ln.getRightSibling();
                        DictionaryPair<K, V> borrowedDP = sibling.getDictionary()[0];

                        ln.insert(borrowedDP);
                        sibling.delete(0);
                        sortDictionary(sibling.getDictionary());

                        int pointerIndex = findIndexOfPointer(parent.getChildPointers(), ln);
                        parent.getKeys()[pointerIndex] = sibling.getDictionary()[0].key;
                    } else if (ln.getLeftSibling() != null &&
                            ln.getLeftSibling().getParent() == ln.getParent() &&
                            ln.getLeftSibling().isMergeable()) {
                        sibling = ln.getLeftSibling();
                        int pointerIndex = findIndexOfPointer(parent.getChildPointers(), ln);
                        for (int i = 0; i < ln.getNumPairs(); i++) {
                            if (ln.getDictionary()[i] != null) {
                                sibling.getDictionary()[sibling.getNumPairs()] = ln.getDictionary()[i];
                                sibling.setNumPairs(sibling.getNumPairs() + 1);
                            }
                        }
                        sortDictionary(sibling.getDictionary());

                        parent.removeKey(pointerIndex - 1);
                        parent.removePointer(pointerIndex);
                        sortKeys(parent.getKeys());

                        sibling.setRightSibling(ln.getRightSibling());
                        if (ln.getRightSibling() != null) {
                            ln.getRightSibling().setLeftSibling(sibling);
                        }

                        if (parent.isDeficient()) {
                            handleDeficiency(parent);
                        }
                    } else if (ln.getRightSibling() != null &&
                            ln.getRightSibling().getParent() == ln.getParent() &&
                            ln.getRightSibling().isMergeable()) {
                        sibling = ln.getRightSibling();
                        int pointerIndex = findIndexOfPointer(parent.getChildPointers(), ln);
                        DictionaryPair<K, V>[] tempDict = new DictionaryPair[this.m];
                        int tempPos = 0;
                        for (int i = 0; i < ln.getNumPairs(); i++) {
                            if (ln.getDictionary()[i] != null) {
                                tempDict[tempPos++] = ln.getDictionary()[i];
                            }
                        }
                        for (int i = 0; i < sibling.getNumPairs(); i++) {
                            if (sibling.getDictionary()[i] != null) {
                                tempDict[tempPos++] = sibling.getDictionary()[i];
                            }
                        }
                        for (int i = 0; i < this.m; i++) {
                            sibling.getDictionary()[i] = tempDict[i];
                        }
                        sibling.setNumPairs(tempPos);
                        sortDictionary(sibling.getDictionary());
                        parent.removeKey(pointerIndex);
                        parent.removePointer(pointerIndex);
                        sortKeys(parent.getKeys());

                        sibling.setLeftSibling(ln.getLeftSibling());
                        if (sibling.getLeftSibling() == null) {
                            firstLeaf = sibling;
                        } else {
                            sibling.getLeftSibling().setRightSibling(sibling);
                        }

                        if (parent.isDeficient()) {
                            handleDeficiency(parent);
                        }
                    } else if (this.root == null && this.firstLeaf.getNumPairs() == 0) {
                        this.firstLeaf = null;
                    } else {
                        sortDictionary(ln.getDictionary());
                    }
                }
            }
        }
    }

    public void insert(K key, V value){
        if (isEmpty()) {
            LeafNode<K, V> ln = new LeafNode<K, V>(this.m, new DictionaryPair<K, V>(key, value));

            this.firstLeaf = ln;
        } else {
            LeafNode<K, V> ln = (this.root == null) ? this.firstLeaf :
                    findLeafNode(key);

            if (!ln.insert(new DictionaryPair<K, V>(key, value))) {
                ln.getDictionary()[ln.getNumPairs()] = new DictionaryPair<K, V>(key, value);
                ln.setNumPairs(ln.getNumPairs() + 1);
                sortDictionary(ln.getDictionary());

                int midpoint = getMidpoint();
                DictionaryPair<K, V>[] halfDict = splitDictionary(ln, midpoint);

                if (ln.getParent() == null) {
                    K[] parent_keys = (K[]) new Comparable[this.m];
                    parent_keys[0] = halfDict[0].key;
                    InternalNode<K, V> parent = new InternalNode<K, V>(this.m, parent_keys);
                    ln.setParent(parent);
                    parent.appendChildPointer(ln);
                } else {
                    K newParentKey = halfDict[0].key;
                    ln.getParent().getKeys()[ln.getParent().getDegree() - 1] = newParentKey;
                    Arrays.sort(ln.getParent().getKeys(), 0, ln.getParent().getDegree());
                }

                LeafNode<K, V> newLeafNode = new LeafNode<K, V>(this.m, halfDict, ln.getParent());

                int pointerIndex = ln.getParent().findIndexOfPointer(ln) + 1;
                ln.getParent().insertChildPointer(newLeafNode, pointerIndex);

                newLeafNode.setRightSibling(ln.getRightSibling());
                if (newLeafNode.getRightSibling() != null) {
                    newLeafNode.getRightSibling().setLeftSibling(newLeafNode);
                }
                ln.setRightSibling(newLeafNode);
                newLeafNode.setLeftSibling(ln);

                if (this.root == null) {
                    this.root = ln.getParent();
                } else {
                    InternalNode<K, V> in = ln.getParent();
                    while (in != null) {
                        if (in.isOverfull()) {
                            splitInternalNode(in);
                        } else {
                            break;
                        }
                        in = in.getParent();
                    }
                }
            }
        }
    }

    public V search(K key) {
        if (isEmpty()) { return null; }

        LeafNode<K, V> ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);

        DictionaryPair<K, V>[] dps = ln.getDictionary();
        int index = binarySearch(dps, ln.getNumPairs(), key);

        if (index < 0) {
            return null;
        } else {
            return dps[index].value;
        }
    }

    public boolean isValidBPlusTree() {
        if (isEmpty()) {
            return true;
        }

        try {
            if (!validateNodeStructure()) {
                return false;
            }
            if (!validateKeysOrder()) {
                return false;
            }
            if (!validateLeafLevel()) {
                return false;
            }
            if (!validateLeafLinks()) {
                return false;
            }
            if (!validateInternalNodeKeys()) {
                return false;
            }
            if (!validateKeyUniqueness()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validateNodeStructure() {
        if (root == null) {
            if (firstLeaf != null) {
                int numPairs = firstLeaf.getNumPairs();
                int maxNumPairs = firstLeaf.getMaxNumPairs();
                int minNumPairs = firstLeaf.getMinNumPairs();
                if (numPairs > maxNumPairs || (numPairs > 0 && numPairs < minNumPairs)) {
                    return false;
                }
            }
            return true;
        }
        return validateInternalNodeStructure(root);
    }

    private boolean validateInternalNodeStructure(InternalNode<K, V> node) {
        if (node == null) return true;

        int degree = node.getDegree();
        int maxDegree = node.getMaxDegree();
        int minDegree = node.getMinDegree();

        if (degree > maxDegree || degree < minDegree) {
            return false;
        }

        int keyCount = 0;
        K[] keys = node.getKeys();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) keyCount++;
        }

        if (keyCount != degree - 1) {
            return false;
        }

        Node<K, V>[] children = node.getChildPointers();
        for (int i = 0; i < degree; i++) {
            if (children[i] instanceof InternalNode) {
                if (!validateInternalNodeStructure((InternalNode<K, V>) children[i])) {
                    return false;
                }
            } else if (children[i] instanceof LeafNode) {
                LeafNode<K, V> leaf = (LeafNode<K, V>) children[i];
                int numPairs = leaf.getNumPairs();
                int maxNumPairs = leaf.getMaxNumPairs();
                int minNumPairs = leaf.getMinNumPairs();
                if (numPairs > maxNumPairs || (numPairs > 0 && numPairs < minNumPairs)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateKeysOrder() {
        if (root == null) {
            return validateLeafKeysOrder(firstLeaf);
        }
        return validateNodeKeysOrder(root);
    }

    private boolean validateNodeKeysOrder(Node<K, V> node) {
        if (node instanceof InternalNode) {
            InternalNode<K, V> internalNode = (InternalNode<K, V>) node;

            K[] keys = internalNode.getKeys();
            for (int i = 1; i < keys.length; i++) {
                if (keys[i] != null && keys[i-1] != null && keys[i-1].compareTo(keys[i]) > 0) {
                    return false;
                }
            }

            Node<K, V>[] children = internalNode.getChildPointers();
            for (int i = 0; i < internalNode.getDegree(); i++) {
                if (!validateNodeKeysOrder(children[i])) {
                    return false;
                }
            }
        } else if (node instanceof LeafNode) {
            return validateLeafKeysOrder((LeafNode<K, V>) node);
        }

        return true;
    }

    private boolean validateLeafKeysOrder(LeafNode<K, V> leaf) {
        if (leaf == null) return true;

        DictionaryPair<K, V>[] pairs = leaf.getDictionary();
        for (int i = 1; i < pairs.length; i++) {
            if (pairs[i] != null && pairs[i-1] != null && pairs[i-1].key.compareTo(pairs[i].key) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean validateLeafLevel() {
        if (root == null) {
            return true;
        }

        List<Integer> leafLevels = new ArrayList<>();
        collectLeafLevels(root, 0, leafLevels);

        if (leafLevels.isEmpty()) return true;

        int expectedLevel = leafLevels.get(0);
        for (int level : leafLevels) {
            if (level != expectedLevel) {
                return false;
            }
        }

        return true;
    }

    private void collectLeafLevels(Node<K, V> node, int currentLevel, List<Integer> levels) {
        if (node instanceof LeafNode) {
            levels.add(currentLevel);
            return;
        }

        if (node instanceof InternalNode) {
            InternalNode<K, V> internalNode = (InternalNode<K, V>) node;
            Node<K, V>[] children = internalNode.getChildPointers();
            for (int i = 0; i < internalNode.getDegree(); i++) {
                collectLeafLevels(children[i], currentLevel + 1, levels);
            }
        }
    }

    private boolean validateLeafLinks() {
        if (firstLeaf == null) return true;

        if (firstLeaf.getLeftSibling() != null) {
            return false;
        }

        LeafNode<K, V> current = firstLeaf;
        while (current.getRightSibling() != null) {
            LeafNode<K, V> next = current.getRightSibling();

            if (next.getLeftSibling() != current) {
                return false;
            }

            DictionaryPair<K, V>[] currentPairs = current.getDictionary();
            DictionaryPair<K, V>[] nextPairs = next.getDictionary();

            K lastKeyInCurrent = null;
            for (DictionaryPair<K, V> pair : currentPairs) {
                if (pair != null) {
                    lastKeyInCurrent = pair.key;
                }
            }

            K firstKeyInNext = null;
            for (DictionaryPair<K, V> pair : nextPairs) {
                if (pair != null) {
                    firstKeyInNext = pair.key;
                    break;
                }
            }

            if (lastKeyInCurrent != null && firstKeyInNext != null && lastKeyInCurrent.compareTo(firstKeyInNext) >= 0) {
                return false;
            }

            current = next;
        }

        return true;
    }

    private boolean validateInternalNodeKeys() {
        if (root == null) return true;

        return validateInternalNodeKeyRanges(root, null, null);
    }

    private boolean validateInternalNodeKeyRanges(InternalNode<K, V> node, K minKey, K maxKey) {
        if (node == null) return true;

        if (!validateNodeKeyRange(node, minKey, maxKey)) {
            return false;
        }

        Node<K, V>[] children = node.getChildPointers();
        K[] keys = node.getKeys();
        for (int i = 1; i < node.getDegree() - 1; i++) {
            if (keys[i] != null && keys[i-1] != null && keys[i-1].compareTo(keys[i]) >= 0) {
                return false;
            }
        }

        for (int i = 0; i < node.getDegree(); i++) {
            Node<K, V> child = children[i];
            if (child == null) {
                return false;
            }

            K childMin = (i == 0) ? minKey : keys[i - 1];
            K childMax = (i == node.getDegree() - 1) ? maxKey : keys[i];

            if (child instanceof InternalNode) {
                if (!validateInternalNodeKeyRanges((InternalNode<K, V>) child, childMin, childMax)) {
                    return false;
                }
            } else if (child instanceof LeafNode) {
                if (!validateNodeKeyRange(child, childMin, childMax)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateNodeKeyRange(Node<K, V> node, K minKey, K maxKey) {
        if (node instanceof InternalNode) {
            InternalNode<K, V> internalNode = (InternalNode<K, V>) node;
            K[] keys = internalNode.getKeys();

            for (K key : keys) {
                if (key != null && ((minKey != null && key.compareTo(minKey) < 0) || (maxKey != null && key.compareTo(maxKey) >= 0))) {
                    return false;
                }
            }
        } else if (node instanceof LeafNode) {
            LeafNode<K, V> leafNode = (LeafNode<K, V>) node;
            DictionaryPair<K, V>[] pairs = leafNode.getDictionary();

            for (DictionaryPair<K, V> pair : pairs) {
                if (pair != null && ((minKey != null && pair.key.compareTo(minKey) < 0) || (maxKey != null && pair.key.compareTo(maxKey) >= 0))) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean validateKeyUniqueness() {
        Set<K> allKeys = new HashSet<>();

        if (root == null) {
            if (firstLeaf != null) {
                DictionaryPair<K, V>[] pairs = firstLeaf.getDictionary();
                for (DictionaryPair<K, V> pair : pairs) {
                    if (pair != null) {
                        if (!allKeys.add(pair.key)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            if (!collectAllKeys(root, allKeys)) {
                return false;
            }
        }

        return true;
    }

    private boolean collectAllKeys(Node<K, V> node, Set<K> keys) {
        if (node instanceof InternalNode) {
            InternalNode<K, V> internalNode = (InternalNode<K, V>) node;

            Node<K, V>[] children = internalNode.getChildPointers();
            for (int i = 0; i < internalNode.getDegree(); i++) {
                if (!collectAllKeys(children[i], keys)) {
                    return false;
                }
            }
        } else if (node instanceof LeafNode) {
            LeafNode<K, V> leafNode = (LeafNode<K, V>) node;
            DictionaryPair<K, V>[] pairs = leafNode.getDictionary();

            for (DictionaryPair<K, V> pair : pairs) {
                if (pair != null && !keys.add(pair.key)) {
                    return false;
                }
            }
        }

        return true;
    }
}
