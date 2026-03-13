package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTest {

    @Test
    @DisplayName("Простая вставка и поиск значений")
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
    @DisplayName("Разделение листа при переполнении")
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
    @DisplayName("Удаление с заимствованием у соседа")
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
    @DisplayName("Удаление со слиянием узлов")
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
    @DisplayName("Поиск и валидация дерева для строковых ключей")
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
    @DisplayName("Поведение пустого дерева")
    void testEmptyTree() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        assertTrue(tree.isValidBPlusTree());
        assertNull(tree.search(1));
    }

    @Test
    @DisplayName("Повторная вставка ключа обновляет значение")
    void testInsertDuplicateKeys() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.insert(1, "B");
        assertTrue(tree.isValidBPlusTree());
        assertEquals("B", tree.search(1));
    }

    @Test
    @DisplayName("Удаление отсутствующего ключа не меняет дерево")
    void testDeleteNonExistentKey() {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "A");
        tree.delete(2);
        assertTrue(tree.isValidBPlusTree());
        assertEquals("A", tree.search(1));
    }

    // вставка null, поиск несуществующего значения
    // добавить трейсы
    @Test
    @DisplayName("Серия сложных операций вставки и удаления")
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

    @Test
    @DisplayName("Создание дерева со степенью больше 7 выбрасывает исключение")
    void testCreateTreeWithDegreeGreaterThanSevenThrows() {
        assertThrows(IllegalArgumentException.class, () -> new BPlusTree<Integer, String>(8));
    }
}
