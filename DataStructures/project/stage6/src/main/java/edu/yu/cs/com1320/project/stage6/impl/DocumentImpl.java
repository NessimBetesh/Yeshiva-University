package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.impl.BTreeImpl;

import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document/*, Comparable<Document>*/ {
    private URI uri;
    private String text;
    private byte[] binaryData;
    //private BTreeImpl<String, String> metaDataTree = new BTreeImpl<>();
    Map<String, Integer> wordCount;
    private long lastUsedTime;
    private HashMap<String, String> metaDataMap;


    public DocumentImpl(URI uri, String text, Map<String, Integer> wordCountMap) {
        if (uri == null || uri.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("txt cannot be null");
        }
        this.uri = uri;
        this.text = text;
        if (wordCountMap != null) {
            this.wordCount = new HashMap<>(wordCountMap);
        }
        else {
            this.wordCount = new HashMap<>();
            String[] palabras = text.split(" ");
            for (var word : palabras) {
                String fixedWord = normalizedtext(word);
                if (!(wordCount.containsKey(fixedWord))) {
                    wordCount.put(fixedWord, 1);
                } else {
                    int cant = wordCount.get(fixedWord);
                    wordCount.put(fixedWord, cant + 1);
                }
            }
        }
        this.lastUsedTime = System.nanoTime();
        this.metaDataMap = new HashMap<>();
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || uri.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (binaryData == null || binaryData.length == 0) {
            throw new IllegalArgumentException("binaryData cannot be null");
        }
        this.uri = uri;
        this.binaryData = binaryData;
        this.lastUsedTime = System.nanoTime();
        this.metaDataMap = new HashMap<>();
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return Math.abs(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentImpl that = (DocumentImpl) o;
        return hashCode() == that.hashCode();
    }

    /**
     * @param key   key of document metadata to store a value for
     * @param value value to store
     * @return old value, or null if there was no old value
     * @throws IllegalArgumentException if the key is null or blank
     */
    @Override
    public String setMetadataValue(String key, String value) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key cannot be empty");
        }
        String oldValue = metaDataMap.get(key);
        metaDataMap.put(key, value);
        return oldValue;

    }

    /**
     * @param key metadata key whose value we want to retrieve
     * @return corresponding value, or null if there is no such key
     * @throws IllegalArgumentException if the key is null or blank
     */
    @Override
    public String getMetadataValue(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        return this.metaDataMap.get(key);
    }

    /**
     * @return a COPY of the metadata saved in this document
     */
    @Override
    public HashMap<String, String> getMetadata() {
        /*
        HashMap<String, String> nuevo = new HashMap<>();
        for (String key : metaDataMap.keySet()) {
            String value = metaDataMap.get(key);
            if (value != null) {
                nuevo.put(key, value);
            }
        }
        return nuevo;
         */
        return new HashMap<>(this.metaDataMap);
    }

    /**
     * @param metadata
     */
    @Override
    public void setMetadata(HashMap<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            throw new IllegalArgumentException("metadata cannot be empty");
        }
        /*
        if (metaDataMap == null){
            metaDataMap = new HashMap<>();
        }
        for (String key : metadata.keySet()) {
            String value = metadata.get(key);
            if (value != null) {
                metaDataMap.put(key, value);
            }
        }
         */
        this.metaDataMap = new HashMap<>(metadata);
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return text;
    }

    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData() {
        return binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey() {
        return uri;
    }

    /**
     * how many times does the given word appear in the document?
     *
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */

    @Override
    public int wordCount(String word) {
        //return wordCount.getOrDefault(word, 0);
        if (word == null || word.isEmpty()) {
            return 0;
        }
        if (binaryData != null) {
            return 0;
        }
        /*if (wordCount.isEmpty()) {
            String[] words = getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
            for (String documentWord : words) {
                String palabra = documentWord.toLowerCase();

                // Update the word count in the wordCount
                wordCount.put(palabra, wordCount.getOrDefault(palabra, 0) + 1);
            }
        }
        return wordCount.getOrDefault(word.toLowerCase(), 0);
        */
        String normalInput = normalizedtext(word);
        if (wordCount.isEmpty()) {
            String normalizedWord = normalizedtext(getDocumentTxt());
            String[] words = normalizedWord.split(" ");
            for (String documentWord : words) {
                String normalWord = normalizedtext(documentWord);
                wordCount.put(normalWord, wordCount.getOrDefault(normalWord, 0) + 1);
            }
        }

        return wordCount.getOrDefault(normalInput, 0);
    }
    private String normalizedtext(String word) {
        // Remove symbols and punctuation
        if (word != null) {
            return word.replaceAll("[^\\p{IsAlphabetic}0-9\\s]+", "");
        }
        return null;
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        Set<String> allWords = new HashSet<>();
        String normalizedWord = normalizedtext(getDocumentTxt());
        if (normalizedWord == null){
            return allWords;
        }
        String[] words = normalizedWord.split(" ");
        for (String word : words) {
            allWords.add(normalizedtext(word));
        }
        return allWords;
    }

    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    @Override
    public long getLastUseTime() {
        return lastUsedTime;
    }

    /**
     * @param timeInNanoseconds
     */
    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        lastUsedTime = timeInNanoseconds;
    }

    /**
     * @return a copy of the word to count map so it can be serialized
     */
    @Override
    public HashMap<String, Integer> getWordMap() {
        HashMap<String, Integer> wordMap = new HashMap<>();
        for (String word : getWords()) {
            wordMap.put(word, wordCount.get(word));
        }
        return wordMap;
    }

    /**
     * This must set the word to count map durlng deserialization
     *
     * @param wordMap
     */
    @Override
    public void setWordMap(HashMap<String, Integer> wordMap) {
        if (wordMap == null || wordMap.isEmpty()) {
            throw new IllegalArgumentException("wordMap cannot be null");
        }
        this.wordCount = wordMap;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Document o) {
        if(o == null){
            throw new NullPointerException();
        }
        return Long.compare(this.getLastUseTime(), o.getLastUseTime());
        /*
        if (this.getLastUseTime() > o.getLastUseTime()) {
            return 1;
        }
        if (this.getLastUseTime() < o.getLastUseTime()) {
            return -1;
        }
        return 0;
         */
    }
}