package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DocumentStoreImpl implements DocumentStore {
    private Map<URI, Document> documentMap = new HashMap<>();

    private String value;
    /**
     * set the given key-value metadata pair for the document at the given uri
     *
     * @param uri
     * @param key
     * @param value
     * @return the old value, or null if there was no previous value
     * @throws IllegalArgumentException if the uri is null or blank, if there is no document stored at that uri, or if the key is null or blank
     */
    @Override
    public String setMetadata(URI uri, String key, String value) {
        if (uri == null || uri.toString().isEmpty()){
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (key == null || key.isEmpty()){
            throw new IllegalArgumentException("key cannot be null");
        }
        Document document = get(uri);
        if (document == null){
            throw new IllegalArgumentException("No document found at URI: " + uri);
        }
        return document.setMetadataValue(key, value);
    }

    /**
     * get the value corresponding to the given metadata key for the document at the given uri
     *
     * @param uri
     * @param key
     * @return the value, or null if there was no value
     * @throws IllegalArgumentException if the uri is null or blank, if there is no document stored at that uri, or if the key is null or blank
     */
    @Override
    public String getMetadata(URI uri, String key) {
        value = this.value;
        if (uri == null || uri.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (key == null || key.isEmpty()){
            throw new IllegalArgumentException("key cannot be null");
        }
        Document document = get(uri);
        if (document == null) {
            return null;
        }
        return document.getMetadataValue(key);
    }

    /**
     * @param input  the document being put
     * @param uri    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException              if there is an issue reading input
     * @throws IllegalArgumentException if uri is null or empty, or format is null
     */
    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || uri.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }
        if (format == null){
            throw new IllegalArgumentException("Format cannot be null");
        }
        //@return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
        //     * @throws IOException              if there is an issue reading input
        int hashCode = 0;
        if (input == null){
            if (documentMap.containsKey(uri)) {
                Document deletedDocument = documentMap.remove(uri);
                hashCode = deletedDocument.hashCode(); //guardar hashcode del borrado
            }
        }
        else {
                try {
                    // create an input stream
                    byte[] inputByte = input.readAllBytes();
                    Document document;
                    if (format == DocumentFormat.TXT){
                        document = new DocumentImpl(uri, new String(inputByte));
                    }
                    else{
                        document = new DocumentImpl(uri, inputByte);
                    }
                    if (documentMap.containsKey(uri)) {
                        Document previousDocument = documentMap.get(uri);
                        hashCode = previousDocument.hashCode();
                    }
                    documentMap.put(uri, document);
                }
                catch (IOException e) {
                    throw new IOException("There was an issue reading the input");
                }
            }
        return hashCode;
    }

    /**
     * @param url the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI url) {
        return documentMap.get(url);
    }

    /**
     * @param url the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI url) {
        if (documentMap.containsKey(url)){
            documentMap.remove(url);
            return true;
        }
        return false;
    }
}
