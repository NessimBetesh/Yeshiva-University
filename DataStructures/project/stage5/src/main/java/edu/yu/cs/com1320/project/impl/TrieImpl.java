package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;

public class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 256;
    private Node<Value> root;

    public static class Node<Value> {
        private List<Value> values = new ArrayList<>();
        private Node<Value>[] links = new Node[alphabetSize];
    }

    public TrieImpl() {
        root = new Node<>();
    }

    /**
     * add the given value at the given key
     *
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        if (val == null || key == null || key.isEmpty()) {
            return;
        } else {
            root = put(root, key, val, 0);
        }
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int d) {
        //crea un node
        if (x == null) {
            x = new Node<>();
        }
        //llega al ultimo node del key
        //le pone el value al key regresa el node
        if (d == key.length()) {
            x.values.add(val);
            return x;
        } else {
            //ir al prox node
            //y crea el key
            char c = key.charAt(d);
            x.links[c] = this.put(x.links[c], key, val, d + 1);
            return x;
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
        Node<Value> node = get(this.root, key, 0);
        if (node == null){
            return new ArrayList<>();
        }
/*
        if (node != null && Objects.equals(node.val, key)) {
            igual.add(node.val);
        }

 */
        Set<Value> igualS = new HashSet<>(node.values);
        if (node != null && !node.values.isEmpty()) {
            igualS.addAll(node.values);
        }
        //collectIgual(node, key, igual);
        List<Value> igual = new ArrayList<>(igualS);
        igual.sort(comparator);
        return igual;
    }

    private Node<Value> get(Node<Value> x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.links[c], key, d + 1);
    }

    private void collectValues(Node<Value> x, Set<Value> values) {
        if (x == null) return;
        //if (x.val != null) {
        values.addAll(x.values);
        //System.out.println("Values collected: " + x.values);
        //}
        for (Node<Value> child : x.links) {
            collectValues(child, values);
        }
    }

    private void collectIgual(Node<Value> x, String key, List<Value> igual) {
        if (x == null || key.isEmpty()) return;
        for (char c : key.toCharArray()) {
            if (x.links[c] != null) {
                x = x.links[c];
            } else {
                return; // No more matches
            }
        }
        if (x.values != null) {
            Set<Value> values = new HashSet<>();
            collectValues(x, values);
            igual.addAll(x.values);
        }
        /*
        if (!x.values.isEmpty()) {
            igual.addAll(x.values);
        }
         */

        /*
        for (char c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                collectIgual(x.links[c], key + (char) c, igual);
            }
        }
         */
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
        Set<Value> igual = new HashSet<>();
        Node<Value> node = get(this.root, key, 0);
        if (node != null && node.values != null) {
            igual.addAll(node.values);
        }
        //collectValues(node, igual);
        return igual;
    }

    private void collectIgualSet(Node<Value> x, String prefix, Set<Value> igual) {
        if (x == null) return;

        //if (x.val != null) {
        igual.addAll(x.values);
        //}
        for (char c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                collectIgualSet(x.links[c], prefix + (char) c, igual);
            }
        }
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
        Node<Value> node = get(this.root, prefix, 0);
        Set<Value> prefixValues = getPrefix(node);
        List<Value> igual = new ArrayList<>(prefixValues); // Convert Set to List
        igual.sort(comparator);
        return igual;
    }

    private Set<Value> getPrefix(Node<Value> x) {
        Set<Value> prefixValues = new HashSet<>();
        collectValues(x, prefixValues);
        return prefixValues;
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
        Set<Value> deletedValue = new HashSet<>();
        root = deleteAllWithPrefix(root, prefix, 0, deletedValue);
        return deletedValue;
    }

    private Node<Value> deleteAllWithPrefix(Node<Value> x, String prefix, int d, Set<Value> deletedValue) {
        if (x == null) return null;
        if (d == prefix.length()) {
            collectValuesPrefix(x, deletedValue);
            x.values.clear();
            return x;
        }
        char c = prefix.charAt(d);
        x.links[c] = deleteAllWithPrefix(x.links[c], prefix, d + 1, deletedValue);
        return x;
    }

    private void collectValuesPrefix(Node<Value> x, Set<Value> deletedValue) {
        if (x == null) return;
        if (x.values != null) {
            deletedValue.addAll(x.values);
        }
        for (Node<Value> child : x.links) {
            collectValuesPrefix(child, deletedValue);
        }
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key) {
        Set<Value> deletedValue = new HashSet<>();
        root = deleteAll(root, key, 0, deletedValue);
        return deletedValue;
    }

    private Node<Value> deleteAll(Node<Value> x, String key, int d, Set<Value> deletedValue) {
        if (x == null) return null;

        if (d == key.length()) {
            deletedValue.addAll(x.values);
            x.values.clear();
            if (x.values.isEmpty() && hijosNull(x.links)) {
                return null;
            }
        } else {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1, deletedValue);
        }
        if (x.values.isEmpty() && hijosNull(x.links)) {
            return null;
        }
        // if (x.values.isEmpty() && hijosNull(x.links)) {
        //   return null; // Return null if the node has no values and no children
        // }
        /*
        boolean hijosNull = true;
        //borra el subtree en x si esta vacio
        for (int c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null && !x.links[c].values.isEmpty()) {
                hijosNull = false;
                //return x; //not empty
                break;
            }
        }
        if (!hijosNull){
            return null;
        }
         */
        return x;
    }

    private boolean hijosNull(Node<Value>[] links) {
        // /*
        for (Node<Value> node : links) {
            if (node != null) return false;
        }
        return true;
        // */
        /*
        for (int i = 0; i < alphabetSize; i++) {
            if (links[i] != null) {
                return false;
            }
        }
        return true;
       */
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     *
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val) {
        return delete(root, key, val, 0);
        /*
        Node<Value> node = get(this.root, key, 0);
        if (node != null && node.values.contains(val)) {
            node.values.remove(val);
            return val;
        }
        return null;
         */
    }
    private Value delete(Node<Value> x, String key, Value val, int d) {
        if (x == null) return null;

        if (d == key.length()) {
            if (x.values.contains(val)) {
                x.values.remove(val);
                return val;
            }
            return null;
        }

        char c = key.charAt(d);
        Value deleted = delete(x.links[c], key, val, d + 1);
/*
        // If the child node becomes null, remove the link from the current node
        if (deleted != null && x.links[c] == null) {
            x.links[c] = null;
        }
 */
        return deleted;
    }
}