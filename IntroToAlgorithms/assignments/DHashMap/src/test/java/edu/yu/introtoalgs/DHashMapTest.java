package edu.yu.introtoalgs;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertEquals;

public class DHashMapTest {

    @Test
    public void testDHashMap() {
        final int perServerMaxCapacity = 2;
        final DHashMapBase<String , Integer> dhm = new DHashMap<>(perServerMaxCapacity);
        dhm. addServer(12 , new SizedHashMap<String , Integer>( perServerMaxCapacity));
        dhm. addServer(18 , new SizedHashMap<String , Integer>( perServerMaxCapacity));
        dhm. put("foo" , 1);
        dhm. put("bar" , 2);
        final int v = dhm.get("bar");
        dhm.remove("foo");
        dhm. addServer(5 , new SizedHashMap<String , Integer>( perServerMaxCapacity));
        dhm. removeServer(12);
        final int v2 = dhm.get("bar");
    }
    @Test
    public void testRemoveServer() {
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);

        dhashMap.addServer(1, new SizedHashMap<>(2));
        dhashMap.addServer(2, new SizedHashMap<>(2));
        dhashMap.put("key1", "value1");
        dhashMap.put("key2", "value2");

        dhashMap.removeServer(1);

        assertEquals("value1", dhashMap.get("key1"));
        assertEquals("value2", dhashMap.get("key2"));
    }
    @Test
    public void testAddServerInvalido() {
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        assertThrows(IllegalArgumentException.class, () -> dhashMap.addServer(-2, new SizedHashMap<>(2)));
    }
    @Test
    public void addServerNull() {
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        assertThrows(IllegalArgumentException.class, () -> dhashMap.addServer(1, null));
    }
    @Test
    public void addServerandGet(){
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        dhashMap.addServer(1, new SizedHashMap<>(2));
        dhashMap.put("key1", "value1");
        dhashMap.put("key2", "value2");

        assertEquals("value1", dhashMap.get("key1"));
        assertEquals("value2", dhashMap.get("key2"));
    }
    @Test
    public void soloPut(){
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        assertThrows(IllegalStateException.class, () -> dhashMap.put("key1", "value1"));
    }
    @Test
    public void removeKey(){
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        dhashMap.addServer(1, new SizedHashMap<>(2));
        dhashMap.put("key1", "value1");
        dhashMap.put("key2", "value2");
        assertEquals("value1", dhashMap.get("key1"));
        assertEquals("value2", dhashMap.get("key2"));
        dhashMap.remove("key1");
        assertNull("value1", dhashMap.get("key1"));
        assertEquals("value2", dhashMap.get("key2"));
    }
    @Test
    public void exceedServerCapacity(){
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        dhashMap.addServer(1, new SizedHashMap<>(2));
        dhashMap.put("key1", "value1");
        dhashMap.put("key2", "value2");
        assertThrows(IllegalStateException.class, () -> dhashMap.put("key3", "value3"));
    }
    @Test
    public void verQDevuelve(){
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
        dhashMap.addServer(1, new SizedHashMap<>(2));
        dhashMap.put("key1", "value1");
        assertEquals("value1", dhashMap.put("key1", "value2"));
    }
    @Test
    public void get2(){
        DHashMap<String, String> dhashMap;
        dhashMap = new DHashMap<>(2);
    }
    //borrar
    @Test
    public void test() {
        int perServerMaxCapacity = 2 ;
        DHashMapBase<String , Integer> dhm = new DHashMap<>(perServerMaxCapacity ) ;
        dhm.addServer( 12 , new SizedHashMap<String , Integer >(perServerMaxCapacity)) ;
        dhm.addServer ( 18 , new SizedHashMap<String , Integer >(perServerMaxCapacity ) ) ;
        dhm.put ( "foo" , 1 ) ;
        dhm.put ( "bar" , 2) ;
        final int v = dhm. get ( "bar" ) ;
        dhm. remove ( "foo" ) ;
        dhm. addServer ( 5 , new SizedHashMap<String , Integer >(perServerMaxCapacity ) ) ;
        dhm. removeServer ( 12 ) ;
        final int v2 = dhm. get ( "bar" ) ;
    }

    //add server
    //uniquely identifies the server, can't be negative, can't currently be in the distributed hash map
    //the server's hash map: all data maintained by the server must be stored in this map, can't be null.
    @Test
    public void addServerTest1() {
        int perServerMaxCapacity = 5;
        DHashMapBase<String , Integer> dhm = new DHashMap<>(perServerMaxCapacity ) ;
        dhm.addServer(1, new SizedHashMap<>(perServerMaxCapacity));
        assertThrows(IllegalArgumentException.class, ()-> dhm.addServer(1, new SizedHashMap<>(perServerMaxCapacity)));
        assertThrows(IllegalArgumentException.class, ()-> dhm.addServer(-1, new SizedHashMap<>(perServerMaxCapacity)));
        assertThrows(IllegalArgumentException.class, ()-> dhm.addServer(-1, new SizedHashMap<>(perServerMaxCapacity)));
        int n = 0;
        while(n<6){
            dhm.addServer(n++, new SizedHashMap<>(perServerMaxCapacity));
            n++;
        }
        assertThrows(IllegalArgumentException.class, ()->dhm.addServer(9, null));
    }

    //remove server
    //Removes the specified server from the distributed hash map.
    //The implementation must relocate the server's current hash map to other servers in the distributed hash map.
    //id uniquely identifies the server, can't be negative, must currently be in the distributed hash map
//    @Test
//    public void removeServerTest1() {
//        int perServerMaxCapacity = 5;
//        DHashMapBase<String , Integer> dhm = new DHashMap<>(perServerMaxCapacity ) ;
//        dhm.addServer(1, new SizedHashMap<>(5));
//        dhm.addServer(2, new SizedHashMap<>(5));
//        dhm.put("guagua", 3838);
//        dhm.put("bebe", 56);
//        dhm.removeServer(1);
//        assertEquals(dhm.get("guagua"), 3838);
//        assertEquals(dhm.get("bebe"), 56);
//        dhm.addServer(1, new SizedHashMap<>(5));
//        assertEquals(dhm.get("guagua"), 3838);
//        assertEquals(dhm.get("bebe"), 56);
//
//    }



    //put
    //Adds the specified key and value association to the distributed hash map.
    //can't be null
    //     * IllegalArgumentException if clients attempt to store more than this amount
    //     * of data.
    //@throws IllegalArgumentException if size constraints prevent the Map entry
    //from being stored or if no server has been added to the distributed hash map
    //return old value
    @Test
    public void Put() {
        int perServerMaxCapacity = 5;
        DHashMapBase<String , String> dhm = new DHashMap<>(perServerMaxCapacity ) ;
        assertThrows(IllegalStateException.class, ()->dhm.put("Key", "9"));
        dhm.addServer(1, new SizedHashMap<>(perServerMaxCapacity));
        dhm.put("guagua", "3838");
        dhm.put("bebe", "56");
        dhm.put("Key", "9");
        dhm.put("key2", "47");
        dhm.put("key3", "45");
        assertThrows(IllegalStateException.class, ()->dhm.put("Key4", "10"));
        dhm.addServer(12, new SizedHashMap<>(perServerMaxCapacity));
        assertEquals("9", dhm.put("Key", "value2"));
    }

    //get
    //Returns the value to which the specified key is mapped, or null if this
    //map contains no mapping for the key.
    //key may not be null.



    //remove
    //Removes the mapping for a key from this map if it is present.
    //@param key key whose mapping is to be removed from the map, may not be null.
    //@returns the previous value associated with key, or null if there was no mapping for key.
    @Test
    public void remove() {
        int perServerMaxCapacity = 5;
        DHashMapBase<String, String> dhm = new DHashMap<>(perServerMaxCapacity);

        // Test inserting into empty map (should throw because no servers exist yet)
        assertThrows(IllegalStateException.class, () -> dhm.put("Key", "9"));

        // Add first server
        dhm.addServer(1, new SizedHashMap<>(perServerMaxCapacity));

        // Adding entries within the capacity of the first server
        dhm.put("guagua", "3838");
        dhm.put("bebe", "56");
        dhm.put("Key", "value1");
        dhm.put("key2", "47");
        dhm.put("key3", "45");

        // Attempt to exceed capacity of first server (should throw exception)
        assertThrows(IllegalStateException.class, () -> dhm.put("Key4", "10"));

        // Add a second server, expanding capacity
        dhm.addServer(12, new SizedHashMap<>(perServerMaxCapacity));

        // Now adding should be possible again because we added capacity
        assertEquals("value1", dhm.put("Key", "value2")); // Updating the existing key
        assertEquals(null, dhm.put("key1", "33"));        // Adding new key

        // Ensure correct values are returned
        assertEquals("3838", dhm.get("guagua"));
        assertEquals("56", dhm.get("bebe"));

        // Removing keys and checking that correct values are returned
        assertEquals("3838", dhm.remove("guagua"));
        assertEquals("56", dhm.remove("bebe"));
        assertEquals("value2", dhm.remove("Key"));
        assertEquals("47", dhm.remove("key2"));
        assertEquals("45", dhm.remove("key3"));
    }
    @Test
    public void putAndReplace() {
    int perServerMaxCapacity = 5;
    DHashMapBase<String, String> dhm = new DHashMap<>(perServerMaxCapacity);

    // Add a server
    dhm.addServer(1, new SizedHashMap<>(perServerMaxCapacity));

    // Insert entries
    assertNull(dhm.put("Key1", "InitialValue")); // No previous value, should return null
    assertEquals("InitialValue", dhm.put("Key1", "NewValue")); // Should return the old value

    // Verify the new value is set correctly
    assertEquals("NewValue", dhm.get("Key1"));

    // Verify removal
    assertEquals("NewValue", dhm.remove("Key1"));
    assertNull(dhm.get("Key1")); // Ensure the key is removed
}
    @Test
    public void runtime(){
        int perServerMaxCapacity = 5;
        DHashMapBase<String , Integer> dhm = new DHashMap<>(perServerMaxCapacity ) ;
        assertThrows(IllegalStateException.class, ()->dhm.put("Key", 9));
        dhm.addServer(1, new SizedHashMap<>(perServerMaxCapacity));
        dhm.put("guagua", 3838);
        dhm.put("bebe", 56);
        dhm.put("Key", 9);
        dhm.put("key2", 47);
        dhm.put("key3", 45);
        dhm.remove("guagua");
    }
}