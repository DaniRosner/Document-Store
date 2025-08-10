package edu.yu.cs.com1320.project.impl;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
class BTreeImplTest {

    @Test
    void get() {
        BTreeImpl<Integer, Integer> BTree = new BTreeImpl();
        BTree.put(1, 1);
        BTree.put(2, 2);
        BTree.put(3, 3);
        BTree.put(4, 4);
        BTree.put(5, 5);
        BTree.put(6, 6);
        BTree.put(7, 7);
        BTree.put(8, 8);
        BTree.put(9, 9);
        BTree.put(10, 10);
        assertEquals(1, BTree.get(1));
        assertEquals(2, BTree.get(2));
        assertEquals(3, BTree.get(3));
        assertEquals(4, BTree.get(4));
        assertEquals(5, BTree.get(5));
        assertEquals(6, BTree.get(6));
        assertEquals(7, BTree.get(7));
        assertEquals(8, BTree.get(8));
        assertEquals(9, BTree.get(9));
        assertEquals(10, BTree.get(10));

    }

    @Test
    void put() {
        BTreeImpl<Integer, Integer> BTree = new BTreeImpl();
        assertNull(BTree.put(1, 1));
        assertNull(BTree.put(2, 2));
        assertNull(BTree.put(3, 3));
        assertNull(BTree.put(4, 4));
        assertNull(BTree.put(5, 5));
    }

    @Test
    void putAndSplit(){
        BTreeImpl<Integer, Integer> BTree = new BTreeImpl();
        BTree.put(1, 1);
        BTree.put(2, 2);
        BTree.put(3, 3);
        BTree.put(4, 4);
        BTree.put(5, 5);
        BTree.put(6, 6);
    }

    @Test
    void moveToDisk() throws IOException {
    }

    @Test
    void setPersistenceManager() {
    }
}