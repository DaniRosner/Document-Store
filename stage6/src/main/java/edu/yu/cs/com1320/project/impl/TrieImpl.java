package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {

    private int alphabetSize = 128;
    private Node<Value> root;

    private class Node<Value> {
        String key;
        HashSet<Value> values = new HashSet<>();
        Node[] links = new Node[alphabetSize];
    }


    public TrieImpl() {}
    /**
     * add the given value at the given key
     *
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if(val == null) {
            deleteAll(key); //If value is null, this is a deleteAll
        }
        else{
            root = put(root, key, val, 0);
        }
    }

    /**
     * Get all exact matches for the given key, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A *KEYWORD* SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * Search is CASE SENSITIVE.
     *
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values. Empty List if no matches.
     */
    @Override
    public List<Value> getSorted(String key, Comparator<Value> comparator) {
        List<Value> sortedValues = new ArrayList<>();
        sortedValues.addAll(get(key));
        sortedValues.sort(comparator);
        return sortedValues;
    }

    /**
     * get all exact matches for the given key.
     * Search is CASE SENSITIVE.
     *
     * @param key
     * @return a Set of matching Values. Empty set if no matches.
     */
    @Override
    public Set<Value> get(String key) {
        Node node = get(root, key, 0);
        if(node == null) {
            return Set.of();
        }
        return node.values;
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A *KEYWORD* SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order. Empty List if no matches.
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        List<Value> listOfPrefixValues = new ArrayList<>(getListOfValsContainingPrefix(root, prefix, 0));
        Set<Value> setOfPrefixValues = new HashSet<>(listOfPrefixValues);
        List<Value> sortedValues = new ArrayList<>(setOfPrefixValues);
        sortedValues.sort(comparator);
        return sortedValues;
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        Set<Value> values = new HashSet<>();
        Set<String> matchingWords = new HashSet<>(getSetOfMatchingWords(root, prefix, 0));
        for(String a : matchingWords) {
            values.addAll(get(a));
            deleteAll(a);
        }
        return values;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key) {
        Set<Value> values = new HashSet<>(get(key));
        root = delete(root, key, 0, null);
        return values;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     *
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */

    //Shvach needs work
    @Override
    public Value delete(String key, Value val) {
        if(!get(key).contains(val)) {
            return null;
        }
        root = delete(root, key, 0, val);
        return val;
    }

    private Node put(Node<Value> node, String key, Value val, int d) {
        if(node == null) {
            node = new Node();
        }
        if(d == key.length()) {
            node.values.add(val);
            node.key = key;
            return node;
        }
        char c = key.charAt(d);
        node.links[c] = put(node.links[c], key, val, d + 1);
        return node;
    }

    private Node delete(Node<Value> node, String key, int d, Value val) {
        if(node == null) {
            return null;
        }
        if (d == key.length()) {
            if(val == null) {
                node.values.clear();
            }
            else{
                node.values.remove(val);
            }
        }
        else{
            char c = key.charAt(d);
            node.links[c] = delete(node.links[c], key, d + 1, val);
        }
        if (!node.values.isEmpty()) {
            return node;
        }
        for(int c = 0; c < alphabetSize; c++) {
            if(node.links[c] != null) {
                return node;
            }
        }
        return null;
    }

    private Node get(Node<Value> node, String key, int d) {
        if(node == null) {
            return null;
        }
        if (d == key.length()) {
            return node;
        }
        char c = key.charAt(d);
        return get(node.links[c], key, d + 1);
    }

    private Set<String> getSetOfMatchingWords(Node<Value> node, String prefix, int d) {
        Set<String> prefixMatches = new HashSet<>();

        if(node == null) {
            return prefixMatches;
        }
        if(d == prefix.length()) {
            if(node.key != null) {
                prefixMatches.add(node.key);
            }
            for(int c = 0; c < alphabetSize; c++) {
                if(node.links[c] != null) {
                    traverseTrieAndAddWordsOrValues(node.links[c], prefixMatches, null);
                }
            }
            return prefixMatches;
        }
        char c = prefix.charAt(d);
        return getSetOfMatchingWords(node.links[c], prefix, d + 1);
    }

    private List<Value> getListOfValsContainingPrefix(Node<Value> node, String prefix, int d) {
        List<Value> ValsContainingPrefix = new ArrayList<>();

        if(node == null) {
            return ValsContainingPrefix;
        }
        if(d == prefix.length()) {
            if(!node.values.isEmpty()) {
                ValsContainingPrefix.addAll(node.values);
            }
            for(int c = 0; c < alphabetSize; c++) {
                if(node.links[c] != null) {
                    traverseTrieAndAddWordsOrValues(node.links[c], null, ValsContainingPrefix);
                }
            }
            return ValsContainingPrefix;
        }
        char c = prefix.charAt(d);
        return getListOfValsContainingPrefix(node.links[c], prefix, d + 1);
    }

    private void traverseTrieAndAddWordsOrValues(Node<Value> node, Set<String> prefixMatches, List<Value> ValsContainingPrefix) {
        if(prefixMatches != null) {
            if(node.key != null) {
                prefixMatches.add(node.key);
            }
            for(int c = 0; c < alphabetSize; c++) {
                if(node.links[c] != null) {
                    traverseTrieAndAddWordsOrValues(node.links[c], prefixMatches, null);
                }
            }
        }
        if(ValsContainingPrefix != null) {
            if(!node.values.isEmpty()) {
                ValsContainingPrefix.addAll(node.values);
            }
            for(int c = 0; c < alphabetSize; c++) {
                if(node.links[c] != null) {
                    traverseTrieAndAddWordsOrValues(node.links[c], null, ValsContainingPrefix);
                }
            }
        }
    }
}
