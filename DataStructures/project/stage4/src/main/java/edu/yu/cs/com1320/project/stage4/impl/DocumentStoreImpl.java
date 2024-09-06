package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
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
            if (previousDocument != null) {
                hashCode = previousDocument.hashCode();
                documentMap.put(url, null);
                commandStack.push(new GenericCommand<>(url, targetUrl -> {
                        documentMap.put(targetUrl, previousDocument);
                        removeFromTrie(previousDocument);
                }));


            }
        } else {
            try {
                // create an input stream
                byte[] inputByte = input.readAllBytes();
                String text = new String(inputByte);
                //Document newDocument = new DocumentImpl(uri, inputByte);
                if (format == DocumentFormat.TXT) {
                    document = new DocumentImpl(url, text);
                     documentMap.put(url, document);
                     triePut(document);

                    commandStack.push(new GenericCommand<>(url, targetUrl -> {
                        removeFromTrie(get(url));
                        documentMap.put(targetUrl, previousDocument);
                }));
                } else {
                    document = new DocumentImpl(url, inputByte);
                     documentMap.put(url, document);
                     commandStack.push(new GenericCommand<>(url, targetUrl -> {
                        documentMap.put(targetUrl, previousDocument);
                }));
                }
                if (previousDocument != null) {
                    hashCode = previousDocument.hashCode();
                }

                /*if (documentMap.containsKey(uri)) {
                    Document previousDocument = documentMap.get(uri);
                    hashCode = previousDocument.hashCode();
                //}*/
                //revisar
                //Document oldDocument = documentMap.put(uri, document);
                   /* this.commandStack.push(new Command(uri, target -> {
                        if (oldDocument != null) {
                            documentMap.put(target, oldDocument);
                        } else {
                            documentMap.put(target, null);
                        }
                    }));*/
            } catch (IOException e) {
                throw new IOException("There was an issue reading the input");
            }
        }
        /*
        if (document != null) {
        triePut(document);
        }
         */
        return hashCode;
    }
    private void triePut(Document document) {
        String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
        for (String word : words) {
            trie.put(word, document);
        }
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
        Document document = documentMap.get(url);
        if (documentMap.containsKey(url)) {
            Document oldDocument = documentMap.put(url, null);
            removeFromTrie(document);
            if (oldDocument != null) {
                Undoable command = new GenericCommand<>(url, targetUrl -> {
                    this.documentMap.put(targetUrl, oldDocument);
                    triePut(oldDocument);
                });
                commandStack.push(command);
            }
            return true;
        }
        return false;
    }
    private void removeFromTrie(Document document) {
        String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
        for (String word : words) {
            trie.delete(word, document);
        }
    }
    /*
    private void removeAncestorsIfNeeded(String word) {
    Node currentNode = trie.root;
    for (int i = 0; i < word.length(); i++) {
        char c = word.charAt(i);
        Node childNode = currentNode.getChild(c);
        if (childNode == null) {
            break; // No more ancestors to remove
        }
        Set<Document> documents = childNode.getDocuments();
        if (documents.isEmpty()) {
            // If no documents exist at this node, remove it and move up to the parent
            currentNode.removeChild(c);
            currentNode = childNode;
        } else {
            break; // Stop removing ancestors
        }
    }
}

     */


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
        // Check if the last command is a GenericCommand
        if (lastCommand instanceof GenericCommand) {
            GenericCommand<?> genericCommand = (GenericCommand<?>) lastCommand;
            genericCommand.undo();
            commandStack.pop();
        } else if (lastCommand instanceof CommandSet) {
            CommandSet<?> commandSet = (CommandSet<?>) lastCommand;
            commandSet.undoAll();
            commandStack.pop();
        } else {
            throw new IllegalStateException("Unknown command type on the stack");
        }
        /*
        if (lastCommand != null) {
            lastCommand.undo();
        } else {
            throw new IllegalStateException("No actions to be undone");
        }
         */
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
            //if (command != null && command.getUri().equals(url)) {
            if (command instanceof CommandSet && ((CommandSet<URI>) command).containsTarget(url)) {
                ((CommandSet<URI>) command).undo(url);
                encontrado = true;

                break;
                /*
                // Restore commands from tempStack to commandStack
                while (tempStack.size() > 0) {
                    commandStack.push(tempStack.pop());
                }
                break;
                 */
            } else if (command instanceof GenericCommand && ((GenericCommand<URI>) command).getTarget().equals(url)) {
                command.undo();
                encontrado = true;
                /*
                while (tempStack.size() > 0) {
                    commandStack.push(tempStack.pop());
                }
                 */
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

        /*boolean encontrado = false;
        StackImpl<Command> tempStack = new StackImpl<>();

        while (commandStack.size() > 0) {
            Command command = commandStack.pop();
            if (command.getUri().equals(url)) {
                encontrado = true;
                command.undo();
                break;
            }
            tempStack.push(command);
        }

        // Restore the commands popped off the stack
        while (tempStack.size() > 0) {
            commandStack.push(tempStack.pop());
        }

        if (!encontrado) {
            throw new IllegalStateException("There are no actions on the command stack for the given URI");
        }
    }*/

        /*boolean found = false;
        StackImpl<Command> tempStack = new StackImpl<>();

        // Check if there are actions associated with the URI
        while (commandStack.size() > 0) {
            Command command = commandStack.pop();
            if (command != null && command.getUri().equals(url)) {
                found = true;
                command.undo();
                // Restore commands from tempStack to commandStack
                while (tempStack.size() > 0) {
                    commandStack.push(tempStack.pop());
                }
                break;
            }
            // Save commands that don't match the URI in tempStack
            tempStack.push(command);
        }

        // Restore commands from tempStack to commandStack
        while (tempStack.size() > 0) {
            commandStack.push(tempStack.pop());
        }

        // If no actions associated with the URI were found, throw an exception
        if (!found) {
            throw new IllegalStateException("There are no actions on the command stack for the given URI");
        }*/
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
        return trie.getSorted(keyword, Comparator.comparingInt((Document doc) -> doc.wordCount(keyword)));
        /*List<Document> match = new ArrayList<>();
        Set<Document> documents = trie.get(keyword);
        if (documents != null) {
            match.addAll(documents);
        }
        match.sort(Comparator.comparingInt(doc -> -doc.wordCount(keyword)));
        return match;

         */
        /*for (Document document : documentMap.values()) {
            String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
            for (String word : words) {
                trie.put(word, document);
                /*if (keyword.equals(word)) {
                    match.add(document);
                    break; // parar de buscar en el document cuando lo encuentra
                }
            }
        }
        match.sort(Comparator.comparingInt(doc -> -doc.wordCount(keyword)));
        return match;
        */
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
        return trie.getAllWithPrefixSorted(keywordPrefix, Comparator.comparingInt((Document doc) -> doc.wordCount(keywordPrefix)));
/*
        List<Document> match = new ArrayList<>();
        List<String> palabrasPrefix = trie.getAllWithPrefixSorted(keywordPrefix, Comparator.reverseOrder());
        for (String word : palabrasPrefix) {
            Set<Document> documents = trie.get(word);
            if (documents != null) {
                match.addAll(documents);
            }
        }
 */

        //esta es otra forma
        /*
        for (Document document : documentMap.values()) {
            String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
            for (String word : words) {
                if (word.startsWith(keywordPrefix)) {
                    match.add(document);
                    break; // parar de buscar en el document cuando lo encuentra
                }
            }
        }*/

        //match.sort(Comparator.comparingInt(doc -> -doc.wordCount(keywordPrefix)));
        //return match;


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
                String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
                for (String word : words) {
                    if (Objects.equals(word, keyword)) {
                        deletedDoc.add(uri/*document.getKey()*/);
                    /*
                    for (String palabra : words) {
                        trie.delete(palabra, document);
                        //trie.deleteAll(palabra);
                    }
                     */
                        commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                            this.documentMap.put(targetUri, document);
                            triePut(document);
                        }));
                        break; // parar de buscar en el document cuando lo encuentra
                    }
                }
            }
        }
        commandStack.push(commandSet);

        for (URI uri : deletedDoc) {
            Document document = documentMap.get(uri);
            documentMap.put(uri, null);
            removeFromTrie(document);
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
        }
        commandStack.push(commandSet);
        /*
        for (Document document : documentsToDelete) {
            URI uri = document.getKey();
            documentMap.put(uri, null);
            removeFromTrie(document);
        }
         */

        return deletedDoc;

        //return trie.deleteAllWithPrefix(keywordPrefix);
        /*
        Set<URI> deletedDoc = new HashSet<>();
        for (Document document : documentMap.values()) {
            String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
            for (String word : words) {
                if (word.startsWith(keywordPrefix)) {
                    deletedDoc.add(document.getKey());
                    break; // Stop searching in the document once the prefix is found
                }
            }
        }
        for (URI uri : deletedDoc) {
            documentMap.put(uri, null); // Remove from the map
        }
        return deletedDoc;
         */
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
        if (keywordPrefix.isEmpty() || keysValues.isEmpty()){
            return Collections.emptyList();
        }
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
                deletedDocs.add(document.getKey());
                String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
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
        commandStack.push(commandSet);
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
            String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
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
        commandStack.push(commandSet);
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
            String[] words = document.getDocumentTxt().split("[^\\p{IsAlphabetic}0-9']+");
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
        commandStack.push(commandSet);
        return deletedDoc;
    }
}