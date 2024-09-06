package edu.yu.cs.com1320.project.stage4;


import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.TXT;
import static org.junit.Assert.*;

public class stage4test {
    URI uri = new URI("nessim.com");
    URI url = new URI("nessim");

    String str = ("Matame, yo feliz");
    InputStream inputStream = new ByteArrayInputStream(str.getBytes());
    InputStream is = new ByteArrayInputStream(str.getBytes());

    public stage4test() throws URISyntaxException {
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

        List<Integer> result = trie.getAllWithPrefixSorted("he", Comparator.naturalOrder());
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

    List<Document> lista = new ArrayList<>();

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


        store.deleteAll("Nessim");
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
        store.undo(uri2);
        lista = store.searchByPrefix("Al");
        assertEquals(1, lista.size());
        store.undo(uri4);
        lista = store.searchByPrefix("Al");
        assertEquals(0, lista.size());
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

}