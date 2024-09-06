package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.Document;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

public class DocumentImpl implements Document {
    private URI uri;
    private String txt;
    private byte[] binaryData;
    private Map<String, String> metaDataMap = new HashMap<>();
    public DocumentImpl(URI uri, String txt){
        if (uri == null || uri.toString().isEmpty()){
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (txt == null || txt.isEmpty()) {
            throw new IllegalArgumentException("txt cannot be null");
        }
        this.uri = uri;
        this.txt = txt;
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || uri.toString().isEmpty()){
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException("binaryData cannot be null");
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }


@Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (txt != null ? txt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
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
        if (key == null || key.isEmpty()){
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
        if (key == null || key.isEmpty()){
            throw new IllegalArgumentException("Key cannot be empty");
        }
        return metaDataMap.get(key);
    }

    /**
     * @return a COPY of the metadata saved in this document
     */
    @Override
    public HashMap<String, String> getMetadata() {
        return new HashMap<>(metaDataMap);
    }

    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return txt;
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
}
