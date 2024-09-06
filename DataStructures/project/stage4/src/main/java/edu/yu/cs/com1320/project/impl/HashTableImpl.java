package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
    private final int SIZE = 5;

    class Entry<Key, Value> {
        Key key;
        Value value;
        Entry<Key, Value> next;

        Entry(Key k, Value v) {
            if (k == null) {
                throw new IllegalArgumentException();
            }
            key = k;
            value = v;
            next = null;
        }
    }

    private Entry<Key, Value>[] table;

    public HashTableImpl() {
        this.table = (Entry<Key, Value>[]) new Entry[SIZE];
        for (int i = 0; i < SIZE; i++) {
            table[i] = null;
        }
    }

    private int hashFunction(Key key) {
        return (key.hashCode() & 0x7fffffff) % SIZE;
    }

    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    @Override
    public Value get(Key k) {
        int index = hashFunction(k);
        Entry<Key, Value> entry = table[index];
        while (entry != null) {
            if (entry.key.equals(k)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * @param k the key at which to store the value
     * @param v the value to store
     *          To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    @Override
    public Value put(Key k, Value v) {
        int index = hashFunction(k);
        Entry<Key, Value>[] array = this.table;
        Entry<Key, Value> entry = array[index];
        Entry<Key, Value> prevEntry = null;

        while (entry != null) {
            if (entry.key.equals(k)) {
                Value oldValue = entry.value;
                if (v == null) {
                    if (prevEntry != null) {
                        prevEntry.next = entry.next; // Update the previous entry.next
                    } else {
                        array[index] = entry.next;
                    }
                    return oldValue;
                } else {
                    // Update value
                    entry.value = v;
                    return oldValue;
                }
            }
            prevEntry = entry;
            entry = entry.next;
        }
        // si no hay entry con el mismo key crea un nuevo entry
        if (v != null) {
            // Check if resizing needed
            if (size() >= table.length * 0.75) {
                resizeArray();
                index = hashFunction(k); // Recalculate index desp de resize
                array = this.table; // Update array desp de resize
            }
            Entry<Key, Value> newEntry = new Entry<>(k, v);
            newEntry.next = array[index];
            array[index] = newEntry;
        }
        return null;
    }
    private void resizeArray() {
        int newSize = table.length * 2;
        Entry<Key, Value>[] newArray = new Entry[newSize];
        // Rehash todos los entries
        for (Entry<Key, Value> entry : table) {
            while (entry != null) {
                Entry<Key, Value> next = entry.next;
                int newIndex = hashFunction(entry.key);
                entry.next = newArray[newIndex];
                newArray[newIndex] = entry;
                entry = next;
            }
        }
        table = newArray;
    }

    /**
     * @param key the key whose presence in the hashtable we are inquiring about
     * @return true if the given key is present in the hashtable as a key, false if not
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean containsKey(Key key) {
        if (key == null) {
            throw new NullPointerException();
        }
        int index = this.hashFunction(key);
        Entry<Key, Value> current = this.table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * @return an unmodifiable set of all the keys in this HashTable
     * @see Collections#unmodifiableSet(Set)
     */
    @Override
    public Set<Key> keySet() {
        Set<Key> keys = new HashSet<>();
        for (Entry<Key, Value> entry : table) {
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        return Collections.unmodifiableSet(keys);
    }

    /**
     * @return an unmodifiable collection of all the values in this HashTable
     * @see Collections#unmodifiableCollection(Collection)
     */
    @Override
    public Collection<Value> values() {
        Collection<Value> values = new HashSet<>();
        for (Entry<Key, Value> entry : table) {
            while (entry != null) {
                values.add(entry.value);
                entry = entry.next;
            }
        }
        return Collections.unmodifiableCollection(values);
    }

    /**
     * @return how entries there currently are in the HashTable
     */
    @Override
    public int size() {
        int count = 0;
        for (Entry<Key, Value> entry : table) {
            Entry<Key, Value> current = entry;
            while (current != null) {
                count++;
                current = current.next;
            }
        }
        return count;
    }
}
