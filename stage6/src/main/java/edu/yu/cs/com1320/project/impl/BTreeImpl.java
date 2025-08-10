package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    //Max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private int MAX = 6;
    private Node root; //Root of the B-Tree
    private BTreeImpl.Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int kvp; //number of key-value pairs in the B-tree
    private String SENTINAL = "\u0007";
    private PersistenceManager<Key, Value> pm = null;

    private class Node {
        private int entryCount; // number of entries
        private BTreeImpl.Entry[] entries = new BTreeImpl.Entry[MAX]; // the array of children
        private BTreeImpl.Node next;
        private BTreeImpl.Node previous;

        // create a node with k entries
        private Node(int k)
        {
            this.entryCount = k;
        }

        private void setNext(BTreeImpl.Node next)
        {
            this.next = next;
        }
        private BTreeImpl.Node getNext()
        {
            return this.next;
        }
        private void setPrevious(BTreeImpl.Node previous)
        {
            this.previous = previous;
        }
        private BTreeImpl.Node getPrevious()
        {
            return this.previous;
        }

        private BTreeImpl.Entry[] getEntries()
        {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }

    //internal nodes: only use key and child
    //external nodes: only use key and value
    private class Entry {
        private Comparable key;
        private Value val;
        private BTreeImpl.Node child;

        public Entry(Comparable key, Value val, BTreeImpl.Node child)
        {
            this.key = key;
            this.val = val;
            this.child = child;
        }
        public Object getValue()
        {
            return this.val;
        }
        public Comparable getKey()
        {
            return this.key;
        }
    }

    public BTreeImpl(){
        this.root = new Node(0);
        this.leftMostExternalNode = this.root;
    }

    /**
     * @param k
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    @Override
    public Value get(Key k) {
        if (k == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Entry thisEntry = get(this.root, k, this.height);
        if(thisEntry != null){
            try{
                Value v = this.pm.deserialize(k);
                thisEntry.val = v;
                put(k, v);
                return v;
            }
            catch(Exception e){
                return thisEntry.val;
            }
        }
        return null;
    }

    /**
     * @param k
     * @param v
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    @Override
    public Value put(Key k, Value v) {
        if (k == null)
        {
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //Check if the key already exists in the BTree
        Entry alreadyThere = get(root, k, height);
        if (alreadyThere != null) {
                try {
                    Value oldVal = pm.deserialize(k);
                    alreadyThere.val = v;
                    return oldVal;
                } catch (Exception e) {
                    Value oldValue = alreadyThere.val;
                    alreadyThere.val = v;
                    return oldValue;
                }

        }
        Node newNode = put(root, k, v, height);
        kvp++;
        if(newNode == null){
            return null;
        }
        //private put method only returns non-null if root.entryCount == Btree.MAX
        //(see if-else on previous slide.) Private code will have copied the upper M/2
        //entries over. We now point the new root's first entry at the old root, and
        //set the node returned from private method to be pointed at by the new root's
        //second entry
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        //A split at the root always increases the tree height by 1
        this.height++;
        return null;
    }

    /**
     * @param k
     * @throws IOException
     */
    @Override
    public void moveToDisk(Key k) throws IOException {
        if (get(k) == null) {
            throw new IllegalArgumentException("argument key to moveToDisk() is null");
        }
        if(this.pm == null){
            throw new IllegalStateException("pm is null");
        }
        pm.serialize(k, get(k));
        Entry thisEntry = get(root, k, height);
        thisEntry.val = null;
    }

    /**
     * @param pm
     */
    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.pm = pm;
    }

    private Entry get(Node currentNode, Key key, int height){
        Entry[] entries = currentNode.entries;
        //Current node is leaf/external (i.e height == 0)
        if( height ==0){
            for(int j = 0; j < currentNode.entryCount; j++){
                if(isEqual(key, entries[j].key)){
                    //Found desired key. Return its value
                    return entries[j];
                }
            }
            //Didn't find the key
            return null;
        }
        //Current node is an internal node (height > 0)
        else{
            for(int j = 0; j < currentNode.entryCount; j++){
                //If (we are at the last key in this node OR the
                // key we are looking for is less than the next key, (i.e the desired key must be in the subtree below),
                // and we recurse to the current entry's child/subtree
                if(j + 1 == currentNode.entryCount || less(key, entries[j + 1].key)){
                    return get(entries[j].child, key, height - 1);
                }
            }
        }
        return null;
    }

    private Node put(Node currentNode, Key key, Value val, int height) {
        //Have to set j to the index in currentNode.entries[] where the
        //new entry goes
        int j;
        Entry newEntry = new Entry(key, val, null);

        //If this is an external/leaf node...
        if(height == 0){
            //Set j to the index of the first entry in the
            // current node whose key > the new key
            for(j = 0; j < currentNode.entryCount; j++){
                if(less(key, currentNode.entries[j].key)){
                    break;
                }
            }
        }
        else{
            for (j = 0; j < currentNode.entryCount; j++){
                if(j + 1 == currentNode.entryCount || less(key, currentNode.entries[j + 1].key)){
                    Node newNode = put(currentNode.entries[j++].child, key, val, height - 1);
                    if(newNode == null){
                        return null;
                    }
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        for(int i = currentNode.entryCount; i > j; i--){
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if(currentNode.entryCount < MAX){
            return null;
        }
        else{
            return split(currentNode);
        }
    }

    private Node split(Node currentNode) {
        Node newNode = new Node(MAX / 2);

        //Copy top half of the currentNode into newNode
        for(int j = 0; j < MAX / 2; j++){
            newNode.entries[j] = currentNode.entries[MAX / 2 + j];
            //Set reference in top half of currentNode to null to avoid memory leaks? (idk man)
            currentNode.entries[MAX / 2 + j] = null;
        }
        //Divide currentNode.entryCount by 2
        currentNode.entryCount = MAX / 2;
        return newNode;
    }

    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean isEqual(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) == 0;
    }
}
