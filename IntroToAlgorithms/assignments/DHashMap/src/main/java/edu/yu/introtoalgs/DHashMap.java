package edu.yu.introtoalgs;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DHashMap<Key, Value> extends DHashMapBase<Key, Value> {
    private int maxCapacity;
    private TreeMap<Integer, SizedHashMap<Key, Value>> servers = new TreeMap<>();

    /**
     * Constructor: client specifies the per-server capacity of participating
     * servers (hash maps) in the distributed hash map.  (For simplicity, each
     * server has the same capacity.)  The system must throw an
     * IllegalArgumentException if clients attempt to store more than this amount
     * of data.
     *
     * @param perServerMaxCapacity per server maximum capacity, must be greater
     *                             than 0.
     * @throws IllegalArgumentException as appropriate.
     */
    public DHashMap(int perServerMaxCapacity) {
        super(perServerMaxCapacity);
        this.maxCapacity = perServerMaxCapacity;
        if (perServerMaxCapacity <= 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the per server max capacity.
     *
     * @return per server max capacity.
     */
    @Override
    public int getPerServerMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Adds a server to the distributed hash map.  The implementation may choose
     * to rebalance the contents of the distributed hash map to incorporate the
     * new server.
     *
     * @param id  uniquely identifies the server, can't be negative, can't
     *            currently be in the distributed hash map
     * @param map the server's hash map: all data maintained by the server must
     *            be stored in this map, can't be null.  It's the client's responsibility to
     *            ensure that all supplied maps have the specified perServerMaxCapacity.
     *            The implementation is responsible for ensuring that the map reference
     *            isn't modified.
     * @throws IllegalArgumentException as appropriate.
     */
    @Override
    public void addServer(int id, SizedHashMap<Key, Value> map) {
        if (id < 0 || servers.containsKey(id)){
            throw new IllegalArgumentException();
        }
        if (map == null || map.size() > maxCapacity){
            throw new IllegalArgumentException();
        }

        int serverHash = hashServerId(id);
        servers.put(serverHash, map);
        rehashKeys(serverHash);
    }

    private int hashServerId(int id) {
        return Integer.hashCode(id);
    }

    private void rehashKeys(int ServerHash) {
        // Create a temporary map to hold all entries
        Integer previousServerHash = servers.lowerKey(ServerHash);
        if (previousServerHash == null) {
            previousServerHash = servers.lastKey(); // Wrap around the ring
        }

        // Move keys from the previous server to the new server if necessary
        SizedHashMap<Key, Value> previousServer = servers.get(previousServerHash);
        SizedHashMap<Key, Value> newServer = servers.get(ServerHash);

        // Collect entries that need to be moved
        Map<Key, Value> toMove = new HashMap<>();
        for (Map.Entry<Key, Value> entry : previousServer.entrySet()) {
            int keyHash = hashId(entry.getKey());
            if (keyHash <= ServerHash && keyHash > previousServerHash) {
                toMove.put(entry.getKey(), entry.getValue());
            }
        }

        // Move entries to the new server
        for (Map.Entry<Key, Value> entry : toMove.entrySet()) {
            previousServer.remove(entry.getKey());
            newServer.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the specified server from the distributed hash map.  The
     * implementation must relocate the server's current hash map to other
     * servers in the distributed hash map.
     *
     * @param id uniquely identifies the server, can't be negative, must
     *           currently be in the distributed hash map
     * @throws IllegalArgumentException as appropriate.
     */
    @Override
    public void removeServer(int id) {
        int serverHash = hashServerId(id);
        if (!servers.containsKey(serverHash)) {
            throw new IllegalArgumentException();
        }
        SizedHashMap<Key, Value> serverToRemove = servers.remove(serverHash);
        for (Map.Entry<Key, Value> entry : serverToRemove.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds the specified key and value association to the distributed hash map.
     *
     * @param key   can't be null
     * @param value
     * @throws IllegalArgumentException if size constraints prevent the Map entry
     *                                  from being stored
     * @throws IllegalStateException    if no server has been added to the
     *                                  distributed hash map
     * @see #addServer
     * @see Map#put
     */
    @Override
    public Value put(Key key, Value value) {
        if (key == null){
            throw new IllegalArgumentException();
        }
        if (servers.isEmpty()){
            throw new IllegalStateException();
        }
        int serverId = hashId(key);
        SizedHashMap<Key, Value> server = servers.get(serverId);

        if (server == null || server.size() >= maxCapacity) {
            throw new IllegalStateException();
        }
        Value oldValue = server.get(key);
        server.put(key, value);

        return oldValue;

    }
    private int hashId(Key key) {
        int h = key.hashCode();
        int serverId= Math.abs(h) % servers.size();

        int i = 0;
        for (int id : servers.keySet()) {
            if (i == serverId){
                return id;
            }
            i++;
        }
        throw new IllegalStateException();
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned, may not be null.
     * @return the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key
     * @throws IllegalArgumentException if key is null
     * @see Map#get
     */
    @Override
    public Value get(Object key) {
        if (key == null){
            throw new IllegalArgumentException();
        }
        int serverId = hashId((Key) key);
        SizedHashMap<Key, Value> server = servers.get(serverId);
        return server.get(key);
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed from the map, may not be
     *            null.
     * @throws IllegalArgumentException as appropriate.
     * @returns the previous value associated with key, or null if there was no
     * mapping for key.
     * @see Map#remove
     */
    @Override
    public Value remove(Object key) {
        if (key == null){
            throw new IllegalArgumentException();
        }
        int serverId = hashId((Key) key);
        SizedHashMap<Key, Value> server = servers.get(serverId);
        return server.remove(key);
    }
}
