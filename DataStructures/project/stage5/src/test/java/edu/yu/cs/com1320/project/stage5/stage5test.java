package edu.yu.cs.com1320.project.stage5;


import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.undo.CommandSet;
import edu.yu.cs.com1320.project.undo.GenericCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat.TXT;
import static org.junit.Assert.*;

public class stage5test {
    URI uri = new URI("nessim.com");
    URI url = new URI("nessim");


    String str = ("Matame, yo feliz");
    InputStream inputStream = new ByteArrayInputStream(str.getBytes());
    InputStream is = new ByteArrayInputStream(str.getBytes());

    public stage5test() throws URISyntaxException {
    }

    //Trie test
    @Test
    public void testPutAndGetTrie() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        List<Integer> result = trie.getSorted("hello", Comparator.naturalOrder());
        assertEquals(1, result.size());
        assertEquals(1, (int) result.get(0));
    }

    @Test
    public void testGetWithAllPrefixSorted() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        trie.put("help", 2);
        trie.put("hell", 3);
        List<Integer> result = trie.getAllWithPrefixSorted("hel", Comparator.naturalOrder());
        assertEquals(3, result.size()); // Expecting 3 because "hel" prefix matches all keys
    }

    @Test
    public void testGetSorted() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        trie.put("help", 2);
        trie.put("hell", 3);
        List<Integer> result = trie.getAllWithPrefixSorted("hel", Comparator.naturalOrder());

        // Assert size
        assertEquals(3, result.size());

        // Assert contents
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));
        assertTrue(result.contains(3));
    }

    @Test
    public void testPutAndGet2() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        List<Integer> result = trie.getSorted("hello", Comparator.naturalOrder());
        assertEquals(1, result.size());
        assertEquals(1, (int) result.get(0));
    }

    @Test
    public void testGetWithPrefixSorted() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        trie.put("help", 2);
        trie.put("he", 3);

        List<Integer> result = trie.getAllWithPrefixSorted("he", Comparator.reverseOrder()); //Comparator.naturalOrder()
        assertEquals(3, result.size());
        assertEquals(3, (int) result.get(0));
        assertEquals(2, (int) result.get(1));
        assertEquals(1, (int) result.get(2));
    }

    @Test
    public void testGetSorted2() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        trie.put("help", 2);
        trie.put("he", 3);

        List<Integer> result = trie.getSorted("hello", Comparator.naturalOrder());
        assertEquals(1, result.size());
        assertEquals(1, (int) result.get(0));
    }

    @Test
    public void deleteAllWithPrefix() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("Aliza", 2);
        trie.put("Ali", 3);
        trie.put("Aligator", 4);
        trie.put("ali", 6);
        trie.put("nessim", 5);
        Set<Integer> list = new HashSet<>();
        list.add(4);
        list.add(3);
        list.add(2);
        assertEquals(list, trie.deleteAllWithPrefix("Ali"));
    }

    @Test
    public void putUndo() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri1 = new URI("uri1");
        URI uri2 = new URI("uri2");

        String string1 = "nes nessim nessun";
        String string2 = "ali aliza";

        InputStream is1 = new ByteArrayInputStream(string1.getBytes());
        InputStream is2 = new ByteArrayInputStream(string2.getBytes());

        store.put(is1, uri1, DocumentStore.DocumentFormat.TXT);
        store.put(is2, uri2, DocumentStore.DocumentFormat.TXT);

        List<Document> lista;

        store.delete(uri2);
        lista = store.searchByPrefix("al");
        assertEquals(0, lista.size());

        // New strings
        String string3 = "nessun Mati";
        String string4 = "nes David";

        InputStream is3 = new ByteArrayInputStream(string3.getBytes());
        InputStream is4 = new ByteArrayInputStream(string4.getBytes());

        URI uri3 = new URI("uri3");
        URI uri4 = new URI("uri4");

        // Put new documents
        store.put(is3, uri3, DocumentStore.DocumentFormat.TXT);
        store.put(is4, uri4, DocumentStore.DocumentFormat.TXT);

        // Verify search results after new puts
        lista = store.searchByPrefix("al");
        assertEquals(0, lista.size()); // "al" prefix should still yield 0 documents

        // Undo operation
        store.undo();

        // Verify search results after undo
        lista = store.searchByPrefix("al");
        assertEquals(0, lista.size()); // After undo, "al" prefix should still yield 0 documents
    }


    @Test
    public void trieDeletes() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri1 = new URI("uri1");
        URI uri2 = new URI("uri2");
        URI uri3 = new URI("uri3");
        URI uri4 = new URI("uri4");

        String string1 = "Nessim Nessim nessim Nosim Nosim Juan";
        String string2 = "Ali Ali Ali Aliza Aliza iza Nessim";
        String string3 = "juan Aliza pedro";
        String string4 = "carro Al";

        InputStream is1 = new ByteArrayInputStream(string1.getBytes());
        InputStream is2 = new ByteArrayInputStream(string2.getBytes());
        InputStream is3 = new ByteArrayInputStream(string3.getBytes());
        InputStream is4 = new ByteArrayInputStream(string4.getBytes());

        store.put(is1, uri1, TXT);
        store.put(is2, uri2, TXT);
        store.put(is3, uri3, TXT);
        store.put(is4, uri4, TXT);

        List<Document> lista = new ArrayList<>();


        assertEquals(2, store.deleteAll("Nessim").size());
        lista = store.searchByPrefix("Al");
        assertEquals(2, lista.size());
        store.undo();
        lista = store.searchByPrefix("Al");
        assertEquals(3, lista.size());
        store.deleteAll("Aliza");
        lista = store.searchByPrefix("Al");
        assertEquals(1, lista.size());
        store.undo();
        lista = store.searchByPrefix("Al");
        assertEquals(3, lista.size());

        store.deleteAllWithPrefix("Ne");
        lista = store.searchByPrefix("Al");
        assertEquals(2, lista.size());
        store.undo();
        lista = store.searchByPrefix("Al");
        assertEquals(3, lista.size());
        store.deleteAllWithPrefix("Al");
        lista = store.searchByPrefix("Al");
        assertEquals(0, lista.size());
        store.undo();
        lista = store.searchByPrefix("Al");
        assertEquals(3, lista.size());

        store.deleteAll("Aliza");
        lista = store.searchByPrefix("Al");
        assertEquals(1, lista.size());
        store.undo(uri2);
        lista = store.searchByPrefix("Al");
        assertEquals(2, lista.size());
        /*
        store.undo(uri2);
        lista = store.searchByPrefix("Al");
        assertEquals(1, lista.size());
        store.undo(uri4);
        lista = store.searchByPrefix("Al");
        assertEquals(0, lista.size());
         */
    }


    @Test
    public void testDeleteAll() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        trie.put("help", 2);
        trie.put("he", 3);

        Set<Integer> deletedValues = trie.deleteAll("hello");
        assertEquals(1, deletedValues.size());
        assertTrue(deletedValues.contains(1));

        assertEquals(0, trie.get("hello").size());
    }

    @Test
    public void testDelete() {
        TrieImpl<Integer> trie = new TrieImpl<>();
        trie.put("hello", 1);
        trie.put("help", 2);
        trie.put("he", 3);

        assertEquals(Integer.valueOf(1), trie.delete("hello", 1));
        assertNull(trie.delete("hello", 1));
    }

    //hashtableimpl test
    @Test
    public void PutAndGet() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3);

        assertEquals(1, (int) hashTable.get("key1"));
        assertEquals(2, (int) hashTable.get("key2"));
        assertEquals(3, (int) hashTable.get("key3"));
    }

    @Test
    public void PutAndGet2() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();
        // Force collisions by inserting keys with the same hash
        hashTable.put("nessim", 1);
        hashTable.put("ilan", 2);
        hashTable.put("bejman", 3);
        hashTable.put("loloey", 4);

        assertEquals(1, (int) hashTable.get("nessim"));
        assertEquals(2, (int) hashTable.get("ilan"));
        assertEquals(3, (int) hashTable.get("bejman"));
        assertEquals(4, (int) hashTable.get("loloey"));
    }

    @Test
    public void PutAndGetNull() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();

        hashTable.put("key1", 1);
        hashTable.get("key1");

        assertEquals(1, (int) hashTable.get("key1"));
        hashTable.put("key1", null);
        hashTable.get("key1");

        assertNull(hashTable.get("key1"));

    }

    @Test
    public void ContainsKey() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();
        hashTable.put("key1", 1);

        assertTrue(hashTable.containsKey("key1"));
        assertFalse(hashTable.containsKey("key2"));
    }

    @Test
    public void Size() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();

        //empieza
        System.out.println("Initial size: " + hashTable.size());
        assertEquals(0, hashTable.size());

        // Put
        hashTable.put("key1", 1);
        System.out.println("Size after putting \"key1\": " + hashTable.size());
        assertEquals(1, hashTable.size());

        hashTable.put("key2", 2);
        System.out.println("Size after putting \"key2\": " + hashTable.size());
        assertEquals(2, hashTable.size());

        hashTable.put("key3", 3);
        System.out.println("Size after putting \"key3\": " + hashTable.size());
        assertEquals(3, hashTable.size());

        hashTable.put("key3", null); // Replace existing value
        System.out.println("Size after putting \"key3\" with null: " + hashTable.size());
        assertEquals(2, hashTable.size());

        hashTable.put("key4", 4);
        System.out.println("Size after putting \"key4\": " + hashTable.size());
        assertEquals(3, hashTable.size());

        hashTable.put("key5", 5);
        System.out.println("Size after putting \"key5\": " + hashTable.size());
        assertEquals(4, hashTable.size());

        hashTable.put("key6", 6);
        System.out.println("Size after putting \"key6\": " + hashTable.size());
        assertEquals(5, hashTable.size());
    }

    @Test
    public void KeySet() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3);

        assertTrue(hashTable.keySet().contains("key1"));
        assertTrue(hashTable.keySet().contains("key2"));
        assertTrue(hashTable.keySet().contains("key3"));
    }

    @Test
    public void Values() {
        HashTableImpl<String, Integer> hashTable = new HashTableImpl<>();
        hashTable.put("key1", 1);
        hashTable.put("key2", 2);
        hashTable.put("key3", 3);

        assertTrue(hashTable.values().contains(1));
        assertTrue(hashTable.values().contains(2));
        assertTrue(hashTable.values().contains(3));
    }

    @Test
    public void ResizeArray() {
        HashTableImpl<Integer, String> hashTable = new HashTableImpl<>();

        for (int i = 0; i < 6; i++) {
            hashTable.put(i, "Value" + i);
        }
        for (int i = 0; i < 6; i++) {
            assertEquals("Value" + i, hashTable.get(i));
        }
        assertEquals(6, hashTable.size());
    }

    //stack test
    @Test
    public void PushAndPop() {
        StackImpl<String> stack = new StackImpl<>();
        stack.push("1");
        assertEquals(1, stack.size());

        stack.push("2");
        assertEquals(2, stack.size());

        String poppedElement = stack.pop();
        assertEquals("2", poppedElement);
        assertEquals(1, stack.size());

        poppedElement = stack.pop();
        assertEquals("1", poppedElement);
        assertEquals(0, stack.size());
    }

    @Test
    public void PushAndPeek() {
        StackImpl<String> stack = new StackImpl<>();
        stack.push("1");
        assertEquals(1, stack.size());
        assertEquals("1", stack.peek());
        assertEquals(1, stack.size());

        stack.push("2");
        assertEquals(2, stack.size());
        assertEquals("2", stack.peek());
        assertEquals(2, stack.size());
    }

    @Test
    public void PopEmptyStack() {
        StackImpl<String> stack = new StackImpl<>();
        assertNull(stack.pop());
    }

    @Test
    public void PeekEmptyStack() {
        StackImpl<String> stack = new StackImpl<>();
        assertNull(stack.peek());
    }

    @Test
    public void SizeEmptyStack() {
        StackImpl<String> stack = new StackImpl<>();
        assertEquals(0, stack.size());
    }

    // document test
    @Test
    public void testCreateTextDocument() {
        URI uri = URI.create("http://example.com/document");
        String textContent = "This is a test document.";
        Document document = new DocumentImpl(uri, textContent);

        assertNotNull(document);
        assertEquals(uri, document.getKey());
        assertEquals(textContent, document.getDocumentTxt());
    }

    @Test
    public void testCreateBinaryDocument() {
        URI uri = URI.create("http://example.com/document");
        byte[] binaryData = {0x12, 0x34, 0x56, 0x78};
        Document document = new DocumentImpl(uri, binaryData);

        assertNotNull(document);
        assertEquals(uri, document.getKey());
        assertEquals(binaryData, document.getDocumentBinaryData());
    }

    @Test
    public void testSetAndGetMetadata() {
        URI uri = URI.create("http://example.com/document");
        Document document = new DocumentImpl(uri, "Test document");

        String key = "author";
        String value = "John Doe";
        document.setMetadataValue(key, value);

        assertEquals(value, document.getMetadataValue(key));
    }

    @Test
    public void testWordCount() {
        URI uri = URI.create("http://example.com/document");
        String textContent = "Nessim wurmann 1 don't. Dont Don't leches cachai, pap 1. the The tHe";
        Document document = new DocumentImpl(uri, textContent);

        assertEquals(2, document.wordCount("1"));
        assertEquals(1, document.wordCount("dont"));
        assertEquals(2, document.wordCount("Dont"));
        assertEquals(1, document.wordCount("the"));
        assertEquals(1, document.wordCount("The"));
        assertEquals(1, document.wordCount("tHe"));
    }

    @Test
    public void testGetWords() {
        // Create a Document with some text
        URI uri = URI.create("http://example.com/document");
        String textContent = "DoNt leche bejman wurmann pap. broder cachai 1 basement, don't pap.";
        Document document = new DocumentImpl(uri, textContent);

        // Get the words from the document
        Set<String> words = document.getWords();

        // Check that the correct number of words is returned
        assertEquals(10, words.size());

        // Check that specific words are present in the set
        assertTrue(words.contains("DoNt"));
        assertTrue(words.contains("leche"));
        assertTrue(words.contains("bejman"));
        assertTrue(words.contains("wurmann"));
        assertTrue(words.contains("pap"));
        assertTrue(words.contains("broder"));
        assertTrue(words.contains("cachai"));
        assertTrue(words.contains("1"));
        assertTrue(words.contains("basement"));
        assertTrue(words.contains("dont"));
    }


    //document store test
    private DocumentStoreImpl documentStore = new DocumentStoreImpl();
    private URI uri1 = URI.create("http://www.yu.edu/documents/doc1");

    @Test
    public void testPutAndGet() throws IOException {
        String content = "This is the content of the document";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());

        // When
        int hashCode = documentStore.put(inputStream, uri1, TXT);
        Document document = documentStore.get(uri1);

        // Then
        assertNotNull(document);
        assertEquals(content, document.getDocumentTxt());
    }

    @Test
    public void DeleteAndGet() throws IOException {
        // si
        String content = "This is the content of the document";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        documentStore.put(inputStream, uri1, TXT);

        // cuando
        assertTrue(documentStore.delete(uri1));

        // entonces
        assertNull(documentStore.get(uri1));
    }

    @Test
    public void Undo() throws IOException {
        String content = "This is the content of the document";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        documentStore.put(inputStream, uri1, TXT);
        assertTrue(documentStore.delete(uri1));

        documentStore.undo();

        assertNotNull(documentStore.get(uri1));
    }

    @Test(expected = IllegalStateException.class)
    public void UndoWithEmptyStackForURI() {

        documentStore.undo(uri1);
    }

    private DocumentStoreImpl dsi;

    @Before
    public void setUp() throws Exception {
        dsi = new DocumentStoreImpl();
        uri = new URI("http://www.example.com/document1");
    }

    @Test
    public void SetAndGetMetadata() throws IOException {
        // si
        String content = "sample document.";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        dsi.put(inputStream, uri, TXT);

        // cuando
        String oldValue = dsi.setMetadata(uri, "pap", "nessim");
        String retrievedValue = dsi.getMetadata(uri, "pap");
        String nonExistentValue = dsi.getMetadata(uri, "no existe");

        // enconces
        assertNull("metadata viejo deberia ser null", oldValue);
        assertEquals("metadatavalue deberia ser 'nessim'", "nessim", retrievedValue);
        assertNull("no existe value deberia ser null", nonExistentValue);
    }

    @Test
    public void SetMetadataAndGetOldValue() throws IOException, URISyntaxException {
        DocumentStore store = new DocumentStoreImpl();
        URI uri = new URI("http://www.example.com/document1");
        String txt = "test doc";
        String key = "name";
        String value = "benitocamela";

        store.put(new ByteArrayInputStream(txt.getBytes()), uri, TXT);
        String oldValue = store.setMetadata(uri, key, value);

        assertNull(oldValue);
        String retrievedValue = store.getMetadata(uri, key);
        assertEquals(value, retrievedValue);
    }

    @Test
    public void undoWhenEmptyShouldThrow() {
        DocumentStoreImpl documentStore = new DocumentStoreImpl();
        assertThrows(IllegalStateException.class, () -> documentStore.undo());
    }

    @Test
    public void undoAfterMultiplePuts() {
        DocumentStoreImpl documentStore = new DocumentStoreImpl();
        URI uri1 = URI.create("http://example.com/document1");
        URI uri2 = URI.create("http://example.com/document2");
        URI uri3 = URI.create("http://example.com/document3");
        URI uri4 = URI.create("http://example.com/document4");

        try {
            // Perform 4 puts
            documentStore.put(new ByteArrayInputStream("Document 1".getBytes()), uri1, TXT);
            documentStore.put(new ByteArrayInputStream("Document 2".getBytes()), uri2, TXT);
            documentStore.put(new ByteArrayInputStream("Document 3".getBytes()), uri3, TXT);
            documentStore.put(new ByteArrayInputStream("Document 4".getBytes()), uri4, TXT);

            // Undo each put in reverse order
            documentStore.undo(uri4);
            documentStore.undo(uri3);
            documentStore.undo(uri2);
            documentStore.undo(uri1);

            // Verify that the documents are not present after undoing
            assertNull(documentStore.get(uri1));
            assertNull(documentStore.get(uri2));
            assertNull(documentStore.get(uri3));
            assertNull(documentStore.get(uri4));

        } catch (IOException e) {
            fail("IOException should not be thrown during puts.");
        }

        // Undo when there are no actions to undo should throw an exception
        assertThrows(IllegalStateException.class, () -> documentStore.undo());
    }

    @Test
    public void undoAfterOnePut() {
        DocumentStoreImpl documentStore = new DocumentStoreImpl();
        URI uri = URI.create("http://example.com/document1");

        try {
            // Perform a single put
            documentStore.put(new ByteArrayInputStream("Document 1".getBytes()), uri, TXT);

            // Undo the put
            documentStore.undo();

            // Verify that the document is not present after undoing
            assertNull(documentStore.get(uri));

        } catch (IOException e) {
            fail("IOException should not be thrown during put.");
        }

        // Undo when there are no actions to undo should throw an exception
        assertThrows(IllegalStateException.class, () -> documentStore.undo());
    }

    @Test
    public void search1() throws IOException, URISyntaxException {
        String str = "Mami leches me encantas";
        String juan = "leches broder";
        InputStream inputStream = new ByteArrayInputStream(str.getBytes());
        InputStream is = new ByteArrayInputStream(juan.getBytes());
        URI uri = new URI("leches");
        URI url = new URI("broder"); // Adjusted URI for consistency
        dsi.put(inputStream, uri, TXT);
        dsi.put(is, url, TXT);
        List<Document> expectedDocuments = new ArrayList<>();
        expectedDocuments.add(dsi.get(uri));
        expectedDocuments.add(dsi.get(url));
        assertEquals(expectedDocuments, dsi.search("leches"));
    }

    @Test
    public void testWordCountForBinaryDocument() {
        // Create a binary document
        URI uri = URI.create("http://www.example.com/doc2");
        byte[] binaryData = {0x01, 0x02, 0x03, 0x04, 0x05};
        Document document = new DocumentImpl(uri, binaryData);

        // Test the word count functionality for binary document
        assertEquals(0, document.wordCount("test")); // Binary document, so word count should be 0
    }

    @Test
    public void undoBinaryAfterMultiplePuts() {
        DocumentStoreImpl documentStore = new DocumentStoreImpl();
        URI uri1 = URI.create("http://example.com/document1");
        URI uri2 = URI.create("http://example.com/document2");
        URI uri3 = URI.create("http://example.com/document3");
        URI uri4 = URI.create("http://example.com/document4");

        try {
            // Perform 4 puts with binary data
            documentStore.put(new ByteArrayInputStream(new byte[]{1, 2, 3}), uri1, DocumentStore.DocumentFormat.BINARY);
            documentStore.put(new ByteArrayInputStream(new byte[]{4, 5, 6}), uri2, DocumentStore.DocumentFormat.BINARY);
            documentStore.put(new ByteArrayInputStream(new byte[]{7, 8, 9}), uri3, DocumentStore.DocumentFormat.BINARY);
            documentStore.put(new ByteArrayInputStream(new byte[]{10, 11, 12}), uri4, DocumentStore.DocumentFormat.BINARY);
            // Undo each put in reverse order
            documentStore.undo(uri4);
            documentStore.undo(uri3);
            documentStore.undo(uri2);
            documentStore.undo(uri1);

            // Verify that the documents are not present after undoing
            assertNull(documentStore.get(uri1));
            assertNull(documentStore.get(uri2));
            assertNull(documentStore.get(uri3));
            assertNull(documentStore.get(uri4));

        } catch (IOException e) {
            fail("IOException should not be thrown during puts.");
        }

        // Undo when there are no actions to undo should throw an exception
        assertThrows(IllegalStateException.class, () -> documentStore.undo());
    }

    @Test
    public void stage4DeleteAllBinary() throws IOException {
        // Create a document store
        DocumentStore documentStore = new DocumentStoreImpl();

        // Create InputStreams for text-based documents
        InputStream textStream1 = new ByteArrayInputStream("Text document 1 content".getBytes());
        InputStream textStream2 = new ByteArrayInputStream("Text document 2 content".getBytes());

        // Create InputStreams for binary-based documents
        InputStream binaryStream1 = new ByteArrayInputStream(new byte[]{0x01, 0x02, 0x03});
        InputStream binaryStream2 = new ByteArrayInputStream(new byte[]{0x04, 0x05, 0x06});

        // Add text-based documents
        URI textUri1 = URI.create("text://document1");
        documentStore.put(textStream1, textUri1, DocumentStore.DocumentFormat.TXT);
        URI textUri2 = URI.create("text://document2");
        documentStore.put(textStream2, textUri2, DocumentStore.DocumentFormat.TXT);

        // Add binary-based documents
        URI binaryUri1 = URI.create("binary://document3");
        documentStore.put(binaryStream1, binaryUri1, DocumentStore.DocumentFormat.BINARY);
        URI binaryUri2 = URI.create("binary://document4");
        documentStore.put(binaryStream2, binaryUri2, DocumentStore.DocumentFormat.BINARY);

        // Delete all documents containing the keyword "document"
        Set<URI> deletedDocuments = documentStore.deleteAll("document");

        // Verify that the text-based documents containing the keyword have been deleted
        assertTrue(deletedDocuments.contains(textUri1));
        assertTrue(deletedDocuments.contains(textUri2));

        // Verify that the binary-based documents containing the keyword have not been deleted
        assertFalse(deletedDocuments.contains(binaryUri1));
        assertFalse(deletedDocuments.contains(binaryUri2));

        // Verify that the other documents remain unaffected
        assertNull(documentStore.get(textUri1));
        assertNull(documentStore.get(textUri2));
        assertNotNull(documentStore.get(binaryUri1));
        assertNotNull(documentStore.get(binaryUri2));
    }

    @Test
    public void stage4UndoByURIThatImpactsOne() throws IOException {
        DocumentStoreImpl documentStore = new DocumentStoreImpl();

        InputStream textStream1 = new ByteArrayInputStream("Text document 1 leches".getBytes());
        InputStream textStream2 = new ByteArrayInputStream("Text document 2 leches".getBytes());
        InputStream textStream3 = new ByteArrayInputStream("Text document 3 Leches".getBytes());

        URI textUri1 = URI.create("text://documentleches");
        documentStore.put(textStream1, textUri1, DocumentStore.DocumentFormat.TXT);
        URI textUri2 = URI.create("text://documentbejman");
        documentStore.put(textStream2, textUri2, DocumentStore.DocumentFormat.TXT);
        URI textUri3 = URI.create("text://documentilan");
        documentStore.put(textStream3, textUri3, DocumentStore.DocumentFormat.TXT);

        Set<URI> deletedDocuments = documentStore.deleteAll("leches");
        assertEquals(2, deletedDocuments.size());
        assertTrue(deletedDocuments.contains(textUri2));
        //assertTrue(deletedDocuments.contains(textUri2));
        //assertTrue(deletedDocuments.contains(textUri1));
        assertNotNull(documentStore.get(textUri3));
        //assertNotNull(documentStore.get(textUri1));

        documentStore.undo(textUri2);
        assertNotNull(documentStore.get(textUri2));
        //assertNull(documentStore.get(textUri1));
        //assertNull(documentStore.get(textUri2));

        Set<URI> deletedDocumentsAfterUndo = documentStore.deleteAll("leches");
        assertFalse(deletedDocumentsAfterUndo.contains(textUri3));
        assertTrue(deletedDocumentsAfterUndo.contains(textUri2));
        assertFalse(deletedDocumentsAfterUndo.contains(textUri1));
    }

    @Test
    public void stage4UndoByURIThatImpactsOneJudah() throws IOException, URISyntaxException {
        DocumentStoreImpl documentStore = new DocumentStoreImpl();

        URI uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        String txt1 = "keyword1 This is the text of doc1, in plain text. No fancy file format - just plain old String";

        URI uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        String txt2 = "keyword1 Text for doc2. A plain old String.";

        URI uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        String txt3 = "keyword123 This is the text of doc3 - doc doc goose";

        URI uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        String txt4 = "keyword12 doc4: how much wood would a woodchuck chuck...";

        documentStore.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        documentStore.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        documentStore.put(new ByteArrayInputStream(txt3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        documentStore.put(new ByteArrayInputStream(txt4.getBytes()), uri4, DocumentStore.DocumentFormat.TXT);

        Set<URI> deletedDocuments = documentStore.deleteAll("keyword1");
        assertEquals(2, deletedDocuments.size());
        assertTrue(deletedDocuments.contains(uri2));
        //assertTrue(deletedDocuments.contains(textUri2));
        assertTrue(deletedDocuments.contains(uri1));
        assertNotNull(documentStore.get(uri3));
        assertNotNull(documentStore.get(uri4));

        documentStore.undo(uri2);
        assertNotNull(documentStore.get(uri2));
        //assertNull(documentStore.get(textUri1));
        //assertNull(documentStore.get(textUri2));

        Set<URI> deletedDocumentsAfterUndo = documentStore.deleteAll("keyword1");
        assertTrue(deletedDocumentsAfterUndo.contains(uri2));
        assertFalse(deletedDocumentsAfterUndo.contains(uri3));
        assertFalse(deletedDocumentsAfterUndo.contains(uri1));
    }


    @Test
    public void overflow() throws IOException, URISyntaxException {
        URI uri1 = new URI("primero");
        String txt1 = "La leche de Nessim se fue";

        //bytes = 19
        URI uri2 = new URI("segundo");
        String txt2 = "Nessim tiene leche";

        //bytes = 28
        URI uri3 = new URI("tercero");
        String txt3 = "Nessim tiene leche";
        //bytes = 19
        URI uri4 = new URI("cuarto");
        String txt4 = "este es el cuarto";

        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        assertEquals(store.search("Nessim").size(), 3);
        store.put(new ByteArrayInputStream(txt4.getBytes()), uri4, DocumentStore.DocumentFormat.TXT);
        assertEquals(store.search("Nessim").size(), 2);
        assertThrows(IllegalStateException.class, () -> {
            store.undo(uri2);
        });
        assertEquals(store.search("leches").size(), 0);
    }

    @Test
    public void todo() throws IOException, URISyntaxException {
        //bytes = 25
        URI uri1 = new URI("first");
        String txt1 = "La leche de Nessim se fue";

        //bytes = 18
        URI uri2 = new URI("second");
        String txt2 = "Nessim tiene leche";

        //bytes = 23
        URI uri3 = new URI("third");
        String txt3 = "Porque Nessim se retira";
        //bytes = 17
        URI uri4 = new URI("fourth");
        String txt4 = "Este es el cuarto ";

        //bytes = 20
        URI uri5 = new URI("fifth");
        String txt5 = "parece ser el quinto";

        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt4.getBytes()), uri4, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt5.getBytes()), uri5, DocumentStore.DocumentFormat.TXT);
        store.delete(uri3);
        store.setMaxDocumentCount(4);
        store.search("fue");
        store.undo();
        assertNull(store.get(uri2));

    }
/*
    @Test
    public void MinHeapImplTest() throws URISyntaxException {
        URI uri1 = new URI("primero");
        URI uri2 = new URI("segundo");
        URI uri3 = new URI("tercero");
        URI uri4 = new URI("cuarto");

        String text1 = "text1";
        String text2 = "text2";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] bytes1 = {0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0};
        byte[] bytes2 = {0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 1, 0};
        DocumentImpl document3 = new DocumentImpl(uri3, bytes1);
        DocumentImpl document4 = new DocumentImpl(uri4, bytes2);

        MinHeapImpl<Document> heap = new MinHeapImpl<>();
        heap.insert(document1);
        heap.insert(document2);
        heap.insert(document3);
        heap.insert(document4);
        assertEquals(heap.getArrayIndex(document1), 1);
        document2.setLastUseTime(System.nanoTime());
        document3.setLastUseTime(System.nanoTime());
        document4.setLastUseTime(System.nanoTime());
        document1.setLastUseTime(System.nanoTime());

        heap.reHeapify(document1);
        assertEquals(4, heap.getArrayIndex(document1));
        System.out.println(document1.getLastUseTime());

        document1.setLastUseTime(0);
        heap.reHeapify(document1);
        assertEquals(heap.remove(), document1);
    }
 */

    //minheap test
    MinHeapImpl<Integer> minHeap = new MinHeapImpl<>();

    @Test
    public void testMinHeapInsert() {

        minHeap.insert(5);
        minHeap.insert(10);
        minHeap.insert(3);
        minHeap.insert(8);

        assertSame(3, minHeap.remove());
        assertSame(5, minHeap.remove());
        assertSame(8, minHeap.remove());
        assertSame(10, minHeap.remove());
    }

    @Test
    public void testMinHeapReHeapify() {
        MinHeapImpl<Integer> minHeap = new MinHeapImpl<>();
        minHeap.insert(5);
        minHeap.insert(10);
        minHeap.insert(3);
        minHeap.insert(8);

        minHeap.reHeapify(3); // Move element 3 to the top of the heap
        assertSame(3, minHeap.remove());
        assertSame(5, minHeap.remove());
        assertSame(8, minHeap.remove());
        assertSame(10, minHeap.remove());
    }

    @Test
    public void testMinHeapRemoveMin() {
        MinHeapImpl<Integer> minHeap = new MinHeapImpl<>();
        minHeap.insert(5);
        minHeap.insert(10);
        minHeap.insert(3);
        minHeap.insert(8);

        assertSame(3, minHeap.remove());
        assertSame(5, minHeap.remove());
        assertSame(8, minHeap.remove());
        assertSame(10, minHeap.remove());
    }

    //mas de document
    @Test
    public void test3CreateTextDocument() {
        URI uri = URI.create("doc");
        String text = "This is a test document.";
        Document document = new DocumentImpl(uri, text);
        assertEquals(uri, document.getKey());
        assertEquals(text, document.getDocumentTxt());
        assertNull(document.getDocumentBinaryData());
        assertEquals(0, document.wordCount("Test"));
        assertEquals(1, document.wordCount("document"));
        Set<String> expectedWords = new HashSet<>();
        expectedWords.add("This");
        expectedWords.add("is");
        expectedWords.add("a");
        expectedWords.add("test");
        expectedWords.add("document");
        assertEquals(expectedWords, document.getWords());
    }

    @Test
    public void testWordCountWithNullWord() {
        URI uri = URI.create("doc4");
        Document document = new DocumentImpl(uri, "Document");
        assertEquals(0, document.wordCount(null));
    }

    @Test
    public void testWordCountWithBinaryData() {
        URI uri = URI.create("doc");
        byte[] binaryData = {0x48, 0x65, 0x6C, 0x6C, 0x6F}; // "Hello"
        Document document = new DocumentImpl(uri, binaryData);
        assertEquals(0, document.wordCount("Hello"));
    }

    @Test
    public void testGetMetadataWithEmptyKey() {
        URI uri = URI.create("doc");
        Document document = new DocumentImpl(uri, "Text");
        assertThrows(IllegalArgumentException.class, () -> document.getMetadataValue(""));
    }

    @Test
    public void testMemoryLimits() throws IOException {
        // Create a document store
        DocumentStore store = new DocumentStoreImpl();

        // Set memory limits
        store.setMaxDocumentCount(3);
        store.setMaxDocumentBytes(38); // 15 bytes in total for simplicity

        // Add documents
        //19 bytes
        URI uri1 = URI.create("document1");
        String text1 = "This is document 1.";

        URI uri2 = URI.create("document2");
        String text2 = "This is document 2.";

        URI uri3 = URI.create("document3");
        String text3 = "This is document 3.";

        URI uri4 = URI.create("document4");
        String text4 = "This is document 4.";

        store.put(new ByteArrayInputStream(text1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(text2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(text3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(text4.getBytes()), uri4, DocumentStore.DocumentFormat.TXT);

        // Verify that uri1 is not present due to document count limit
        assertNull(store.get(uri1));

        // Verify that uri2 is not present due to document byte limit
        assertNull(store.get(uri2));

        // Verify that uri3 is present
        assertNotNull(store.get(uri3));

        // Verify that uri4 is present
        assertNotNull(store.get(uri4));
    }
/*
    @Test
    public void bigput() throws URISyntaxException, IOException {
        //bytes = 37
        URI uri1 = new URI("primero");
        String txt1 = "La leche de Nessim se fue a comer pan";

        //bytes = 67
        URI uri2 = new URI("segundo");
        String txt2 = "Nessim tiene leche y la usa para comer mucho cornflake en la semana";

        //bytes = 18
        URI uri3 = new URI("tercero");
        String txt3 = "Nessim tiene leche";
        //bytes = 17
        URI uri4 = new URI("cuarto");
        String txt4 = "este es el cuarto";

        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(139);
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(txt4.getBytes()), uri4, DocumentStore.DocumentFormat.TXT);

        store.delete(uri2);
        store.setMaxDocumentBytes(73);
        store.undo();

        assertNotNull(uri1);
        assertNull(uri2);
        assertNotNull(uri3);
        assertNotNull(uri4);
    }
*/
    @Test
    public void testPutExceedingMaxByteLimit() {
        // Create a document store
        DocumentStore store = new DocumentStoreImpl();

        // Set the maximum byte limit to 10 bytes
        store.setMaxDocumentBytes(10);

        // Define a document with text that exceeds the maximum byte limit
        String text = "This document is larger than the maximum byte limit.";

        // Define the URI for the document
        URI uri = URI.create("document1");

        assertThrows(IllegalArgumentException.class, () -> {
            store.put(new ByteArrayInputStream(text.getBytes()), uri, DocumentStore.DocumentFormat.TXT);
        });
    }
}