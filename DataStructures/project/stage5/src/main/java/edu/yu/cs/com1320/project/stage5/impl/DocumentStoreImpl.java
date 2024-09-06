package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> documentMap = new HashTableImpl<>();
    private StackImpl<Undoable> commandStack = new StackImpl<>();
    private TrieImpl trie = new TrieImpl<>();
    MinHeapImpl<Document> heap = new MinHeapImpl<>();
    private long maxCount = -1;
    private long maxByte = -1;
    private long totalCount = 0;
    private long totalByte = 0;


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
        if (uri == null || uri.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Document document = get(uri);
        if (document == null) {
            throw new IllegalArgumentException("No document found at URI: " + uri);
        }
        String oldValue = document.getMetadataValue(key);
        Undoable command = new GenericCommand<>(uri, targetUrl -> document.setMetadataValue(key, oldValue));
        commandStack.push(command);
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
        if (uri == null || uri.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Document document = get(uri);
        if (document == null) {
            return null;
        }
        document.setLastUseTime(System.nanoTime());
        return document.getMetadataValue(key);
    }

    /**
     * @param input  the document being put
     * @param url    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException              if there is an issue reading input
     * @throws IllegalArgumentException if url or format are null
     */
    @Override
    public int put(InputStream input, URI url, DocumentFormat format) throws IOException {
        if (url == null || url.toString().isEmpty()) {
            throw new IllegalArgumentException("URI cannot be null or empty");
        }
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        //@return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
        //     * @throws IOException              if there is an issue reading input
        Document previousDocument = documentMap.get(url);
        Document document = null;
        int hashCode = 0;
        if (input == null) {
            hashCode = deletePut(url, previousDocument);
        } else {
            try {
                hashCode = doPut(input, url, format, previousDocument);
            } catch (IOException e) {
                throw new IOException("There was an issue reading the input");
            }
        }
        if (maxCount > 0 && totalCount > maxCount) {
            borrarDocumentosViejo();
        }
        memoryLimitCount();
        memoryByteCount();
        return hashCode;
    }

    private int deletePut(URI url, Document previousDocument) {
        int hashCode = 0;
        if (previousDocument != null) {
            if (calcBytes(previousDocument) > maxByte && maxByte != -1) {
                throw new IllegalArgumentException("Document size exceeds the maximum byte limit");
            }
            hashCode = previousDocument.hashCode();
            documentMap.put(url, null);
            removeFromTrie(previousDocument);
            removeFromHeap(previousDocument);
            //previousDocument.setLastUseTime(System.nanoTime());

            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                documentMap.put(targetUrl, previousDocument);
                triePut(previousDocument);
            }));
        }
        return hashCode;
    }

    private int doPut(InputStream input, URI url, DocumentFormat format, Document previousDocument) throws IOException {
        Document document = null;
        int hashCode = 0;
        byte[] inputByte = input.readAllBytes();

        String text = new String(inputByte);
        int documentSize = inputByte.length;
        if (documentSize != 0 && calcBytes(previousDocument) > maxByte && maxByte != -1) {
            throw new IllegalArgumentException("Document size exceeds the maximum byte limit");
        }
        /*
        if (format == DocumentFormat.BINARY){
            document = new DocumentImpl(url, inputByte);
        }
         */
        if (format == DocumentFormat.TXT) {
            document = new DocumentImpl(url, text);
            if (document != null && calcBytes(document) > maxByte && maxByte != -1) {
                throw new IllegalArgumentException("Document size exceeds the maximum byte limit");
            }
            //hashCode = document.hashCode();
            documentMap.put(url, document);
            triePut(document);

            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                documentMap.put(targetUrl, previousDocument); //tamn puede ser null creo
                if (previousDocument != null) {
                    documentMap.put(targetUrl, previousDocument);
                }
            }));
        } else {
            document = new DocumentImpl(url, inputByte);
            if (document != null && calcBytes(document) > maxByte && maxByte != -1) {
                throw new IllegalArgumentException("Document size exceeds the maximum byte limit");
            }
            //hashCode = document.hashCode();
            documentMap.put(url, document);
            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                documentMap.put(targetUrl, previousDocument);
                removeFromTrie(previousDocument);
            }));
        }
        /*
        if (heap != null) {
            if (previousDocument != null) {
                hashCode = previousDocument.hashCode();
                //totalByte -= calcBytes(previousDocument);
                removeFromTrie(previousDocument);
                removeFromHeap(previousDocument);
            }
         */
        document.setLastUseTime(System.nanoTime());
        heap.insert(document);
        heap.reHeapify(document);
        totalCount++;
        totalByte += calcBytes(document);

        return hashCode;
    }

    private void removeFromHeap(Document previousDocument) {
        //int index = heap.getArrayIndex(previousDocument);
        //if (index != -1) {
        if (previousDocument != null) {
            previousDocument.setLastUseTime(Long.MIN_VALUE); //mayb Long.Max_Value, 0
            heap.reHeapify(previousDocument);
            heap.remove();
            totalCount--;
            totalByte -= calcBytes(previousDocument);
        }
            /*
            CommandSet<Document> commandSet = new CommandSet<>();
            commandStack.push(new GenericCommand<>(previousDocument.getKey(), targetUrl -> {
                heap.insert(previousDocument);
                previousDocument.setLastUseTime(System.nanoTime());
            }));
            commandStack.push(commandSet);
             */
    }

    private void triePut(Document document) {
        if (document != null) {
            Set<String> words = document.getWords();
            for (String word : words) {
                trie.put(word, document);
            }
        }
    }

    /**
     * @param url the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI url) {
        if(url==null){
            throw new IllegalArgumentException();
        }
        Document document = documentMap.get(url);
        if(document==null){
            return null;
        }
        document.setLastUseTime(System.nanoTime());
        heap.reHeapify(document);
        return document;
    }

    /**
     * @param url the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI url) {
        Document document = documentMap.get(url);
        if (documentMap.containsKey(url)) {
            // no creo q hay q hacerlo aqui document.setLastUseTime(System.nanoTime());
            Document oldDocument = documentMap.put(url, null);
            if (oldDocument != null) {
                Undoable command = new GenericCommand<>(url, targetUrl -> {
                    this.documentMap.put(targetUrl, oldDocument);
                    triePut(oldDocument);
                });
                removeFromTrie(document);
                removeFromHeap(document);
                commandStack.push(command);
            }
            return true;
        }
        return false;
    }
    private void removeFromTrie(Document document) {
        if (document != null) {
            Set<String> words = document.getWords();
            for (String word : words) {
                trie.delete(word, document);
            }
        }
    }
    /**
     * undo the last put or delete command
     *
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        if (commandStack == null || commandStack.toString().isEmpty()) {
            throw new IllegalStateException("No actions to be undone");
        }
        Undoable lastCommand = commandStack.peek();
        long currentTime = System.nanoTime();
        // Check if the last command is a GenericCommand
        if (lastCommand instanceof GenericCommand) {
            GenericCommand<?> genericCommand = (GenericCommand<?>) lastCommand;
            genericCommand.undo();
            commandStack.pop();
            Document document = getDocumentCommand(genericCommand);
            if (document != null) {
                document.setLastUseTime(currentTime);
                if (heap != null) {
                    heap.insert(document);
                    heap.reHeapify(document);
                    totalCount++;
                    memoryLimitCount();
                    memoryByteCount();
                }
            }
        } else if (lastCommand instanceof CommandSet) {
            CommandSet<?> commandSet = (CommandSet<?>) lastCommand;
            List<? extends GenericCommand<?>> setList = commandSet.stream().toList();
            commandSet.undoAll();
            commandStack.pop();
            for (Undoable command : setList) {
                Document document = getDocumentCommand((GenericCommand<?>) command);
                if (document != null) {
                    document.setLastUseTime(currentTime);
                    if (heap != null) {
                        heap.insert(document);
                        heap.reHeapify(document);
                        totalCount++;
                        memoryLimitCount();
                        memoryByteCount();
                    }
                }
            }
        } else {
            throw new IllegalStateException("Unknown command type on the stack");
        }
    }

    private Document getDocumentCommand(GenericCommand<?> command) {
        URI uri = (URI) command.getTarget();
        if (uri != null) {
            return documentMap.get(uri);
        }
        return null;
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     *
     * @param url
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI url) throws IllegalStateException {
        if (commandStack.toString().isEmpty() || commandStack.peek() == null) {
            throw new IllegalStateException("There are no actions on the command stack for the given URI");
        }

        StackImpl<Undoable> tempStack = new StackImpl<>();
        boolean encontrado = false;
        // Pop commands from commandStack until it's empty or the URI matches
        while (commandStack.size() > 0) {
            Undoable command = commandStack.pop();
            long currentTime = System.nanoTime();
            //if (command != null && command.getUri().equals(url)) {
            if (command instanceof CommandSet && ((CommandSet<URI>) command).containsTarget(url)) {
                CommandSet<URI> commandSet = ((CommandSet<URI>) command);
                //commandSet.undo(url);
                //((CommandSet<URI>) command).undo(url);
                for (Undoable cmd : commandSet){
                    if(cmd instanceof GenericCommand && ((GenericCommand<URI>) cmd).getTarget().equals(url)){
                        GenericCommand<URI> genCommand = (GenericCommand<URI>) cmd;
                        genCommand.undo();
                        Document document = getDocumentCommand(genCommand);
                        if (document != null) {
                            document.setLastUseTime(currentTime);
                            if (heap != null) {
                                heap.insert(document);
                                heap.reHeapify(document);
                                totalCount++;
                                memoryLimitCount();
                                memoryByteCount();
                            }
                        }
                    }
                }
                encontrado = true;
                break;
            } else if (command instanceof GenericCommand && ((GenericCommand<URI>) command).getTarget().equals(url)) {
                command.undo();
                encontrado = true;
                //Update last used time of the document with the undo
                Document document = getDocumentCommand((GenericCommand<?>) command);
                if (document != null) {
                    document.setLastUseTime(currentTime);
                    if (heap != null) {
                        heap.insert(document);
                        heap.reHeapify(document);
                        totalCount++;
                        memoryLimitCount();
                        memoryByteCount();
                    }
                }
                break;
            }
            tempStack.push(command);
        }
        while (tempStack.size() > 0) {
            commandStack.push(tempStack.pop());
        }
        if (!encontrado) {
            throw new IllegalStateException("There are no actions on the command stack for the given URI");
        }
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        List<Document> document = trie.getSorted(keyword, Comparator.comparingInt((Document doc) -> doc.wordCount(keyword)));
        long currentTime = System.nanoTime();
        for (Document doc : document) {
            doc.setLastUseTime(currentTime);
            heap.reHeapify(doc);
        }
        return document;
    }

    /**
     * Retrieve all documents that contain text which starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix) {
        List<Document> document = trie.getAllWithPrefixSorted(keywordPrefix, Comparator.comparingInt((Document doc) -> doc.wordCount(keywordPrefix)));
        long currentTime = System.nanoTime();
        for (Document doc : document) {
            doc.setLastUseTime(currentTime);
            heap.reHeapify(doc);
        }
        return document;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    //revisar lo mas probable esta mal. falta lo de quitarlo en el trie
    public Set<URI> deleteAll(String keyword) {
        //return trie.deleteAll(keyword);
        // CHUPALO
        CommandSet<URI> commandSet = new CommandSet<>();
        Set<URI> deletedDoc = new HashSet<>();
        for (Document document : documentMap.values()) {
            URI uri = document.getKey();
            if (document.getDocumentTxt() != null) {
                Set<String> words = document.getWords();
                for (String word : words) {
                    if (Objects.equals(word, keyword)) {
                        deletedDoc.add(uri/*document.getKey()*/);
                        commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                            this.documentMap.put(targetUri, document);
                            triePut(document);

                        }));
                        break; // parar de buscar en el document cuando lo encuentra
                    }
                }
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        for (URI uri : deletedDoc) {
            if (documentMap != null) {
                Document document = documentMap.get(uri);
                documentMap.put(uri, null);
                removeFromTrie(document);
                removeFromHeap(document);
            }
        }
        return deletedDoc;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        Set<URI> deletedDoc = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        // Get all documents associated with the prefix from the Trie
        Set<Document> documentsToDelete = trie.deleteAllWithPrefix(keywordPrefix);

        // Remove documents from the documentMap and Trie
        for (Document document : documentsToDelete) {
            URI uri = document.getKey();
            commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                this.documentMap.put(targetUri, document);
                triePut(document);
            }));
            deletedDoc.add(uri);
            documentMap.put(uri, null);
            removeFromTrie(document);
            removeFromHeap(document);
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        /*
        for (Document document : documentsToDelete) {
            URI uri = document.getKey();
            documentMap.put(uri, null);
            removeFromTrie(document);
            removeFromHeap(document);
        }
         */
        return deletedDoc;
    }

    /**
     * @param keysValues metadata key-value pairs to search for
     * @return a List of all documents whose metadata contains ALL OF the given values for the given keys. If no documents contain all the given key-value pairs, return an empty list.
     */
    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) {
        if (keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        List<Document> matchingDocs = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (Document document : documentMap.values()) {
            HashTable<String, String> metadata = document.getMetadata();
            boolean igual = true;

            for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String metadataValue = metadata.get(key);

                if (metadataValue == null || !metadataValue.equals(value)) {
                    igual = false;
                    break;
                }
            }
            if (igual) {
                //update last uded time
                document.setLastUseTime(currentTime);
                matchingDocs.add(document);
            }
        }
        return matchingDocs;
    }

    /**
     * Retrieve all documents whose text contains the given keyword AND which has the given key-value pairs in its metadata
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @param keysValues
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        List<Document> matches = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (Document document : documentMap.values()) {
            if (document.getDocumentTxt().contains(keyword)) {
                HashTable<String, String> metadata = document.getMetadata();
                boolean metadataIgual = true;

                for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String metadataValue = metadata.get(key);

                    if (metadataValue == null || !metadataValue.equals(value)) {
                        metadataIgual = false;
                        break;
                    }
                }
                if (metadataIgual) {
                    document.setLastUseTime(currentTime);
                    matches.add(document);
                }
            }
        }
        matches.sort(Comparator.comparingInt(doc -> /*-*/doc.wordCount(keyword)));
        return matches;
    }

    /**
     * Retrieve all documents that contain text which starts with the given prefix AND which has the given key-value pairs in its metadata
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @param keysValues
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        if (keywordPrefix.isEmpty() || keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        long currentTime = System.nanoTime();
        List<Document> matches = new ArrayList<>();
        for (Document document : documentMap.values()) {
            if (document.getDocumentTxt().contains(keywordPrefix)) {
                HashTable<String, String> metadata = document.getMetadata();
                boolean metadataIgual = true;

                for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String metadataValue = metadata.get(key);

                    if (metadataValue == null || !metadataValue.equals(value)) {
                        metadataIgual = false;
                        break;
                    }
                }
                if (metadataIgual) {
                    document.setLastUseTime(currentTime);
                    heap.reHeapify(document);
                    matches.add(document);
                }
            }
        }
        matches.sort(Comparator.comparingInt(doc -> /*-*/doc.wordCount(keywordPrefix)));
        return matches;
    }

    /**
     * Completely remove any trace of any document which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     *
     * @param keysValues
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) {
        Set<URI> deletedDocs = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        for (Document document : documentMap.values()) {

            boolean matchesMeta = true;
            for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!document.getMetadata().containsKey(key) || !document.getMetadata().get(key).equals(value)) {
                    matchesMeta = false;
                    break;
                }
            }
            if (matchesMeta) {
                if (document.getDocumentTxt() != null) {
                    deletedDocs.add(document.getKey());
                    Set<String> words = document.getWords();
                    documentMap.put(document.getKey(), null);
                    for (String word : words) {
                        //trie.delete(word, document);
                        trie.deleteAll(word);
                    }
                    commandSet.addCommand(new GenericCommand<>(document.getKey(), targetUri -> {
                        this.documentMap.put(targetUri, document);
                        triePut(document);
                    }));
                }
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        return deletedDocs;
    }


    /**
     * Completely remove any trace of any document which contains the given keyword AND which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @param keysValues
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) {
        Set<URI> deletedDoc = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        for (Document document : documentMap.values()) {
            boolean matchesMeta = true;
            boolean matchesKeyword = false;
            if (document.getDocumentTxt() != null) {
                Set<String> words = document.getWords();
                for (String word : words) {
                    if (keyword.equals(word)) {
                        matchesKeyword = true;
                        break;
                    }
                }
                for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!document.getMetadata().containsKey(key) || !document.getMetadata().get(key).equals(value)) {
                        matchesMeta = false;
                        break;
                    }
                }
                if (matchesKeyword && matchesMeta) {
                    deletedDoc.add(document.getKey());
                    documentMap.put(document.getKey(), null);
                    for (String word : words) {
                        trie.deleteAll(word);
                    }
                    commandSet.addCommand(new GenericCommand<>(document.getKey(), targetUri -> {
                        this.documentMap.put(targetUri, document);
                        triePut(document);
                    }));
                }
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        return deletedDoc;
    }
    /**
     * Completely remove any trace of any document which contains a word that has the given prefix AND which has the given key-value pairs in its metadata
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @param keysValues
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) {
        Set<URI> deletedDoc = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        for (Document document : documentMap.values()) {
            boolean matchesMeta = true;
            boolean matchesPrefix = false;
            Set<String> words = document.getWords();
            for (String word : words) {
                if (word.startsWith(keywordPrefix)) {
                    matchesPrefix = true;
                    break;
                }
            }
            for (Map.Entry<String, String> entry : keysValues.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!document.getMetadata().containsKey(key) || !document.getMetadata().get(key).equals(value)) {
                    matchesMeta = false;
                    break;
                }
            }
            if (matchesPrefix && matchesMeta) {
                deletedDoc.add(document.getKey());
                documentMap.put(document.getKey(), null);
                for (String word : words) {
                    trie.deleteAll(word);
                }
                commandSet.addCommand(new GenericCommand<>(document.getKey(), targetUri -> {
                    this.documentMap.put(targetUri, document);
                    triePut(document);
                }));
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        return deletedDoc;
    }

    /**
     * set maximum number of documents that may be stored
     *
     * @param limit
     * @throws IllegalArgumentException if limit < 1
     */
    @Override
    public void setMaxDocumentCount(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        this.maxCount = limit;
        memoryLimitCount();
        memoryByteCount();
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     *
     * @param limit
     * @throws IllegalArgumentException if limit < 1
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        this.maxByte = limit;
        memoryByteCount();
        memoryLimitCount();
    }
//arreglar coger del heap
    private Document getOldestDoc() {
       return heap.peek();
    }

    private void memoryLimitCount() {
        if (maxCount < 0){
            return;
        }
        while (documentMap.size() > maxCount){
            borrarDocumentosViejo();
        }
    }
    private void memoryByteCount() {
        if (maxByte < 0) {
            return;
        }
        while (calcTotalBytes() > maxByte){
            borrarDocumentosViejo();
        }
    }
    private void borrarDocumentosViejo() {
            Document oldestDocument = getOldestDoc();
            URI url = oldestDocument.getKey();
            documentMap.put(url, null);
            removeFromTrie(oldestDocument);
            removeFromHeap(oldestDocument);
            removeDocCommand(oldestDocument);

    }

    private long calcTotalBytes() {
        long totalBytes = 0;
        for (Document document : documentMap.values()) {
            totalBytes += calcBytes(document);
        }
        return totalBytes;
    }

    private long calcBytes(Document document) {
        if (document == null) {
            return 0;
        }
        if (document.getDocumentTxt() != null) {
            // For text, calculate the size using the length of the byte array
            return document.getDocumentTxt().getBytes().length;
        } else if (document.getDocumentBinaryData() != null) {
            // For binary, calculate the size using the length of the byte array
            return document.getDocumentBinaryData().length;
        } else {
            return 0; // Return 0 if there is no data
        }
    }

    private void removeDocCommand(Document document) {
        StackImpl<Undoable> tempStack = new StackImpl<>();
        while (commandStack.size() > 0) {
            Undoable command = commandStack.pop();
            if (command instanceof GenericCommand && ((GenericCommand<?>) command).getTarget().equals(document.getKey())) {
                continue;
            }
            if (command instanceof CommandSet) {
                ((CommandSet<?>) command).remove(document.getKey());
                if (!((CommandSet<?>) command).isEmpty()) {
                    tempStack.push(command);
                }
            } else {
                tempStack.push(command);
            }
        }
        while (tempStack.size() > 0) {
            commandStack.push(tempStack.pop());
        }
    }
}