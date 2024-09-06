package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.undo.Command;
import edu.yu.cs.com1320.project.impl.StackImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> documentMap = new HashTableImpl<>();
    private StackImpl<Command> commandStack = new StackImpl<>();
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
        Command command = new Command(uri, targetUrl -> document.setMetadataValue(key, oldValue));
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
        value = this.value;
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
                commandStack.push(new Command(url, targetUrl -> documentMap.put(targetUrl, previousDocument)));
            }
        } else {
            try {
                // create an input stream
                byte[] inputByte = input.readAllBytes();
                String text = new String(inputByte);
                //Document newDocument = new DocumentImpl(uri, inputByte);
                if (format == DocumentFormat.TXT) {
                    document = new DocumentImpl(url, text);
                } else {
                    document = new DocumentImpl(url, inputByte);
                }
                documentMap.put(url, document);
                Command command = new Command(url, targetUrl -> {
                    if (previousDocument == null) {
                        documentMap.put(targetUrl, null);
                    } else {
                        documentMap.put(targetUrl, previousDocument);
                    }
                });
                commandStack.push(command);
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
        if (documentMap.containsKey(url)) {
            Document oldDocument = documentMap.put(url, null);
            if (oldDocument != null) {
                this.commandStack.push(new Command(url, targetUrl -> {
                    this.documentMap.put(targetUrl, oldDocument);
                }));
            }
            return true;
        }
        return false;
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
        // Pop the last command from the stack and undo it
        Command lastCommand = commandStack.pop();
        if (lastCommand != null) {
            lastCommand.undo();
        }
        else {
            throw new IllegalStateException("No actions to be undone");
        }
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
        StackImpl<Command> tempStack = new StackImpl<>();
        boolean encontrado = false;
        // Pop commands from commandStack until it's empty or the URI matches
        while (commandStack.size() > 0) {
            Command command = commandStack.pop();
            if (command != null && command.getUri().equals(url)) {
                command.undo();
                encontrado = true;
                // Restore commands from tempStack to commandStack
                while (tempStack.size() > 0) {
                    commandStack.push(tempStack.pop());
                }
                break;
            }
            tempStack.push(command);
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
}
