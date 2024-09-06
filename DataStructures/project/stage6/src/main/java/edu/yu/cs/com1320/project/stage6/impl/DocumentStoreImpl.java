package edu.yu.cs.com1320.project.stage6.impl;


import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import edu.yu.cs.com1320.project.undo.Undoable;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

//things you have to make sure, lets break it down into pieces step by step
  //      1) asegurate que si haces put y causa un overflow manda a disk y si haces undo it brings it back

    //    2) si algo esta en disk, lets call it AlizaURI-doc1 y si yo meto AlizaURI-newDoc a memory entonces deleteas
//de disk y metes el nuevo en memory, ESO VA EN btree, however the undo logic goes in here, the docuument that was in disk goes back to disk and the other one is deleted from memory

  //      3) get() on a uri from disk brings back to memory the document respective to that uri, same for the searches, there ae no undoes on searches, searches must check if theres a doc in disk that contains the given keyword


    //    4) delete from something from memory deletes from memory, from something from disk deletes from disk


public class DocumentStoreImpl implements DocumentStore {
    //private HashTableImpl<URI, Document> documentMap = new HashTableImpl<>();
    //private HashMap<URI, Document> bTree = new HashMap<>();
    private BTreeImpl<URI, Document> bTree = new BTreeImpl<>();
    private StackImpl<Undoable> commandStack = new StackImpl<>();
    private TrieImpl<URI> trie = new TrieImpl<>();
    private MinHeapImpl<vaina> heap = new MinHeapImpl<>();
    private List<URI> lista = new ArrayList<>();
    private Map<URI, vaina> vainaMap = new HashMap();
    private Set<URI> docDisk = new HashSet<>();
    private Set<Document> diskAhora = new HashSet<>();
    private Set<URI> justDisk = new HashSet<>();
    private Set<URI> undoConEsteURI = new HashSet<>();
    private Map<URI, Set<URI>> gay = new HashMap();
    private Set<URI> borradoDeDisk = new HashSet<>();


    /*
    MinHeapImpl<URI> heap = new MinHeapImpl<>((uri1, uri2) ->{
        Document doc1 = bTree.get(uri1);
        Document doc2 = bTree.get(uri2);
        return Long.compare(doc1.getLastUseTime(), doc2.getLastUseTime());
    });
     */
    private File file;
    private long maxCount = -1;
    private long maxByte = -1;
    private long totalCount = 0;
    private long totalByte = 0;
    private DocumentPersistenceManager pm;

    public DocumentStoreImpl(File baseDir) {
        this.file = baseDir;
        try {
            pm = new DocumentPersistenceManager(baseDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bTree.setPersistenceManager(pm);
    }

    public DocumentStoreImpl() {
        this.file = new File(System.getProperty("user.dir"));
        try {
            pm = new DocumentPersistenceManager(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bTree.setPersistenceManager(pm);
    }
/*
    public DocumentStoreImpl() {
        try {
            this.pm = new DocumentPersistenceManager(null);
            this.bTree.setPersistenceManager(pm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public DocumentStoreImpl(File baseDir) {
        try {
            this.pm = new DocumentPersistenceManager<>(baseDir);
            this.bTree.setPersistenceManager(pm);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 */

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
    public String setMetadata(URI uri, String key, String value) throws IOException {
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
        /*
        if (docDisk.contains(document)){
            inMemory(document);
            vaina v = vainaMap.get(uri);
            v.setisonheap(true);
            triePut(uri);
            lista.add(uri);
            vainaMap.put(uri, v);
        }
         */
        // else {
        String oldValue = document.getMetadataValue(key);
        Undoable command = new GenericCommand<>(uri, targetUrl -> document.setMetadataValue(key, oldValue));
        commandStack.push(command);
        //    }
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
    public String getMetadata(URI uri, String key) throws IOException {
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
        Document previousDocument = bTree.get(url);
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

        undoConEsteURI.clear();
        memoryLimitCount();
        memoryByteCount();
        gay.put(url, undoConEsteURI);
        return hashCode;
    }

    private int deletePut(URI url, Document previousDocument) {
        int hashCode = 0;
        if (previousDocument != null) {
            if (calcBytes(previousDocument) > maxByte && maxByte != -1) {
                throw new IllegalArgumentException("Document size exceeds the maximum byte limit");
            }


            hashCode = previousDocument.hashCode();

            delete(url);
            /*

            vaina v = vainaMap.get(previousDocument.getKey());
            //previousDocument.setLastUseTime(System.nanoTime());


            if (vainaMap.containsKey(url)){
                commandStack.push(new GenericCommand<>(url, targetUrl -> {
                    bTree.put(targetUrl, previousDocument);
                    triePut(targetUrl);

                    if (!vainaMap.containsKey(targetUrl)){
                        vainaMap.put(targetUrl, new vaina(targetUrl));
                        heap.insert(new vaina(targetUrl));
                        //vaina v = vainaMap.get(previousDocument.getKey());
                        v.setisonheap(true);
                    }
                    else {
                        heap.insert(vainaMap.get(targetUrl));
                        //vaina v = vainaMap.get(previousDocument.getKey());
                        v.setisonheap(true);
                    }
                }));
            }
            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                bTree.put(targetUrl, previousDocument);
                triePut(targetUrl);
                //Esto es un comment, lo pongo en rojo (como tu tuches) para que lo leas,
                //si haces new vaina cada
                // vez que metes al heap como piensas comparar una vaina con otra si haces new cada vez,
                // deberias chequear si url ya esta contenido en tu vaina, y solo en caso de que no creas new vaina
                //vaina vaina = new vaina(targetUrl);

                if (!vainaMap.containsKey(targetUrl)){
                    vainaMap.put(targetUrl, new vaina(targetUrl));
                    heap.insert(new vaina(targetUrl));
                    //vaina v = vainaMap.get(previousDocument.getKey());
                    v.setisonheap(true);
                }
                else {
                    heap.insert(vainaMap.get(targetUrl));
                    //vaina v = vainaMap.get(previousDocument.getKey());
                    v.setisonheap(true);
                }
            }));

            removeFromTrie(url);
            removeFromHeap(previousDocument);
            bTree.put(url, null);

             */
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

        if (format == DocumentFormat.BINARY) {
            document = new DocumentImpl(url, inputByte);
        } else {
            document = new DocumentImpl(url, text, null);
        }
        if (document != null && calcBytes(document) > maxByte && maxByte != -1) {
            throw new IllegalArgumentException("Document size exceeds the maximum byte limit");
        }
        if (previousDocument != null && this.bTree.get(url) == null) {
            this.pm.delete(url);
        }
        //hashCode = document.hashCode();
        bTree.put(url, document);

        triePut(url);
        if (!lista.contains(url)) lista.add(url);
        vainaMap.put(url, new vaina(url));

        document.setLastUseTime(System.nanoTime());
        vaina v = vainaMap.get(document.getKey());
        //v.setisonheap(true);
        //inMemory(document);

        if (docDisk.contains(url)) {
            inMemory(document);
            vaina vaina = vainaMap.get(document.getKey());
            triePut(url);
            docDisk.remove(url);
            justDisk.remove(url);
            v.setisonheap(true);
            //    pm.deserialize(url);

            Document finalDocument = document;
            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                //Document prev = bTree.put(targetUrl, finalDocument);//tamn puede ser null creo
                Set<URI> aa = new HashSet<>(justDisk);
                try {
                    if (!docDisk.contains(finalDocument.getKey())) borarDoc(finalDocument);
                    for (URI i : aa) {
                        docDisk.remove(i);
                        justDisk.remove(i);
                        inMemory(bTree.get(i));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                justDisk.clear();
                    /*
                    if (previousDocument != null) {
                        bTree.put(targetUrl, prev);
                        heap.insert(new vaina(targetUrl));
                        totalByte -= calcBytes(finalDocument);
                        totalByte += calcBytes(prev);
                        try {
                            for (var i : justDisk) {
                                docDisk.remove(i);
                                inMemory(bTree.get(i));
                            }
                            bTree.moveToDisk(url);
                            docDisk.add(url);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                     */
            }));
            return previousDocument.hashCode();
        }
        if (previousDocument != null) {
            heap.reHeapify(v);
            v.setisonheap(true);
            Document finalDocument2 = document;
            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                bTree.put(targetUrl, previousDocument);
                removeFromTrie(finalDocument2);
                triePut(previousDocument.getKey());
                totalByte -= calcBytes(previousDocument);
                totalByte += calcBytes(finalDocument2);
            }));
            removeFromTrie(previousDocument);
            return previousDocument.hashCode();
        } else {
            heap.insert(v); //era document
            heap.reHeapify(v); //era document
            v.setisonheap(true);
            totalCount++;
            totalByte += calcBytes(document);
            Document finalDocument1 = document;
            commandStack.push(new GenericCommand<>(url, targetUrl -> {
                Document prev = bTree.put(targetUrl, finalDocument1); //tamn puede ser null creo
                if (previousDocument != null) {
                    bTree.put(targetUrl, previousDocument);
                    removeFromHeap(previousDocument);
                    totalCount--;
                    totalByte -= calcBytes(previousDocument);
                    Set<URI> aa = new HashSet<>(justDisk);
                    justDisk.clear();
                    try {
                        for (var i : aa) {
                            docDisk.remove(i);
                            inMemory(bTree.get(i));
                        }
                        bTree.moveToDisk(url);
                        docDisk.add(url);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    removeFromHeap(prev);
                    totalCount--;
                    totalByte -= calcBytes(prev);
                    bTree.put(targetUrl, null);
                    Set<String> words = prev.getWords();
                    for (String word : words) {
                        trie.delete(word, url);
                    }
                    for (var i : justDisk) {
                        docDisk.remove(i);
                        inMemory(bTree.get(i));
                    }

                }
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
        if (previousDocument != null) {
            hashCode = previousDocument.hashCode();
        }

        return hashCode;
    }


    private void removeFromHeap(Document previousDocument) {
        //int index = heap.getArrayIndex(previousDocument);
        //if (index != -1) {
        //Document doc = bTree.get(uri);
        //vaina doc = new vaina(uri);
        //URI url = doc.getKey();
        //Document document = bTree.get(url);

        if (previousDocument != null) {
            previousDocument.setLastUseTime(Long.MIN_VALUE);//mayb Long.Max_Value, 0
            //make sure you reheapify the right vaina for your url/document
            vaina v = vainaMap.get(previousDocument.getKey());
            heap.reHeapify(v);
            heap.remove();
            v.setisonheap(false);
            vainaMap.remove(previousDocument.getKey());
            //       totalCount--;
            //       totalByte -= calcBytes(previousDocument);
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

    /*
    private void inMemory(Document document) {
        URI uri = document.getKey();
        long documentBytes = calcBytes(document);

        vaina docRef = new vaina(uri);
        this.heap.insert(docRef);
        this.heap.reHeapify(docRef);

        this.totalCount++;
        this.totalByte += documentBytes;

        memoryByteCount();
        memoryLimitCount();
    }
    */
    //estee
    private void inMemory(Document document) {
        long nuevosBytes = calcBytes(document);
        URI uri = document.getKey();
        vaina vaina = new vaina(uri);
        vaina n = vainaMap.computeIfAbsent(document.getKey(), k -> new vaina(uri));
        document.setLastUseTime(System.nanoTime());
        if (!n.isonHeap()) {
            n.setisonheap(true);
            heap.insert(n);
            totalCount++;
            totalByte += nuevosBytes;
        }
        //  heap.insert(n);
        //heap.reHeapify(n);
        docDisk.remove(uri);
        memoryLimitCount();
        memoryByteCount();
        //borrarDocumentosViejo();
    }

    //Ya lo dije  en otro lado, pero recomiendo pasarle URI url y chequear si el document respectivo para este url es null
    private void triePut(URI url) {
        Document doc = bTree.get(url);
        if (doc != null) {
            Set<String> words = doc.getWords();
            for (String word : words) {
                trie.put(word, url);
            }
        }
    }

    /**
     * @param url the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException();
        }
        Document document = bTree.get(url);
        if (document == null) {
            return null;
        }
        //if the uri was sent to disk. pm.deserialize
        vaina v = vainaMap.get(url);
        if (docDisk.contains(url)) {
            inMemory(document);
            //this.bTree.put(url, document);
            //meterlo al heap
            //inMemory(document);
            //heap.insert(new vaina(url));
            docDisk.remove(url);
            v = vainaMap.get(url);
            v.setisonheap(true);
            triePut(url);
            return document;
        }
        document.setLastUseTime(System.nanoTime());
        heap.reHeapify(v);
        return document;
    }

    /**
     * @param url the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    //como puedo chequear si esta en memory un doc o un heap? hint, recuerda lo que hiciste con values, esos son los documents in memory
    @Override
    public boolean delete(URI url) {
        Document document = bTree.get(url);
        if (lista.contains(url)) {
            // no creo q hay q hacerlo aqui document.setLastUseTime(System.nanoTime());
            if (document != null) {
                Undoable command;

                if (vainaMap.containsKey(url)) {
                    vaina v = vainaMap.get(document.getKey());
                    long docBytes = calcBytes(document);
                    totalCount--;
                    totalByte -= docBytes;

                    command = new GenericCommand<>(url, targetUrl -> {
                        this.bTree.put(targetUrl, document);
                        triePut(url);
                        inMemory(document);
                        v.setisonheap(true);

                    });
                } else {
                    command = new GenericCommand<>(url, targetUrl -> {
                        this.bTree.put(targetUrl, document);
                        triePut(url);
                        try {
                            bTree.moveToDisk(url);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    });
                }


                if (vainaMap.containsKey(url)) removeFromHeap(document);
                vainaMap.remove(url);
                removeFromTrie(url);
                Document oldDocument = bTree.put(url, null);
                commandStack.push(command);
            }
            return true;
        }
        return false;
    }

    //recomiendo cambiar Document dcument a URI url y meterle eso al trie.delete, maybe cambias document != a bTree.get(url) !=
    private void removeFromTrie(URI url) {
        Document document = bTree.get(url);
        if (bTree.get(url) != null) {
            Set<String> words = document.getWords();
            for (String word : words) {
                trie.delete(word, url);
            }
        }
    }

    private void removeFromTrie(Document document) {
        Set<String> words = document.getWords();
        for (String word : words) {
            trie.delete(word, document.getKey());
        }
    }

    /**
     * undo the last put or delete command
     *
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    //remember what I alreay said in previous puts and deletes
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
            /*
            Document document = getDocumentCommand(genericCommand);
            if (document != null) {

                document.setLastUseTime(currentTime);


                if (heap != null) {
                    heap.insert(vainaMap.get(document.getKey()));
                    heap.reHeapify(vainaMap.get(document.getKey()));
                    totalCount++;
                    memoryLimitCount();
                    memoryByteCount();
                }




            }
        */
        } else if (lastCommand instanceof CommandSet) {
            CommandSet<?> commandSet = (CommandSet<?>) lastCommand;
            List<? extends GenericCommand<?>> setList = commandSet.stream().toList();
            commandSet.undoAll();
            commandStack.pop();
            for (Undoable command : setList) {
                Document document = getDocumentCommand((GenericCommand<?>) command);

                if (borradoDeDisk.remove(((GenericCommand<?>) command).getTarget())) {
                } else if (document != null) {
                    document.setLastUseTime(currentTime);
                    if (heap != null) {
                        heap.insert(vainaMap.get(document.getKey()));
                        vainaMap.get(document.getKey()).setisonheap(true);
                        //aqui lo saco del disk en undo
                        docDisk.remove(document);
                        heap.reHeapify(vainaMap.get(document.getKey()));
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
        if (!vainaMap.containsKey(uri)) {
            return null;
        }
        if (uri != null) {
            return bTree.get(uri);
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
                for (Undoable cmd : commandSet) {
                    if (cmd instanceof GenericCommand && ((GenericCommand<URI>) cmd).getTarget().equals(url)) {
                        GenericCommand<URI> genCommand = (GenericCommand<URI>) cmd;
                        genCommand.undo();
                        Document document = getDocumentCommand(genCommand);

                        if (document != null) {
                            document.setLastUseTime(currentTime);
                            if (heap != null) {
                                heap.insert(vainaMap.get(document.getKey()));
                                docDisk.remove(url);
                                heap.reHeapify(vainaMap.get(document.getKey()));
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

                /*
                if (document != null) {
                    document.setLastUseTime(currentTime);
                    if (heap != null) {
                        heap.insert(vainaMap.get(url));
                        docDisk.remove(url);
                        heap.reHeapify(vainaMap.get(document.getKey()));
                        totalCount++;
                        memoryLimitCount();
                        memoryByteCount();
                    }
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
    public List<Document> search(String keyword) throws IOException {
        //hacer un list de documents
        //btree.get
        List<URI> lista = new ArrayList<>();
        lista = trie.getSorted(keyword, (u1, u2) -> {
            if (bTree.get(u1).wordCount(keyword) == bTree.get(u2).wordCount(keyword)) {
                return 0;
            } else if (bTree.get(u1).wordCount(keyword) > bTree.get(u2).wordCount(keyword)) {
                return -1;
            } else {
                return 1;
            }
        });
        //List<URI> document = trie.getSorted(keyword, Comparator.comparingInt(uri -> bTree.get(uri) != null ? bTree.get(uri).wordCount(keyword):0));
        List<Document> result = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (URI uri : lista) {
            Document doc = bTree.get(uri);
            doc.setLastUseTime(currentTime);
            result.add(doc);
            vaina v = vainaMap.get(doc.getKey());
            //       v.setisonheap(true);
            //meter al heap
            if (v != null && v.isonHeap()) {
                heap.reHeapify(vainaMap.get(doc.getKey()));
            } else {
                inMemory(doc);
                //heap.insert(vainaMap.get(doc.getKey()));
                heap.reHeapify(vainaMap.get(doc.getKey()));
            }
        }
        return result;
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
    public List<Document> searchByPrefix(String keywordPrefix) throws IOException{
        //hacer un list de documents
        //btree.get
        List<URI> lista = new ArrayList<>();
        lista = trie.getAllWithPrefixSorted(keywordPrefix, (u1, u2) -> {
            if (bTree.get(u1).wordCount(keywordPrefix) == bTree.get(u2).wordCount(keywordPrefix)) {
                return 0;
            } else if (bTree.get(u1).wordCount(keywordPrefix) > bTree.get(u2).wordCount(keywordPrefix)) {
                return -1;
            } else {
                return 1;
            }
        });
        //List<URI> document = trie.getAllWithPrefixSorted(keywordPrefix, Comparator.comparingInt(uri -> bTree.get(uri).wordCount(keywordPrefix)));
        List<Document> result = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (URI uri : lista) {
            Document doc = bTree.get(uri);
            doc.setLastUseTime(currentTime);
            result.add(doc);
            //meter al heap
            vaina v = vainaMap.get(doc.getKey());
            //meter al heap
            if (v != null && v.isonHeap()) {
                heap.reHeapify(vainaMap.get(doc.getKey()));
            } else {
                inMemory(doc);
                //heap.insert(vainaMap.get(doc.getKey()));
                heap.reHeapify(vainaMap.get(doc.getKey()));
            }
        }
        return result;
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
        for (Document document : getURIOtro()) {
            URI uri = document.getKey();
            if (document.getDocumentTxt() != null) {
                Set<String> words = document.getWords();
                for (String word : words) {
                    if (Objects.equals(word, keyword)) {
                        deletedDoc.add(uri/*document.getKey()*/);
                        break; // parar de buscar en el document cuando lo encuentra
                    }
                }
            }
        }
        //  if (commandSet.size() > 0) commandStack.push(commandSet);
        for (URI uri : deletedDoc) {
            Document doc = bTree.get(uri);
            Map<URI, vaina> vainaMapTemp = vainaMap;
            // Esta en disk
            if (!vainaMap.containsKey(uri)) {
                borradoDeDisk.add(uri);
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    try {
                        bTree.moveToDisk(uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }));
            } else { // Esta en memory
                long docBytes = calcBytes(doc);
                totalCount--;
                totalByte -= docBytes;
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    vainaMap.put(uri, new vaina(uri));
                    //  inMemory(doc);
                }));
            }
            removeFromTrie(uri);
            if (vainaMap.containsKey(uri)) removeFromHeap(doc);
            bTree.put(uri, null);
            try {
                pm.delete(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
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
        Set<URI> documentsToDelete = trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> aaa = new HashSet<>();

        // Remove documents from the documentMap and Trie
        for (URI uri : documentsToDelete) {
            Document doc = bTree.get(uri);
            Map<URI, vaina> vainaMapTemp = vainaMap;
            // Esta en disk
            if (!vainaMap.containsKey(uri)) {
                borradoDeDisk.add(uri);
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    try {
                        bTree.moveToDisk(uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }));
            } else { // Esta en memory
                long docBytes = calcBytes(doc);
                totalCount--;
                totalByte -= docBytes;
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    vainaMap.put(uri, new vaina(uri));
                    //  inMemory(doc);
                }));
            }

            deletedDoc.add(uri);
            removeFromTrie(uri);
            if (vainaMap.containsKey(uri)) removeFromHeap(doc);
            bTree.put(uri, null);
            try {
                pm.delete(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        return deletedDoc;
    }

    /**
     * @param keysValues metadata key-value pairs to search for
     * @return a List of all documents whose metadata contains ALL OF the given values for the given keys. If no documents contain all the given key-value pairs, return an empty list.
     */


    @Override
    public List<Document> searchByMetadata(Map<String, String> keysValues) throws IOException {
        if (keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        List<Document> matchingDocs = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (Document document : getURI()) {
            HashMap<String, String> metadata = document.getMetadata();
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
        matchingDocs.addAll(diskMeta(keysValues));
        return matchingDocs;
    }

    private List<Document> diskMeta(Map<String, String> keysValues) throws IOException {
        if (keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        List<Document> matchingDocs = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (URI uri : docDisk) {
            Document doc = bTree.get(uri);
            HashMap<String, String> metadata = doc.getMetadata();
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
                inMemory(doc);
                vaina v = vainaMap.get(uri);
                docDisk.remove(uri);
                v = vainaMap.get(uri);
                v.setisonheap(true);
                triePut(uri);
                //update last uded time
                doc.setLastUseTime(currentTime);
                matchingDocs.add(doc);
            } else {
                bTree.moveToDisk(uri);
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
    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) throws IOException {
        List<Document> matches = new ArrayList<>();
        long currentTime = System.nanoTime();
        for (Document document : getURIOtro()) {
            if (document.getDocumentTxt().contains(keyword)) {
                HashMap<String, String> metadata = document.getMetadata();
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
        matches.addAll(diskKeyWordAndMetadata(keyword, keysValues));
        matches.sort(Comparator.comparingInt(doc -> doc.wordCount(keyword)));
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
    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException {
        if (keywordPrefix.isEmpty() || keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        long currentTime = System.nanoTime();
        List<Document> matches = new ArrayList<>();
        for (Document document : getURI()) {
            if (document.getDocumentTxt().contains(keywordPrefix)) {
                HashMap<String, String> metadata = document.getMetadata();
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
                    //wop revisar
                    heap.reHeapify(vainaMap.get(document.getKey()));
                    matches.add(document);
                }
            }
        }
        matches.addAll(diskPrefixAndMetadata(keywordPrefix, keysValues));
        matches.sort(Comparator.comparingInt(doc -> /*-*/doc.wordCount(keywordPrefix)));
        return matches;
    }

    private List<Document> diskPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException {
        if (keywordPrefix.isEmpty() || keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        long currentTime = System.nanoTime();
        List<Document> matches = new ArrayList<>();
        for (URI uri : docDisk) {
            Document document = bTree.get(uri);
            if (document.getDocumentTxt().contains(keywordPrefix)) {
                HashMap<String, String> metadata = document.getMetadata();
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
                    inMemory(document);
                    vaina v = vainaMap.get(uri);
                    docDisk.remove(uri);
                    v = vainaMap.get(uri);
                    v.setisonheap(true);
                    triePut(uri);
                    //update last uded time
                    document.setLastUseTime(currentTime);
                    matches.add(document);
                } else {
                    bTree.moveToDisk(uri);
                }
            }
        }
        return matches;
    }
    private List<Document> diskKeyWordAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException {
        if (keywordPrefix.isEmpty() || keysValues.isEmpty()) {
            return Collections.emptyList();
        }
        long currentTime = System.nanoTime();
        List<Document> matches = new ArrayList<>();
        for (URI uri : docDisk) {
            Document document = bTree.get(uri);
            if (document.getDocumentTxt().contains(keywordPrefix)) {
                HashMap<String, String> metadata = document.getMetadata();
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
                    inMemory(document);
                    vaina v = vainaMap.get(uri);
                    docDisk.remove(uri);
                    v = vainaMap.get(uri);
                    v.setisonheap(true);
                    triePut(uri);
                    //update last uded time
                    document.setLastUseTime(currentTime);
                    matches.add(document);
                } else {
                    bTree.moveToDisk(uri);
                }
            }
        }
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
    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) throws IOException{
        Set<URI> deletedDocs = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        for (Document document : getURIOtro()) {
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
                }
            }
        }

        for (URI uri : deletedDocs) {
            Document doc = bTree.get(uri);
            Map<URI, vaina> vainaMapTemp = vainaMap;
            // Esta en disk
            if (!vainaMap.containsKey(uri)){
                borradoDeDisk.add(uri);
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    try {
                        bTree.moveToDisk(uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }));
            } else { // Esta en memory
                long docBytes = calcBytes(doc);
                totalCount--;
                totalByte -= docBytes;
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    vainaMap.put(uri, new vaina(uri));
                    //  inMemory(doc);
                }));
            }
            removeFromTrie(uri);
            if (vainaMap.containsKey(uri)) removeFromHeap(doc);
            bTree.put(uri, null);
            try {
                pm.delete(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) throws IOException{
        Set<URI> deletedDoc = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        for (Document document : getURIOtro()) {
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

                    /*
                    bTree.put(document.getKey(), null);
                    for (String word : words) {
                        trie.deleteAll(word);
                    }
                    commandSet.addCommand(new GenericCommand<>(document.getKey(), targetUri -> {
                        this.bTree.put(targetUri, document);
                        triePut(targetUri);
                    }));

                     */
                }
            }
        }

        for (URI uri : deletedDoc) {
            Document doc = bTree.get(uri);
            Map<URI, vaina> vainaMapTemp = vainaMap;
            // Esta en disk
            if (!vainaMap.containsKey(uri)){
                borradoDeDisk.add(uri);
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    try {
                        bTree.moveToDisk(uri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }));
            } else { // Esta en memory
                long docBytes = calcBytes(doc);
                totalCount--;
                totalByte -= docBytes;
                commandSet.addCommand(new GenericCommand<>(uri, targetUri -> {
                    this.bTree.put(targetUri, doc);
                    triePut(uri);
                    vainaMap.put(uri, new vaina(uri));
                    //  inMemory(doc);
                }));
            }

            deletedDoc.add(uri);
            removeFromTrie(uri);
            if (vainaMap.containsKey(uri)) removeFromHeap(doc);
            bTree.put(uri, null);
            try {
                pm.delete(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException{
        Set<URI> deletedDoc = new HashSet<>();
        CommandSet<URI> commandSet = new CommandSet<>();
        for (Document document : getURI()) {
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
                bTree.put(document.getKey(), null);
                for (String word : words) {
                    trie.deleteAll(word);
                    try {
                        pm.delete(document.getKey());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                commandSet.addCommand(new GenericCommand<>(document.getKey(), targetUri -> {
                    this.bTree.put(targetUri, document);
                    triePut(targetUri);
                }));
            }
        }
        if (commandSet.size() > 0) commandStack.push(commandSet);
        return deletedDoc;
    }

    private List<Document> getURI(){
        List<Document> listaDoc = new ArrayList<>();
        for (URI uri : vainaMap.keySet()){
           Document doc = bTree.get(uri);
            if (doc != null) {
                listaDoc.add(doc);
            }
        }
        return listaDoc;
    }

    private List<Document> getURIOtro(){
        List<Document> listaDoc = new ArrayList<>();
        for (URI uri : lista){
            Document doc = bTree.get(uri);
            if (doc != null) {
                listaDoc.add(doc);
            }
        }
        return listaDoc;
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
    private vaina getOldestDoc() {
       return heap.peek();
    }

    private void memoryLimitCount() {
        if (maxCount < 0){
            return;
        }

        if (totalCount > maxCount) justDisk.clear();
        while (totalCount > maxCount){
            borrarDocumentosViejo();
        }
    }
    private void memoryByteCount() {
        if (maxByte < 0) {
            return;
        }
        if (calcTotalBytes() > maxByte) justDisk.clear();
        while (totalByte > maxByte){
            borrarDocumentosViejo();
        }
    }
/*
   private void undoMoveToDisk(URI uri) {
       Document document = bTree.get(uri);
       if (document != null) {
           inMemory(document);
           heap.insert(vainaMap.get(uri));
           vaina v = vainaMap.get(uri);
           v.setisonheap(true);
           triePut(uri);
       }
   }
    private void undoRemainInMemory(URI uri) {
        try {
            Document document = bTree.get(uri);
            if (document != null) {
                removeFromHeap(document);
                bTree.moveToDisk(uri);
                docDisk.add(uri);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 */
    private void borarDoc(Document doc) throws IOException {
      //  diskAhora.add(doc);
        long docBytes = calcBytes(doc);
        totalCount--;
        totalByte -= docBytes;
        removeFromHeap(doc);
        try {
            bTree.moveToDisk(doc.getKey());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        docDisk.add(doc.getKey());
    }

    private void borrarDocumentosViejo() {
        vaina oldestDocument = getOldestDoc();
        if (oldestDocument == null) {
            return;
        }
        try {
            Document doc = bTree.get(oldestDocument.getKey());
            undoConEsteURI.add(doc.getKey());
            justDisk.add(doc.getKey());
            diskAhora.add(doc);
            long docBytes = calcBytes(doc);
            totalCount--;
            totalByte -= docBytes;
            removeFromHeap(doc);
            //removeDocCommand(oldestDocument);
            bTree.moveToDisk(oldestDocument.getKey());
            docDisk.add(oldestDocument.getKey());
            /*
            boolean wasOnDisk = docDisk.contains(oldestDocument.getKey());
            boolean movedNow = diskAhora.contains(oldestDocument.getKey());
            bTree.moveToDisk(oldestDocument.getKey());
            docDisk.add(oldestDocument.getKey());
            if (movedNow) {
                inMemory(doc);
                vaina v = vainaMap.get(doc.getKey());
                triePut(oldestDocument.getKey());
                v.setisonheap(true);
                pm.deserialize(doc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
             */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//            diskAhora.clear();
//            diskAhora.add(doc);

            /*
            URI uri = oldestDocument.getKey();
            Document movedDoc = doc;

            commandStack.push(new GenericCommand<>(uri, undoUri -> {
                try {
                    // Move document back into memory
                    Document back = bTree.get(undoUri);
                    if (back == null) {
                        return;
                    }
                    inMemory(back);
                    heap.insert(new vaina(undoUri));
                    vaina v = vainaMap.get(back.getKey());
                    v.setisonheap(true);
                    triePut(undoUri);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
             */

    private long calcTotalBytes() {
        long totalBytes = 0;
        for (Document document : getURI()) {
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

    private void removeDocCommand(vaina document) {
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
    private class vaina implements Comparable<vaina> {
        URI uri;
        private boolean enHeap;
        //long lastUsedTime;

        public vaina(URI uri){
            this.uri = uri;
            enHeap = false;
            //this.lastUsedTime = System.nanoTime();
        }
        public boolean isonHeap() {
            return enHeap;
        }
        private void setisonheap(boolean b){
            enHeap = b;
        }

        @Override
        public int compareTo(vaina o) {
            Document doc = bTree.get(this.uri);
            Document otherDoc = bTree.get(o.uri);
            if (o == null) {
                throw new NullPointerException();
            }
            return Long.compare(doc.getLastUseTime(), otherDoc.getLastUseTime());
        }
        @Override
        public int hashCode() {
            return uri.hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            vaina doc = (vaina) obj;
            return uri.equals(doc.uri);
        }
        public URI getKey(){
            return uri;
        }
    }
}