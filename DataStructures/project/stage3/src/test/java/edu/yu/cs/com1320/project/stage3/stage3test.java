package edu.yu.cs.com1320.project.stage3;


import edu.yu.cs.com1320.project.impl.StackImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage3.impl.DocumentStoreImpl;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class stage3test {
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

    //document store test
    private DocumentStoreImpl documentStore = new DocumentStoreImpl();
    private URI uri1 = URI.create("http://www.yu.edu/documents/doc1");
    @Test
    public void testPutAndGet() throws IOException {
       String content = "This is the content of the document";
       InputStream inputStream = new ByteArrayInputStream(content.getBytes());

       // When
       int hashCode = documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);
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
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);

        // cuando
        assertTrue(documentStore.delete(uri1));

        // entonces
        assertNull(documentStore.get(uri1));
    }

    @Test
    public void Undo() throws IOException {
        String content = "This is the content of the document";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);
        assertTrue(documentStore.delete(uri1));

        documentStore.undo();

        assertNotNull(documentStore.get(uri1));
    }

    @Test(expected = IllegalStateException.class)
    public void UndoWithEmptyStackForURI() {

        documentStore.undo(uri1);
    }
    private DocumentStoreImpl dsi;
    private URI uri;

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
        dsi.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

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

        store.put(new ByteArrayInputStream(txt.getBytes()), uri, DocumentStore.DocumentFormat.TXT);
        String oldValue = store.setMetadata(uri, key, value);

        assertNull(oldValue);
        String retrievedValue = store.getMetadata(uri, key);
        assertEquals(value, retrievedValue);
    }
}