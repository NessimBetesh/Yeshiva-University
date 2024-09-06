package edu.yu.cs.com1320.project.stage6.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.PersistenceManager;

import java.io.*;
import com.google.gson.*;
import jakarta.xml.bind.DatatypeConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private File baseDir;
/*
    public DocumentPersistenceManager() {

        this.baseDir = new File(System.getProperty("user.dir"));
    }
 */

    public DocumentPersistenceManager(File baseDir) throws IOException {
        if (baseDir != null) {
            this.baseDir = baseDir;
        } else {
            this.baseDir = new File(System.getProperty("user.dir"));
        }
    }

    private class DocumentSerialize implements JsonSerializer<Document>{
        /**
         * @param document
         * @param type
         * @param context
         * @return
         */
        @Override
        public JsonElement serialize(Document document, Type type, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            JsonObject metadata = new JsonObject();
            JsonObject Word = new JsonObject();
            json.addProperty("URI", document.getKey().toString());
            json.add("wordMap", context.serialize(document.getWordMap()));
            /*
            Map<String, Integer> wordmap = document.getWordMap();
            for (Map.Entry<String, Integer> words: wordmap.entrySet()) {
                Word.addProperty(words.getKey(), words.getValue());
            }
            json.add("wordmap", Word);
             */

            for (Map.Entry<String, String> entry : document.getMetadata().entrySet()) {
                metadata.addProperty(entry.getKey(), entry.getValue());
            }
            json.add("metadata", metadata);
            if (document.getDocumentTxt() != null) {
                json.addProperty("txt", document.getDocumentTxt());
            }
            else {
                //byte[] bytes = document.getDocumentBinaryData();
                //String base64Encoded = Base64.getEncoder().encodeToString(document.getDocumentBinaryData());
                //System.out.println("Base64 encoded: " + base64Encoded);
                String base64Encoded = DatatypeConverter.printBase64Binary(document.getDocumentBinaryData());
                json.addProperty("bytes", base64Encoded);
            }
            return json;
        }
    }
     /**
     * @param key
     * @param val
     * @throws IOException
     */
    @Override
    public void serialize (URI key, Document val) throws IOException {
        URI uri = key;
        Document document = val;
        Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, new DocumentSerialize()).create();
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(Document.class, new DocumentSerialize()).create();
//        Gson gson = gsonBuilder.create();
        String filepath = findDirectory(uri);
        File file = new File(filepath);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Could not create directory " + file.getParent());
        }
        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(document, writer);
        }

        /*
        if (file == null) {
            throw new IllegalStateException("Base directory is not set");
        }
        DocumentSerialize serializer = new DocumentSerialize ();
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonObject json = new JsonObject();
        json.add("URI", gsonBuilder.create().toJsonTree(key));
        json.add("map", gsonBuilder.create().toJsonTree(val.getWordMap));
         */
    }
    private class DocumentDeserialize implements JsonDeserializer<Document> {

        /**
         * @param jsonElement
         * @param type
         * @param context
         * @return
         * @throws JsonParseException
         */
        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            Gson gson = new Gson();
            JsonObject json = jsonElement.getAsJsonObject();
            String uriStr = json.get("URI").getAsString();
            URI uri = URI.create(uriStr);
            Document document;

            if (json.has("bytes")) {
                //byte[] decodedBytes = Base64.getDecoder().decode(json.get("bytes").getAsString());
                byte[] bytes = Base64.getDecoder().decode(json.get("bytes").getAsString());
                document = new DocumentImpl(uri, bytes);
            } else {
                String txt = json.get("txt").getAsString();
                Type mapType = new TypeToken<HashMap<String, Integer>>() {
                }.getType();
                Map<String, Integer> wordMap = context.deserialize(json.get("wordMap"), mapType);
                document = new DocumentImpl(uri, txt, wordMap);
                /*
                JsonObject wordMapJson = json.getAsJsonObject("wordmap");
                HashMap<String, Integer> wordMap = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : wordMapJson.entrySet()) {
                    wordMap.put(entry.getKey(), entry.getValue().getAsInt());
                 */
            }
//                String txt = json.get("txt").getAsString();
//                document = new DocumentImpl(uri, txt, wordMap);

            // Deserialize metadata
            JsonObject metadataJson = json.getAsJsonObject("metadata");
            HashMap<String, String> metadata = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : metadataJson.entrySet()) {
                metadata.put(entry.getKey(), entry.getValue().getAsString());
            }
            document.setMetadata(metadata);

            return document;
        }
    }
    /**
     //* @param key
     * @return
     * @throws IOException
     */
    @Override
    public Document deserialize(URI uri) throws IOException {
        File filepath = new File(findDirectory(uri));
        //File file = new File(this.baseDir, filepath);
        if (!filepath.exists()) {
            return null;
        }
        try (Reader reader = new FileReader(filepath)) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Document.class, new DocumentDeserialize()).create();
            Document document = gson.fromJson(reader, DocumentImpl.class);
            delete(uri);
            return document;
        }
        /*
        byte[] bytes = Files.readAllBytes(filepath.toPath());
        String content = new String(bytes);
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Document.class, new DocumentDeserialize());
           // Gson gson = gsonBuilder.create();
            Document doc = gsonBuilder.setLenient().create().fromJson(content, DocumentImpl.class);
            delete(key);
            return (Value) doc;
        }
        catch (IOException e){
            throw new IOException();
        }
         */
    }

    /**
     * delete the file stored on disk that corresponds to the given key
     *
    // * @param key
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    @Override
    public boolean delete(URI uri) throws IOException {
        if (uri == null) {
            throw new IOException("Key must be of type URI");
        }
        String filePath = findDirectory(uri);
        File fileToDelete = new File(filePath); //uri.toString()
        if (!fileToDelete.exists()) {
            return false;
        }
        boolean deleted = fileToDelete.delete();
        return deleted;
        //deleteEmptyParentDirectories(new File(this.baseDir, filePath).getParentFile());
    }
/*
    private void deleteEmptyParentDirectories(File directory) {
        while (directory != null && !directory.equals(new File(String.valueOf(baseDir)))) {
            File[] files = directory.listFiles();
            if (files != null && files.length == 0) {
                directory.delete();
                directory = directory.getParentFile();
            } else {
                break;
            }
        }
    }
 */
    private String findDirectory(URI uri) {
        String uriPath = uri.getPath();
        if(uri.getScheme() != null){
            uriPath = uriPath.replace(uri.getScheme(), "");
        }
        // Replace illegal characters in file names with underscores
       // String fileName = uriPath.replaceAll("[<>:\"|?*@ ]", "_") + ".json";

        uriPath = uriPath.replace("/", File.separator);

        // Construct the full directory path excluding the file name
        String directoryPath = uri.getHost() + uriPath;

        // Construct the full directory path including the file name
        String fullDirectoryPath = baseDir.getPath() + File.separator + directoryPath + ".json";

        return fullDirectoryPath;
    }
}