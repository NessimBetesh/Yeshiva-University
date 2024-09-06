package edu.yu.cs.com1320.project.stage6;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.stage6.*;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;
import edu.yu.cs.com1320.project.stage6.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;


public class test {
    private BTreeImpl<URI, String> bTree;
    private PersistenceManager<URI, String> pm;
    private URI uri1;
    private URI uri2;
    private URI uri3;
    private URI uri4;

    @BeforeEach
    public void setUp() throws IOException, URISyntaxException {
        this.bTree = new BTreeImpl<>();
        File baseDir = new File(System.getProperty("user.dir") + File.separator + "testDir");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        this.pm = new DocumentPersistenceManager<>(baseDir);
        this.bTree.setPersistenceManager(pm);
        this.uri1 = new URI("http://example.com/doc1");
        this.uri2 = new URI("http://example.com/doc2");
        this.uri3 = new URI("http://example.com/doc3");
        this.uri4 = new URI("http://example.com/doc4");
    }

    @Test
    public void testPutAndGet() {
        bTree.put(uri1, "Document 1");
        bTree.put(uri2, "Document 2");
        bTree.put(uri3, "Document 3");

        assertEquals("Document 1", bTree.get(uri1));
        assertEquals("Document 2", bTree.get(uri2));
        assertEquals("Document 3", bTree.get(uri3));
        assertNull(bTree.get(uri4));
    }

    @Test
    public void testOverwriteValue() {
        bTree.put(uri1, "Document 1");
        bTree.put(uri1, "New Document 1");

        assertEquals("New Document 1", bTree.get(uri1));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(bTree.isEmpty());
        bTree.put(uri1, "Document 1");
        assertFalse(bTree.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, bTree.size());
        bTree.put(uri1, "Document 1");
        bTree.put(uri2, "Document 2");
        assertEquals(2, bTree.size());
    }

    @Test
    public void testHeight() {
        assertEquals(0, bTree.height());
        bTree.put(uri1, "Document 1");
        bTree.put(uri2, "Document 2");
        bTree.put(uri3, "Document 3");
        assertTrue(bTree.height() >= 0); // height can be 0 or greater depending on splits
    }


    @Test
    public void testDelete() throws IOException {
        bTree.put(uri1, "Document 1");
        assertEquals("Document 1", bTree.get(uri1));

        pm.delete(uri1);
        assertNull(pm.deserialize(uri1));
    }
}
