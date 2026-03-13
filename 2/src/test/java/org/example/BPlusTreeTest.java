package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTest {

    @Test
    void testSimpleInsert() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(10, "A");
        tree.insert(20, "B");
        tree.insert(5, "C");

        assertTrue(tree.isValidBPlusTree());
        assertEquals("A", tree.search(10));
        assertEquals("B", tree.search(20));
        assertEquals("C", tree.search(5));
        assertNull(tree.search(15));
    }

    @Test
    void testLeafSplit() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.insert(2, "B");
        tree.insert(3, "C");
        tree.insert(4, "D");

        assertTrue(tree.isValidBPlusTree());
        assertEquals("A", tree.search(1));
        assertEquals("B", tree.search(2));
        assertEquals("C", tree.search(3));
        assertEquals("D", tree.search(4));
    }

    @Test
    void testDeleteWithBorrowing() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.insert(2, "B");
        tree.insert(3, "C");
        tree.insert(4, "D");
        tree.insert(5, "E");
        tree.insert(6, "F");

        tree.delete(1);
        assertTrue(tree.isValidBPlusTree());
        assertNull(tree.search(1));
        assertEquals("B", tree.search(2));
    }

    @Test
    void testDeleteWithMerging() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.insert(2, "B");
        tree.insert(3, "C");
        tree.insert(4, "D");
        tree.insert(5, "E");

        tree.delete(1);
        tree.delete(2);
        assertTrue(tree.isValidBPlusTree());
        assertNull(tree.search(1));
        assertNull(tree.search(2));
        assertEquals("C", tree.search(3));
    }

    @Test
    void testSearchAndValidation() {
        BPlusTree<String, String> tree = new BPlusTree<>(4);
        tree.insert("apple", "fruit");
        tree.insert("banana", "fruit");
        tree.insert("carrot", "vegetable");
        tree.insert("zebra", "animal");

        assertTrue(tree.isValidBPlusTree());
        assertEquals("fruit", tree.search("apple"));
        assertEquals("animal", tree.search("zebra"));
        assertNull(tree.search("dog"));
    }

    @Test
    void testEmptyTree() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        assertTrue(tree.isValidBPlusTree());
        assertNull(tree.search(1));
    }

    @Test
    void testInsertDuplicateKeys() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.insert(1, "B");
        assertTrue(tree.isValidBPlusTree());
        assertEquals("B", tree.search(1));
    }

    @Test
    void testDeleteNonExistentKey() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.delete(2);
        assertTrue(tree.isValidBPlusTree());
        assertEquals("A", tree.search(1));
    }

    @Test
    void testComplexOperations() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        for (int i = 1; i <= 10; i++) {
            tree.insert(i, "Value" + i);
        }
        assertTrue(tree.isValidBPlusTree());
        tree.delete(5);
        tree.delete(3);
        tree.delete(8);
        assertTrue(tree.isValidBPlusTree());
        assertNull(tree.search(5));
        assertEquals("Value1", tree.search(1));
        assertEquals("Value10", tree.search(10));
    }
}
