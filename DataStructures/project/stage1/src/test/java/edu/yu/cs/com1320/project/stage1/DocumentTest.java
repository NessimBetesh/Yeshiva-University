package edu.yu.cs.com1320.project.stage1;

import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;


public class DocumentTest {
    @Test
    public void test() throws URISyntaxException {

        URI uri = new URI("edu/yu/txt.txt");
        String txt = "random";
        Document document = new DocumentImpl(uri, txt);

        assertEquals(txt, document.getDocumentTxt());
        assertEquals(uri, document.getKey());

        assertEquals(document, new DocumentImpl(uri, txt));
        assertEquals(document.hashCode(), new DocumentImpl(uri, txt).hashCode());

        document.setMetadataValue("nessim", "betesh");

        assertEquals(document.getMetadataValue("nessim"), "betesh");

    }
    @Test
    public void testConstructorWithValidURIAndText() {
        URI uri = URI.create("http://example.com/doc1");
        String text = "This is the document content";
        DocumentImpl document = new DocumentImpl(uri, text);
        assertEquals(uri, document.getKey());
        assertEquals(text, document.getDocumentTxt());
    }
    @Test
    public void testConstructorValid() {
        URI uri = URI.create("http://example.com/doc2");
        byte[] binaryData = new byte[]{1, 2, 3, 4};
        DocumentImpl document = new DocumentImpl(uri, binaryData);
        assertEquals(uri, document.getKey());
        assertArrayEquals(binaryData, document.getDocumentBinaryData());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testNullURI() {
        new DocumentImpl(null, "This should fail");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyURI() {
        new DocumentImpl(URI.create(""), "This should fail");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testNullText() {
        new DocumentImpl(URI.create("http://example.com/doc3"), (String) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testNullBinaryData() {
        new DocumentImpl(URI.create("http://example.com/doc5"), (String) null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyBinaryData() {
        new DocumentImpl(URI.create("http://example.com/doc6"), new byte[]{});
    }
    @Test
    public void testSetAndGetMetadata() {
        DocumentImpl document = new DocumentImpl(URI.create("http://example.com/doc7"), "Test content");
        String oldValue = document.setMetadataValue("key1", "value1");
        assertNull(oldValue); // No previous value
        assertEquals("value1", document.getMetadataValue("key1"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataWithNullKey() {
        DocumentImpl document = new DocumentImpl(URI.create("http://example.com/doc8"), "Test content");
        document.setMetadataValue(null, "value2");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataWithEmptyKey() {
        DocumentImpl document = new DocumentImpl(URI.create("http://example.com/doc9"), "Test content");
        document.setMetadataValue("", "value3");
    }
    @Test
    public void testGetMetadataForNonExistentKey() {
        DocumentImpl document = new DocumentImpl(URI.create("http://example.com/doc10"), "Test content");
        assertNull(document.getMetadataValue("non-existent-key"));
    }

    @Test
    public void testSetMetadata() throws IOException {
        // Create a new DocumentStore
        DocumentStoreImpl documentStore = new DocumentStoreImpl();

        // Create a new Document
        URI uri = URI.create("http://example.com/document1");
        String initialText = "This is the initial text of the document.";
        DocumentImpl document = new DocumentImpl(uri, initialText.getBytes());

        // Put the document into the DocumentStore
        documentStore.put(new ByteArrayInputStream(initialText.getBytes()), uri, DocumentStore.DocumentFormat.TXT);

        // Set metadata for the document
        String key = "author";
        String value = "John Doe";
        String oldValue = documentStore.setMetadata(uri, key, value);

        // Check that the old value is null because there was no previous metadata for the key
        assertEquals(null, oldValue);

        // Retrieve the metadata value to verify
        String newValue = documentStore.getMetadata(uri, key);
        assertEquals(value, newValue);
    }

    @Test
    public void testSetMetadataSuccessful() throws IOException, URISyntaxException {
        URI uri = new URI("edu/yu/txt.txt");
        String txt = "random";
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream("content".getBytes()), uri, DocumentStore.DocumentFormat.TXT); // Add a document first
        String oldValue = store.setMetadata(uri, "key1", "value1");
        assertNull(oldValue); // No previous value
        assertEquals("value1", store.getMetadata(uri, "key1"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataNullURI() {
        DocumentStore store = new DocumentStoreImpl();
        store.setMetadata(null, "key", "value");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataBlankURI() {
        DocumentStore store = new DocumentStoreImpl();
        store.setMetadata(URI.create(""), "key", "value");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataNullKey() {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        store.setMetadata(uri, null, "value");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataBlankKey() {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        store.setMetadata(uri, "", "value");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetMetadataDocumentNotFound() {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        store.setMetadata(uri, "key", "value");
    }
    @Test
    public void testGetMetadataNonExistentKey() throws IOException {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream("content".getBytes()), uri, DocumentStore.DocumentFormat.TXT);
        assertNull(store.getMetadata(uri, "nonexistentKey"));
    }
    @Test
    public void testPutNewDocument() throws IOException {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        int hashCode = store.put(new ByteArrayInputStream("content".getBytes()), uri, DocumentStore.DocumentFormat.TXT);
        assertEquals(0, hashCode);
        assertNotNull(store.get(uri));
    }
    @Test(expected = IllegalArgumentException.class)
    public void testPutNullFormat() throws IOException {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream("content".getBytes()), uri, null);
    }
    @Test
    public void testGetExistingDocument() throws IOException {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        String content = "This is the content";
        store.put(new ByteArrayInputStream(content.getBytes()), uri, DocumentStore.DocumentFormat.TXT);
        Document document = store.get(uri);
        assertEquals(content, new String(document.getDocumentTxt().getBytes(), StandardCharsets.UTF_8));
    }
    @Test
    public void testGetNonExistentDocument() {
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        assertNull(store.get(uri));
    }
    /*@Test
    public void testPutDelete() throws IOException{
        URI uri = URI.create("http://example.com/doc1");
        DocumentStore store = new DocumentStoreImpl();
        store.get(uri).hashCode();

        int hashCode1 = store.put(new ByteArrayInputStream("content1".getBytes()), uri, DocumentStore.DocumentFormat.TXT);
        int hashCode2 = store.put(null, uri, DocumentStore.DocumentFormat.TXT); // Delete
        assertEquals(hashCode1, hashCode2); // Check deleted document's hashCode
        assertNull(store.get(uri)); // Verify document is deleted
    }*/
}