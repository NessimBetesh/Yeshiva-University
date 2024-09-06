package edu.yu.cs.com1320.project.stage4;
import com.sun.jdi.Value;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import org.junit.Assert;
import org.junit.Test;

import static edu.yu.cs.com1320.project.stage4.DocumentStore.DocumentFormat.TXT;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class stage4testTodo {
    DocumentStoreImpl dsi = new DocumentStoreImpl();
    URI uri = new URI("nessim.com");
    URI url = new URI("nessim");
    URI link = new URI("gut.shabbes");
    DocumentImpl document = new DocumentImpl(uri, "haha");
    private StackImpl<Integer> stack = new StackImpl<>();
    private HashTableImpl<String, String> table = new HashTableImpl<>();
    String str = ("Matame, yo feliz");
    InputStream inputStream = new ByteArrayInputStream(str.getBytes());
    InputStream is = new ByteArrayInputStream(str.getBytes());
    private TooSimpleTrieCopy<Integer> trieCopy = new TooSimpleTrieCopy<>();
    private TrieImpl<Integer> trieimpl = new TrieImpl<>();

    public stage4testTodo() throws URISyntaxException {
    }

    public void TrieTest() throws URISyntaxException {
    }


    @Test
    public void Test1() throws URISyntaxException {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(613);
        trieCopy.put("Nah", 613);
        assertEquals(trieCopy.getAllSorted("Nah"), result);


    }
    @Test
    public void trieDeletes2() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri1 = new URI("uri1");
        URI uri2 = new URI("uri2");
        URI uri3 = new URI("uri3");
        URI uri4 = new URI("uri4");

        String string1 = "David David david Dovid Dovid Walter";
        String string2 = "Mati Mati Mati Matusa Matusa atusa David";
        String string3 = "enrique Matusa eduardo";
        String string4 = "avion Ma";

        InputStream is1 = new ByteArrayInputStream(string1.getBytes());
        InputStream is2 = new ByteArrayInputStream(string2.getBytes());
        InputStream is3 = new ByteArrayInputStream(string3.getBytes());
        InputStream is4 = new ByteArrayInputStream(string4.getBytes());

        store.put(is1, uri1, DocumentStore.DocumentFormat.TXT);
        store.put(is2, uri2, DocumentStore.DocumentFormat.TXT);
        store.put(is3, uri3, DocumentStore.DocumentFormat.TXT);
        store.put(is4, uri4, DocumentStore.DocumentFormat.TXT);

        List<Document> lista = new ArrayList<>();

        store.delete(uri3);
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());
        store.put(inputStream, url, DocumentStore.DocumentFormat.TXT);
        lista = store.searchByPrefix("Ma");
        assertEquals(3, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());

    }

    @Test
    public void Test2() {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(613);
        trieCopy.put("Nah", 613);
        trieCopy.deleteAll("Nah");
        assertEquals(trieCopy.getAllSorted("Nah"), null);
    }

    @Test
    //get new empty Set since there are no matches because it was deleted
    public void delete() {
        trieimpl.put("Nah", 613);
        trieimpl.delete("Nah", 613);
        HashSet<Value> mySetOfValues = new HashSet<>();
        assertEquals((trieimpl.get("Nah")), mySetOfValues);
    }

    @Test
    //@return the value which was deleted.
    public void delete1() {
        trieimpl.put("Nah", 613);
        int a = trieimpl.delete("Nah", 613);
        assertEquals(a, 613);
    }

    @Test
    //If the key did not contain the given value, return null.
    public void delete2() {
        trieimpl.put("Nah", 613);
        assertNull(trieimpl.delete("Nah", 7));
    }

    @Test
    //(do not remove the value from other nodes in the Trie)
    public void delete3() {
        HashSet<Integer> result = new HashSet<>();
        result.add(21);
        trieimpl.put("Nao", 613);
        trieimpl.put("Linda preciosa", 21);
        trieimpl.delete("Nah", 613);
        assertEquals((trieimpl.get("Linda preciosa")), result);
    }

    @Test
    public void delete4() {
        trieimpl.put("she", 0);
        trieimpl.put("shells", 24);
        trieimpl.put("sells", 6);
        int a = trieimpl.delete("shells", 24);
        assertEquals(a, 24);
    }

    @Test
    public void delete5() {
        HashSet<Integer> result = new HashSet<>();
        result.add(0);
        trieimpl.put("she", 0);
        trieimpl.put("shells", 24);
        trieimpl.put("sells", 6);
        int a = trieimpl.delete("shells", 24);
        assertEquals((trieimpl.get("she")), result);
    }

    @Test
    public void delete6() {
        HashSet<Integer> result = new HashSet<>();
        result.add(6);
        trieimpl.put("she", 0);
        trieimpl.put("shells", 24);
        trieimpl.put("sells", 6);
        int a = trieimpl.delete("shells", 24);
        assertEquals((trieimpl.get("sells")), result);
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @return a Set of all Values that were deleted.
     */
    @Test
    public void deleteAll() {
        trieimpl.put("Nah", 613);
        trieimpl.deleteAll("Nah");
        HashSet<Value> mySetOfValues = new HashSet<>();
        assertEquals((trieimpl.get("Nah")), mySetOfValues);
    }

    @Test
    public void deleteAll1() {
        HashSet<Integer> result = new HashSet<>();
        result.add(613);
        trieimpl.put("Nah", 613);
        assertEquals(trieimpl.deleteAll("Nah"), result);
    }

    @Test
    public void deleteAll3() {
        HashSet<Integer> result = new HashSet<>();
        result.add(21);
        trieimpl.put("Nao", 613);
        trieimpl.put("Linda preciosa", 21);
        trieimpl.deleteAll("Nao");
        assertEquals((trieimpl.get("Linda preciosa")), result);
    }

    @Test
    public void deleteAll4() {
        HashSet<Integer> result = new HashSet<>();
        result.add(24);
        trieimpl.put("she", 0);
        trieimpl.put("shells", 24);
        trieimpl.put("sells", 6);
        assertEquals(trieimpl.deleteAll("shells"), result);
    }

    @Test
public void deleteAll5() {
    TrieImpl<Integer> trie = new TrieImpl<>();
    HashSet<Integer> result = new HashSet<>();
    result.add(0);
    trie.put("she", 0);
    trie.put("shells", 24);
    trie.put("sells", 6);
    trie.deleteAll("shells");

    // Ensure that the values associated with the key "shells" are deleted
    assertTrue(trie.get("shells").isEmpty());

    // Ensure that the values associated with the key "she" remain unchanged
    assertEquals(trie.get("she"), result);
}


    @Test
    public void deleteAll6() {
        HashSet<Integer> result = new HashSet<>();
        result.add(6);
        trieimpl.put("she", 0);
        trieimpl.put("shells", 24);
        trieimpl.put("sells", 6);
        trieimpl.deleteAll("shells");
        assertEquals((trieimpl.get("sells")), result);
    }

    //delete vs deleteAll
    @Test
    public void deleteanddeleteAll() {
        TrieImpl<Integer> trie = new TrieImpl<>();

    // Add values to the trie
    trie.put("i", 1);
    trie.put("il", 2);
    trie.put("ila", 3);
    trie.put("ilan", 4);

    // Delete all values associated with the key "i"
    Set<Integer> deletedValues = trie.deleteAll("i");

    // Ensure that the deleted values contain only the value associated with the key "i"
    Set<Integer> expectedDeletedValues = new HashSet<>();
    expectedDeletedValues.add(1);
    assertEquals(expectedDeletedValues, deletedValues);

    // Ensure that the values associated with the key "i" are now empty
    assertTrue(trie.get("i").isEmpty());

    // Ensure that the values associated with other keys remain unaffected
    assertEquals(Set.of(2), trie.get("il"));
    assertEquals(Set.of(3), trie.get("ila"));
    assertEquals(Set.of(4), trie.get("ilan"));
}

    /**
     * Remember to check delete all!
     */
    @Test
    public void test() {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(613);
        trieCopy.put("Nah", 613);
        assertEquals(trieCopy.getAllSorted("NAH"), null);
    }

   @Test
    public void searchByPrefix1() throws IOException {
       String string = "Victor Chuuuuupa";
       String myString = "Chuupala, te encanta, besitos y abrazos pap, Chu";
       InputStream i = new ByteArrayInputStream(string.getBytes());
       InputStream si = new ByteArrayInputStream(myString.getBytes());
       dsi.put(i, uri, TXT);
       dsi.put(si, url, TXT);

       List<Document> actual = dsi.searchByPrefix("Chu");

       System.out.println("Documents found:");
       for (Document doc : actual) {
           System.out.println(doc.getDocumentTxt());
       }

       List<Document> expected = new ArrayList<>();
       expected.add(dsi.get(url));
       expected.add(dsi.get(uri));

       // Assert that the size of both lists is the same
       assertEquals(expected.size(), actual.size());

       // Assert that the contents of the lists are the same
       for (Document doc : expected) {
           assertTrue(actual.contains(doc));
       }
   }
@Test
public void search() throws IOException {
    String string = "Victor Chuuuuupa";
    String myString = "Chuupala, te encanta, besitos y abrazos pap, Chu Victor";
    InputStream i = new ByteArrayInputStream(string.getBytes());
    InputStream si = new ByteArrayInputStream(myString.getBytes());
    dsi.put(i, uri, TXT);
    dsi.put(si, url, TXT);

    List<Document> actual = dsi.search("Victor");

    System.out.println("Documents found:");
    for (Document doc : actual) {
        System.out.println(doc.getDocumentTxt());
    }

    List<Document> expected = new ArrayList<>();
    expected.add(dsi.get(url));
    expected.add(dsi.get(uri));

    // Assert that the size of both lists is the same
    assertEquals(expected.size(), actual.size());

    // Assert that the contents of the lists are the same
    for (Document doc : expected) {
        assertTrue(actual.contains(doc));
    }
}


    /**
     * get all matches which contain a String with the given prefix, sorted in descending order, where "descending" is defined by the comparator.
     * NOTE FOR COM1320 PROJECT: FOR PURPOSES OF A KEYWORD SEARCH, THE COMPARATOR SHOULD DEFINE ORDER AS HOW MANY TIMES THE KEYWORD APPEARS IN THE DOCUMENT.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @return a List of all matching Values containing the given prefix, in descending order. Empty List if no matches.
     */
/*
    @Test
    public void testGetAllWithPrefixSorted() {
        TrieImpl<Integer> trie = new TrieImpl<>();

        // Populate the trie with some values
        trie.put("apple", 3);
        trie.put("banana", 5);
        trie.put("orange", 4);
        trie.put("kiwi", 2);
        trie.put("peach", 1);

        // Define the prefix and the comparator
        String prefix = "a";
        Comparator<Integer> comparator = Comparator.reverseOrder();

        // Get the sorted list of values with the given prefix
        List<Integer> sortedValues = trie.getAllWithPrefixSorted(prefix, comparator);

        // Expected sorted values for the prefix "a"
        List<Integer> expected = Arrays.asList(5, 4, 3);

        // Assert that the sorted values match the expected values
        assertEquals(expected, sortedValues);

        // Additional assertions to ensure all expected values are present and in the correct order
        assertEquals(expected.size(), sortedValues.size()); // Check if the sizes match

        // Check if the values are sorted in descending order
        for (int i = 0; i < sortedValues.size() - 1; i++) {
            assertTrue(comparator.compare(sortedValues.get(i), sortedValues.get(i + 1)) >= 0);
        }
    }
/*
    @Test
    public void getALlMatches() {
        List<String> list = new ArrayList<>();
        trieimpl.put("i", 1);
        trieimpl.put("il", 2);
        trieimpl.put("ila", 3);
        trieimpl.put("ilan", 4);
        list.add("il");
        list.add("ila");
        list.add("ilan");
        assertEquals(trieimpl.getAllWithPrefixSorted("il", Comparator.comparingInt(doc -> -wordCountInTrie(doc))));
    }

 */

    @Test
public void testPut() {
    TrieImpl<Integer> trieImpl = new TrieImpl<>();
    trieImpl.put("Naomi", 2);
    trieImpl.put("Naomi", 3);
    List<Integer> expected = Arrays.asList(2, 3);
    Comparator<Integer> descendingComparator = Comparator.reverseOrder();
    List<Integer> actual = trieImpl.getSorted("Aliza", descendingComparator);
    assertEquals(expected, actual);
}

@Test
    public void deleteAllWithPrefix(){
        trieimpl.put("Aliza", 2);
        trieimpl.put("Ali", 3);
        trieimpl.put("Aligator", 4);
        trieimpl.put("ali", 6);
        trieimpl.put("nessim", 5);
        Set<Integer> list = new HashSet<>();
        list.add(4);
        list.add(3);
        list.add(2);
        assertEquals(list, trieimpl.deleteAllWithPrefix("Ali"));
    }
    @Test
    public void searchMetadataWithEmptyMap() throws IOException {
        dsi.put(inputStream, uri, TXT);
        dsi.put(is, uri, TXT);
        dsi.setMetadata(uri, "cueco", "msrivon");
        assertEquals(dsi.setMetadata(uri, "cueco", "msrivon"), "msrivon");
        //map.put("cueco", "msrivon");
        List<Document> list = new ArrayList<>();
        list.add(dsi.get(uri));
        assertEquals(dsi.searchByMetadata(Collections.emptyMap()), Collections.emptyList());
    }


    @Test
    public void wordCountAndGetWordsTest() throws URISyntaxException {
        DocumentImpl txtDoc = new DocumentImpl(new URI("placeholder"), "Es!tas son. Unas p@al@abras con& s**ymbolos adentro. (e)Spero que esTe test tEst passe don't! dont");
        assertEquals(0, txtDoc.wordCount("bundle"));
        assertEquals(1, txtDoc.wordCount("Estas"));
        assertEquals(1, txtDoc.wordCount("son"));
        assertEquals(1, txtDoc.wordCount("symbolos"));
        assertEquals(1, txtDoc.wordCount("passe"));
        assertEquals(1, txtDoc.wordCount("test"));
        assertEquals(1, txtDoc.wordCount("tEst"));
        assertEquals(2, txtDoc.wordCount("dont"));
        Set<String> words = txtDoc.getWords();
        assertEquals(14, words.size());
        assertTrue(words.contains("Unas"));

        DocumentImpl binaryDoc = new DocumentImpl(new URI("0110"), new byte[]{0, 1, 1, 0});
        assertEquals(0, binaryDoc.wordCount("anythingYouPutHereShouldBeZero"));
        Set<String> words2 = binaryDoc.getWords();
        assertEquals(0, words2.size());
    }
    @Test
    public void trieSearch() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri1 = new URI("uri1");
        URI uri2 = new URI("uri2");
        URI uri3 = new URI("uri3");
        URI uri4 = new URI("uri4");

        String string1 = "David David david Dovid Dovid Walter";
        String string2 = "Mati Mati Mati Matusa Matusa David";
        String string3 = "enrique Matusa eduardo";
        String string4 = "avion Ma";

        InputStream is1 = new ByteArrayInputStream(string1.getBytes());
        InputStream is2 = new ByteArrayInputStream(string2.getBytes());
        InputStream is3 = new ByteArrayInputStream(string3.getBytes());
        InputStream is4 = new ByteArrayInputStream(string4.getBytes());

        store.put(is1, uri1, DocumentStore.DocumentFormat.TXT);
        store.put(is2, uri2, DocumentStore.DocumentFormat.TXT);
        store.put(is3, uri3, DocumentStore.DocumentFormat.TXT);
        store.put(is4, uri4, DocumentStore.DocumentFormat.TXT);


        List<Document> lista = new ArrayList();
        lista = store.searchByPrefix("Da");
        Assert.assertEquals(2, lista.size());
        lista = store.searchByPrefix("Do");
        Assert.assertEquals(1, lista.size());
        lista = store.searchByPrefix("da");
        Assert.assertEquals(1, lista.size());
        lista = store.searchByPrefix("Ma");
        Assert.assertEquals(3, lista.size());
        assertEquals(lista.get(0), store.get(uri2));
        assertEquals(lista.get(1), store.get(uri4));
        assertEquals(lista.get(2), store.get(uri3));
        lista = store.search("Ma");
        Assert.assertEquals(1, lista.size());
        lista = store.search("Matusa");
        Assert.assertEquals(2, lista.size());
        lista = store.search("David");
        Assert.assertEquals(2, lista.size());
        lista = store.search("dovid");
        Assert.assertEquals(0, lista.size());


    }
    @Test
    public void trieDeletes() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri1 = new URI("uri1");
        URI uri2 = new URI("uri2");
        URI uri3 = new URI("uri3");
        URI uri4 = new URI("uri4");

        String string1 = "David David david Dovid Dovid Walter";
        String string2 = "Mati Mati Mati Matusa Matusa atusa David";
        String string3 = "enrique Matusa eduardo";
        String string4 = "avion Ma";

        InputStream is1 = new ByteArrayInputStream(string1.getBytes());
        InputStream is2 = new ByteArrayInputStream(string2.getBytes());
        InputStream is3 = new ByteArrayInputStream(string3.getBytes());
        InputStream is4 = new ByteArrayInputStream(string4.getBytes());

        store.put(is1, uri1, DocumentStore.DocumentFormat.TXT);
        store.put(is2, uri2, DocumentStore.DocumentFormat.TXT);
        store.put(is3, uri3, DocumentStore.DocumentFormat.TXT);
        store.put(is4, uri4, DocumentStore.DocumentFormat.TXT);

        List<Document> lista = new ArrayList<>();

        store.delete(uri3);
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());
        store.delete(uri2);
        lista = store.searchByPrefix("Ma");
        assertEquals(1, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(3, lista.size());

        store.deleteAll("David");
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(3, lista.size());
        store.deleteAll("Matusa");
        lista = store.searchByPrefix("Ma");
        assertEquals(1, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(3, lista.size());

        store.deleteAllWithPrefix("Da");
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(3, lista.size());
        store.deleteAllWithPrefix("Ma");
        lista = store.searchByPrefix("Ma");
        assertEquals(0, lista.size());
        store.undo();
        lista = store.searchByPrefix("Ma");
        assertEquals(3, lista.size());

        store.deleteAll("Matusa");
        lista = store.searchByPrefix("Ma");
        assertEquals(1, lista.size());
        store.undo(uri2);
        lista = store.searchByPrefix("Ma");
        assertEquals(2, lista.size());
        /*
        store.undo(uri2);
        lista = store.searchByPrefix("Ma");
        assertEquals(1, lista.size());
        store.undo(uri4);
        lista = store.searchByPrefix("Ma");
        assertEquals(0, lista.size());
         */
    }
    @Test
    public void stage4ProblemWithAlphabeticRange() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri1 = new URI("uri1");
        String string1 = "String-1";
        InputStream is = new ByteArrayInputStream(string1.getBytes());

        store.put(is,uri1,DocumentStore.DocumentFormat.TXT);
        Document get = store.get(uri1);
        List<Document> dd = store.search("String1");
        List<Document> de = store.search("String");


        assertEquals(string1, get.getDocumentTxt());
        Assert.assertEquals(1, dd.size());
        Assert.assertEquals(0, de.size());

        store.undo();
    }
    /*
    @Test
public void testSearchByPrefixAfterDeletion() throws URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
    // Assuming store and other setup is available
    store.deleteAllWithPrefix("Da"); // Delete documents with prefix "Da"
    List<Document> lista = store.searchByPrefix("Ma"); // Search documents with prefix "Ma"
    assertEquals(2, lista.size()); // Ensure correct number of documents found
}
     */

    @Test
    public void getAllWithPrefixSorted() {
        TrieImpl<Integer> trieimpl = new TrieImpl<>();
        trieimpl.put("Aliza", 2);
        trieimpl.put("Ali", 3);
        trieimpl.put("juan", 4);

        // Create a set of expected values
        List<Integer> expectedValues = new ArrayList<>();
        expectedValues.add(2);
        expectedValues.add(3);

        // Retrieve the values for the key "Naomi"
        Comparator<Integer> descendingComparator = Comparator.reverseOrder();

        List<Integer> retrievedValues = trieimpl.getAllWithPrefixSorted("Ali", descendingComparator);

        // Check if the retrieved values match the expected values
        assertEquals(expectedValues, retrievedValues);
    }
    @Test
    public void Get2() {
        trieimpl.put("Nao", 2);
        trieimpl.put("Naomi", 3);
        Set<Integer> list = new HashSet<>();
        list.add(2);
        list.add(3);
        assertNotEquals(trieimpl.get("Nao"), list);
    }
    @Test
    public void Get3() {
        trieimpl.put("Nao", 2);
        trieimpl.put("Naomi", 3);
        Set<Integer> list = new HashSet<>();
        list.add(2);
        assertEquals(trieimpl.get("Nao"), list);
    }


     @Test
    public void GetP() {
        trieimpl.put("Nao", 2);
        trieimpl.put("Naomi", 3);
        trieimpl.put("Naomita", 4);
        Set<Integer> list = new HashSet<>();
        list.add(2);
        list.add(3);
        list.add(4);
        assertNotEquals(trieimpl.getAllWithPrefixSorted("Nao", Comparator.comparingInt(doc -> doc)), list);
    }
    @Test
    public void NessimMaricon(){
        TrieImpl<String> myTrie = new TrieImpl<String>();
        myTrie.put("Nessim", "Aliza");
        myTrie.put("Nessim", "Ilan");
        Set<String> set = new HashSet<>();
        set.add("Aliza");
        set.add("Ilan");
        assertEquals(myTrie.get("Nessim"), set);
    }
}