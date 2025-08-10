package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import edu.yu.cs.com1320.project.stage6.DocumentStore;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class DocumentStoreImplTest {
    URI uri1;
    URI uri2;
    URI uri3;
    URI uri4;
    URI uri5;
    String txt1;
    String txt2;
    String txt3;
    String txt4;
    String txt5;
    int bytes1;
    int bytes2;
    int bytes3;
    int bytes4;
    int bytes5;
    File baseDir;
    File otherDir;
    File[] filesInDir;
    File fullPath;
    List<File> filesToDelete = new ArrayList<>();

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "this text text text doc1 plain text string computer headphones";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "text text text doc2 plain string";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "this is the text text of doc3";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
        this.txt4 = "this is the text of doc4";

        this.uri5 = new URI("http://edu.yu.cs/com1320/project/doc5");
        this.txt5 = "this is the words of doc5";

        this.bytes1 = this.txt1.getBytes().length;
        this.bytes2 = this.txt2.getBytes().length;
        this.bytes3 = this.txt3.getBytes().length;
        this.bytes4 = this.txt4.getBytes().length;
        this.bytes5 = this.txt5.getBytes().length;

        //create baseDir
        this.baseDir = new File(System.getProperty("user.dir"));
        this.fullPath = new File(this.baseDir, "edu.yu.cs" + File.separator + "com1320" + File.separator + "project");
        this.filesInDir = this.fullPath.listFiles();
        //create otherDir
        this.otherDir = Files.createTempDirectory("stage6").toFile();


    }

    @BeforeEach
    public void preCleanup() {
        for (File file : filesToDelete) {
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("Deleted " + file.getAbsolutePath() + ": " + deleted);
            }
        }
        filesToDelete.clear();
    }

    @AfterEach
    public void postCleanup() {
        for (File file : filesToDelete) {
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("Deleted " + file.getAbsolutePath() + ": " + deleted);
            }
        }
        filesToDelete.clear();
    }

    @Test
    @Order(1)
    void putOverMaxDocs() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(2)
    void setMetadataDocOnDisk() throws IOException, InterruptedException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.setMetadata(uri1, "MetaData Key", "Value");
        //doc2 should be on disk and doc1 should be in memory
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        assertEquals("Value", store.getMetadata(uri1, "MetaData Key"));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(3)
    void getMetadataDocOnDisk() throws IOException, InterruptedException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.setMetadata(uri1, "MetaData Key", "Value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        assertEquals("Value", store.getMetadata(uri1, "MetaData Key"));
        //doc2 should be on disk and doc1 should be in memory
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(4)
    void putAndGetTXT() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        assertEquals(0, store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT));
        Document doc1 = store.get(uri1);
        assertEquals(doc1, store.get(uri1));
        assertEquals(0,store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT));
        Document doc2 = store.get(uri2);
        assertEquals(doc2, store.get(uri2));
        assertEquals(0,store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT));
        Document doc3 = store.get(uri3);
        assertEquals(doc3, store.get(uri3));
        assertEquals(0,store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT));
        Document doc4 = store.get(uri4);
        assertEquals(doc4, store.get(uri4));
    }

    @Test
    @Order(5)
    void putAndGetBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        assertEquals(0, store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY));
        Document doc1 = store.get(uri1);
        assertEquals(doc1, store.get(uri1));
        assertEquals(0,store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY));
        Document doc2 = store.get(uri2);
        assertEquals(doc2, store.get(uri2));
        assertEquals(0,store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY));
        Document doc3 = store.get(uri3);
        assertEquals(doc3, store.get(uri3));
        assertEquals(0,store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY));
        Document doc4 = store.get(uri4);
        assertEquals(doc4, store.get(uri4));
    }

    @Test
    @Order(6)
    void getOnDocOnDisk() throws IOException, InterruptedException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        assertTrue(doc1.equals(store.get(uri1)));
        //doc2 should be on disk and doc 1 should be in memory
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(7)
    void deleteBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        assertEquals(0, store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY));
        assertTrue(store.delete(uri1));
        assertNull(store.get(uri1));
    }

    @Test
    @Order(8)
    void deleteTXT() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        assertEquals(0, store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT));
        assertTrue(store.delete(uri1));
        assertNull(store.get(uri1));
    }

    @Test
    @Order(9)
    void deleteOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        //deleting it deletes from Disk, path should be gone
        assertTrue(store.delete(uri1));
        assertFalse(getClearURIPath(uri1).exists());
        assertNull(store.get(uri1));
    }

    @Test
    @Order(10)
    void putNewDocOnDocOnDisk() throws IOException, InterruptedException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.setMetadata(uri1, "MetaData Key", "Value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        //doc2 should be on disk and doc1 should be in memory
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(11)
    void undo() {
    }

    @Test
    @Order(12)
    void testUndo() {
    }

    @Test
    @Order(13)
    void search() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        List<Document> docs = store.search("text");
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
    }

    @Test
    @Order(14)
    void searchWithDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.search("text");
        assertTrue(getClearURIPath(uri1).exists());
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
        assertTrue(getClearURIPath(uri1).exists());
        assertTrue(docs.size() == 4);
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(15)
    void searchByPrefix() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        List<Document> docs = store.searchByPrefix("te");
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
    }

    @Test
    @Order(16)
    void searchByPrefixWithDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByPrefix("te");
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
        assertTrue(getClearURIPath(uri1).exists());
        assertTrue(docs.size() == 4);
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(17)
    void deleteAll() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        Set<URI> deletedURIs= store.deleteAll("text");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        List<Document> docs = store.search("text");
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
    }

    @Test
    @Order(18)
    void undoDeleteAll() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        Set<URI> deletedURIs= store.deleteAll("text");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        List<Document> docs = store.search("text");
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
    }

    @Test
    @Order(19)
    void deleteAllWithDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAll("text");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertTrue(deletedURIs.size() == 4);
        assertFalse(getClearURIPath(uri1).exists());
        List<Document> docs = store.search("text");
        assertTrue(docs.isEmpty());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(20)
    void undoDeleteAllWithDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAll("text");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertTrue(deletedURIs.size() == 4);
        assertFalse(getClearURIPath(uri1).exists());
        List<Document> docs = store.search("text");
        assertTrue(docs.isEmpty());
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(21)
    void deleteAllWithPrefix() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        Set<URI> deletedURIs = store.deleteAllWithPrefix("te");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertTrue(deletedURIs.size() == 4);
        List<Document> docs = store.searchByPrefix("te");
        assertTrue(docs.isEmpty());
    }

    @Test
    @Order(22)
    void undoDeleteAllWithPrefix() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        Set<URI> deletedURIs = store.deleteAllWithPrefix("te");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertTrue(deletedURIs.size() == 4);
        List<Document> docs = store.searchByPrefix("te");
        assertTrue(docs.isEmpty());
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
    }

    @Test
    @Order(23)
    void deleteAllWithPrefixWithDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithPrefix("te");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(doc1.getKey()));
        assertTrue(deletedURIs.contains(doc2.getKey()));
        assertTrue(deletedURIs.contains(doc3.getKey()));
        assertTrue(deletedURIs.contains(doc4.getKey()));
        assertFalse(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByPrefix("te");
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
    }

    @Test
    @Order(24)
    void undoDeleteAllWithPrefixWithDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithPrefix("te");
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertTrue(deletedURIs.contains(doc1.getKey()));
        assertTrue(deletedURIs.contains(doc2.getKey()));
        assertTrue(deletedURIs.contains(doc3.getKey()));
        assertTrue(deletedURIs.contains(doc4.getKey()));
        assertFalse(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByPrefix("te");
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(25)
    void searchByMetadata() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc1.setMetadata(metadata);
        doc2.setMetadata(metadata);
        doc3.setMetadata(metadata);
        doc4.setMetadata(metadata);
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.contains(doc1));
        assertTrue(docs.contains(doc2));
        assertTrue(docs.contains(doc3));
        assertTrue(docs.contains(doc4));
        assertTrue(docs.size() == 4);
    }

    @Test
    @Order(26)
    void searchByMetadataWithDocOnDisk() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.contains(doc1));
        assertTrue(docs.contains(doc2));
        assertTrue(docs.contains(doc3));
        assertTrue(docs.contains(doc4));
        assertTrue(docs.size() == 4);
        assertTrue(getClearURIPath(uri4).exists());
        filesToDelete.add(getClearURIPath(uri4));
    }

    @Test
    @Order(27)
    void searchByKeywordAndMetadata() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc1.setMetadata(metadata);
        doc2.setMetadata(metadata);
        doc3.setMetadata(metadata);
        doc4.setMetadata(metadata);
        List<Document> docs = store.searchByKeywordAndMetadata("text", metadata);
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
        assertTrue(docs.size() == 4);
    }

    @Test
    @Order(28)
    void searchByKeywordAndMetadataWithDocOnDisk() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByKeywordAndMetadata("text", metadata);
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
        assertTrue(getClearURIPath(uri1).exists());
        assertTrue(docs.size() == 4);
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(29)
    void searchByPrefixAndMetadata() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc1.setMetadata(metadata);
        doc2.setMetadata(metadata);
        doc3.setMetadata(metadata);
        doc4.setMetadata(metadata);
        List<Document> docs = store.searchByPrefixAndMetadata("te", metadata);
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
        assertTrue(docs.size() == 4);
    }

    @Test
    @Order(30)
    void searchByPrefixAndMetadataWithDocOnDisk() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByPrefixAndMetadata("te", metadata);
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(docs.get(3).equals(doc4));
        assertTrue(getClearURIPath(uri1).exists());
        assertTrue(docs.size() == 4);
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(31)
    void deleteAllWithMetadata() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
    }

    @Test
    @Order(32)
    void undoDeleteAllWithMetadata() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
    }

    @Test
    @Order(33)
    void deleteAllWithMetadataWithDocOnDisk() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(34)
    void undoDeleteAllWithMetadataWithDocOnDisk() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
        assertFalse(getClearURIPath(uri1).exists());
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(35)
    void deleteAllWithKeywordAndMetadata() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc1.setMetadata(metadata);
        doc2.setMetadata(metadata);
        doc3.setMetadata(metadata);
        doc4.setMetadata(metadata);
        Set<URI> deletedURIs = store.deleteAllWithKeywordAndMetadata("text", metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        List<Document> docs = store.searchByKeywordAndMetadata("text", metadata);
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
    }

    @Test
    @Order(36)
    void deleteAllWithKeywordAndMetadataWithDocOnDisk() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithKeywordAndMetadata("text", metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        assertFalse(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByKeywordAndMetadata("text", metadata);
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
    }

    @Test
    @Order(37)
    void deleteAllWithPrefixAndMetadata() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc1.setMetadata(metadata);
        doc2.setMetadata(metadata);
        doc3.setMetadata(metadata);
        doc4.setMetadata(metadata);
        Set<URI> deletedURIs = store.deleteAllWithPrefixAndMetadata("te", metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        List<Document> docs = store.searchByPrefixAndMetadata("te", metadata);
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
    }

    @Test
    @Order(38)
    void deleteAllWithPrefixAndMetadataWithDocOnDisk() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithPrefixAndMetadata("te", metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertNull(store.get(uri4));
        List<Document> docs = store.searchByPrefixAndMetadata("te", metadata);
        assertTrue(docs.isEmpty());
        assertTrue(deletedURIs.size() == 4);
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(39)
    void put() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        assertEquals(doc1, store.get(uri1));
    }

    @Test
    @Order(40)
    void putThatOverwritesInMemory() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri1);
        assertEquals(doc2, store.get(uri1));
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(41)
    void putThatOverwritesOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc with uri1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        //doc with uri1 should be in memory & doc with uri2 should be on disk
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        Document doc3 = store.get(uri1);
        assertEquals(doc3, store.get(uri1));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(42)
    void undoPut() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        assertEquals(doc1, store.get(uri1));
        store.undo();
        assertNull(store.get(uri1));
    }

    @Test
    @Order(43)
    void undoPutThatOverwritesInMemory() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri1);
        assertEquals(doc2, store.get(uri1));
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(44)
    void undoPutThatOverwritesOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc with uri1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        //doc with uri1 should be in memory & doc with uri2 should be on disk
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        Document doc3 = store.get(uri1);
        assertEquals(doc3, store.get(uri1));
        store.undo();
        assertEquals(doc1, store.get(uri1));
        filesToDelete.add(getClearURIPath(uri2));
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(45)
    void undoPutThatPushesADocToDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.undo();
        //doc2 should be deleted and doc 1 can still be on disk as per piazza @239
        assertNull(store.get(uri2));
        assertTrue(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(46)
    void undoURIPutOnDocInDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.undo(uri1);
        //doc1 should no longer be on disk or in memory
        assertFalse(getClearURIPath(uri1).exists());
        assertNull(store.get(uri1));
    }

    @Test
    @Order(47)
    void setMetadata() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(48)
    void overwriteMetadata() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
        store.setMetadata(uri1, "key", "value2");
        assertEquals("value2", store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(49)
    void setMetadataOnDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(getClearURIPath(uri1).exists());
        store.setMetadata(uri1, "key", "value");
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(50)
    void undoSetMetadata() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
        store.undo();
        assertNull(store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(51)
    void undoOverwriteMetadata() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
        store.setMetadata(uri1, "key", "value2");
        assertEquals("value2", store.getMetadata(uri1, "key"));
        store.undo();
        assertEquals("value", store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(52)
    void undoSetMetadataOnDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(getClearURIPath(uri1).exists());
        store.setMetadata(uri1, "key", "value");
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        assertNull(store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(53)
    void undoSetMetadataAfterDocWasPushedToDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1, "key", "value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        //doc with uri1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.undo(uri1);
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        assertNull(store.getMetadata(uri1, "key"));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(54)
    void getMetadataFromDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.setMetadata(uri1, "key", "value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(getClearURIPath(uri1).exists());
        store.getMetadata(uri1, "key");
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(55)
    void delete() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc = store.get(uri1);
        store.delete(uri1);
        assertNull(store.get(uri1));
    }

    @Test
    @Order(56)
    void undoDelete() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.delete(uri1);
        assertNull(store.get(uri1));
        store.undo();
        assertEquals(doc1, store.get(uri1));
    }

    @Test
    @Order(57)
    void undoDeleteOnDocOnDisk() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(getClearURIPath(uri1).exists());
        store.delete(uri1);
        assertNull(store.get(uri1));
        assertFalse(getClearURIPath(uri1).exists());
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        assertEquals(doc1, store.get(uri1));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(58)
    void setMaxDocumentCount() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(59)
    void setMaxDocumentBytes() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(122);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        assertTrue(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(60)
    void stackSize() {
    }

    @Test
    @Order(61)
    void searchWhereNotAllContainWord() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.search("text");
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri5).exists());
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(getClearURIPath(uri5).exists());
        assertTrue(docs.size() == 3);
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(62)
    void searchByPrefixWhereNotAllContainWord() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByPrefix("te");
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri5).exists());
        assertTrue(docs.get(0).equals(doc1));
        assertTrue(docs.get(1).equals(doc2));
        assertTrue(docs.get(2).equals(doc3));
        assertTrue(getClearURIPath(uri5).exists());
        assertTrue(docs.size() == 3);
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(63)
    void searchByMetadataWhereNotAllContain() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.contains(doc1));
        assertTrue(docs.contains(doc2));
        assertTrue(docs.contains(doc3));
        assertFalse(docs.contains(doc4));
        assertTrue(docs.size() == 3);
        assertTrue(getClearURIPath(uri4).exists());
        filesToDelete.add(getClearURIPath(uri4));
    }

    @Test
    @Order(64)
    void deleteAllWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAll("text");
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertEquals(doc5, store.get(uri5));
    }

    @Test
    @Order(65)
    void undoDeleteAllWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAll("text");
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertEquals(doc5, store.get(uri5));
        assertFalse(getClearURIPath(uri1).exists());
        store.undo();
        assertTrue(getClearURIPath(uri5).exists());
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(66)
    void deleteAllByPrefixWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithPrefix("te");
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertEquals(doc5, store.get(uri5));
    }

    @Test
    @Order(67)
    void undoDeleteAllByPrefixWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithPrefix("te");
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        assertEquals(doc5, store.get(uri5));
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri5).exists());
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(68)
    void deleteAllByMetaData() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithMetadata(metadata);
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertEquals(doc5, store.get(uri5));
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
    }

    @Test
    @Order(69)
    void undoDeleteAllByMetaData() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithMetadata(metadata);
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertEquals(doc5, store.get(uri5));
        assertNull(store.get(uri1));
        assertNull(store.get(uri2));
        assertNull(store.get(uri3));
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri5).exists());
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(70)
    void undoDeleteAllWithKeyWorkAndMetaDataWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2"));
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithKeywordAndMetadata("text",metadata);
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertEquals(doc5, store.get(uri5));
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri5).exists());
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(70)
    void deleteAllWithKeyWorkAndMetaDataWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2"));
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithKeywordAndMetadata("text",metadata);
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertEquals(doc5, store.get(uri5));
    }

    @Test
    @Order(70)
    void deleteAllWithPrefixAndMetaDataWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2"));
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithPrefixAndMetadata("te",metadata);
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertEquals(doc5, store.get(uri5));
    }

    @Test
    @Order(70)
    void undoDeleteAllWithPrefixAndMetaDataWhereNotAllContain() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2"));
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
        Document doc5 = store.get(uri5);
        assertTrue(getClearURIPath(uri1).exists());
        store.deleteAllWithPrefixAndMetadata("te",metadata);
        assertFalse(getClearURIPath(uri1).exists());
        assertFalse(getClearURIPath(uri5).exists());
        assertEquals(doc5, store.get(uri5));
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri5).exists());
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        filesToDelete.add(getClearURIPath(uri5));
    }

    @Test
    @Order(71)
    void searchByMetadataBINARY() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        doc1.setMetadata(metadata);
        doc2.setMetadata(metadata);
        doc3.setMetadata(metadata);
        doc4.setMetadata(metadata);
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.contains(doc1));
        assertTrue(docs.contains(doc2));
        assertTrue(docs.contains(doc3));
        assertTrue(docs.contains(doc4));
        assertTrue(docs.size() == 4);
    }

    @Test
    @Order(72)
    void searchByMetadataWithDocOnDiskBINARY() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.contains(doc1));
        assertTrue(docs.contains(doc2));
        assertTrue(docs.contains(doc3));
        assertTrue(docs.contains(doc4));
        assertTrue(docs.size() == 4);
        assertTrue(getClearURIPath(uri4).exists());
        filesToDelete.add(getClearURIPath(uri4));
    }

    @Test
    @Order(73)
    void deleteAllWithMetadataBINARY() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
    }

    @Test
    @Order(74)
    void undoDeleteAllWithMetadataBINARY() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
    }

    @Test
    @Order(75)
    void deleteAllWithMetadataWithDocOnDiskBINARY() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(76)
    void undoDeleteAllWithMetadataWithDocOnDiskBINARY() throws IOException {
        HashMap metadata = new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        doc1.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        assertTrue(getClearURIPath(uri1).exists());
        Set<URI> deletedURIs = store.deleteAllWithMetadata(metadata);
        assertTrue(deletedURIs.contains(uri1));
        assertTrue(deletedURIs.contains(uri2));
        assertTrue(deletedURIs.contains(uri3));
        assertTrue(deletedURIs.contains(uri4));
        assertEquals(4, deletedURIs.size());
        List<Document> docs = store.searchByMetadata(metadata);
        assertTrue(docs.isEmpty());
        assertFalse(getClearURIPath(uri1).exists());
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertEquals(doc2, store.get(uri2));
        assertEquals(doc3, store.get(uri3));
        assertEquals(doc4, store.get(uri4));
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(77)
    void putBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        assertEquals(doc1, store.get(uri1));
    }

    @Test
    @Order(78)
    void putThatOverwritesInMemoryBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri1);
        assertEquals(doc2, store.get(uri1));
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(79)
    void putThatOverwritesOnDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        //doc with uri1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        //doc with uri1 should be in memory & doc with uri2 should be on disk
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        Document doc3 = store.get(uri1);
        assertEquals(doc3, store.get(uri1));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(80)
    void undoPutBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        assertEquals(doc1, store.get(uri1));
        store.undo();
        assertNull(store.get(uri1));
    }

    @Test
    @Order(81)
    void undoPutThatOverwritesInMemoryBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri1);
        assertEquals(doc2, store.get(uri1));
        store.undo();
        assertEquals(doc1, store.get(uri1));
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(82)
    void undoPutThatOverwritesOnDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        //doc with uri1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        //doc with uri1 should be in memory & doc with uri2 should be on disk
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        Document doc3 = store.get(uri1);
        assertEquals(doc3, store.get(uri1));
        store.undo();
        assertEquals(doc1, store.get(uri1));
        filesToDelete.add(getClearURIPath(uri2));
        assertFalse(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(83)
    void undoPutThatPushesADocToDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.undo();
        //doc2 should be deleted and doc 1 can still be on disk as per piazza @239
        assertNull(store.get(uri2));
        assertTrue(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(84)
    void undoURIPutOnDocInDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.undo(uri1);
        //doc1 should no longer be on disk or in memory
        assertFalse(getClearURIPath(uri1).exists());
        assertNull(store.get(uri1));
    }

    @Test
    @Order(85)
    void setMetadataBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(86)
    void overwriteMetadataBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
        store.setMetadata(uri1, "key", "value2");
        assertEquals("value2", store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(87)
    void setMetadataOnDocOnDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        store.setMetadata(uri1, "key", "value");
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(88)
    void undoSetMetadataBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
        store.undo();
        assertNull(store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(89)
    void undoOverwriteMetadataBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.setMetadata(uri1, "key", "value");
        assertEquals("value", store.getMetadata(uri1, "key"));
        store.setMetadata(uri1, "key", "value2");
        assertEquals("value2", store.getMetadata(uri1, "key"));
        store.undo();
        assertEquals("value", store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(90)
    void undoSetMetadataOnDocOnDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        store.setMetadata(uri1, "key", "value");
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        assertNull(store.getMetadata(uri1, "key"));
    }

    @Test
    @Order(91)
    void undoSetMetadataAfterDocWasPushedToDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.setMetadata(uri1, "key", "value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        //doc with uri1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        store.undo(uri1);
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        assertNull(store.getMetadata(uri1, "key"));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(92)
    void getMetadataFromDocOnDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.setMetadata(uri1, "key", "value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        store.getMetadata(uri1, "key");
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(93)
    void undoDeleteBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.delete(uri1);
        assertNull(store.get(uri1));
        store.undo();
        assertEquals(doc1, store.get(uri1));
    }

    @Test
    @Order(94)
    void undoDeleteOnDocOnDiskBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        store.delete(uri1);
        assertNull(store.get(uri1));
        assertFalse(getClearURIPath(uri1).exists());
        store.undo();
        assertFalse(getClearURIPath(uri1).exists());
        assertTrue(getClearURIPath(uri2).exists());
        assertEquals(doc1, store.get(uri1));
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(95)
    void setMaxDocumentCountBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(2);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(96)
    void setMaxDocumentBytesBINARY() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(122);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        System.out.println(store.get(uri1).getDocumentBinaryData().length);
        System.out.println(store.get(uri2).getDocumentBinaryData().length);
        System.out.println(store.get(uri3).getDocumentBinaryData().length);
        assertTrue(getClearURIPath(uri1).exists());
    }

    @Test
    @Order(97)
    void getMetadataDocOnDiskMaxBytes() throws IOException, InterruptedException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(123);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        Document doc1 = store.get(uri1);
        store.setMetadata(uri1, "MetaData Key", "Value");
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        Document doc2 = store.get(uri2);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        Document doc3 = store.get(uri3);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        Document doc4 = store.get(uri4);
        //doc1 should be on disk
        assertTrue(getClearURIPath(uri1).exists());
        assertEquals("Value", store.getMetadata(uri1, "MetaData Key"));
        //doc2 should be on disk and doc1 should be in memory
        assertTrue(getClearURIPath(uri2).exists());
        assertFalse(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri2));
    }

    @Test
    @Order(98)
    void putOverMaxBytes() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentBytes(123);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(99)
    void testOtherDir() {
        System.out.println(this.otherDir.getAbsolutePath());
    }

    @Test
    @Order(100)
    void testOtherDirStore() throws IOException {
        DocumentStore store = new DocumentStoreImpl(otherDir);
        store.setMaxDocumentCount(1);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.BINARY);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.BINARY);
        assertTrue(getClearURIPathFromOtherDir(uri1).exists());
        filesToDelete.add(getClearURIPathFromOtherDir(uri1));
    }

    @Test
    @Order(101)
    void searchByPrefixAndMetadata2() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        List<Document> docs = store.searchByPrefixAndMetadata("te", metadata);
        assertTrue(docs.get(0).equals(doc2));
        assertTrue(docs.get(1).equals(doc3));
        assertTrue(docs.get(2).equals(doc4));
        assertTrue(docs.size() == 3);
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(101)
    void searchByKeywordAndMetadata2() throws IOException {
        HashMap metadata =new HashMap(Map.of("Key", "value", "Key2", "value2", "Key3", "value3"));
        DocumentStore store = new DocumentStoreImpl();
        store.setMaxDocumentCount(3);
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        Document doc1 = store.get(uri1);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        Document doc2 = store.get(uri2);
        doc2.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        Document doc3 = store.get(uri3);
        doc3.setMetadata(metadata);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        Document doc4 = store.get(uri4);
        doc4.setMetadata(metadata);
        List<Document> docs = store.searchByKeywordAndMetadata("text", metadata);
        assertTrue(docs.get(0).equals(doc2));
        assertTrue(docs.get(1).equals(doc3));
        assertTrue(docs.get(2).equals(doc4));
        assertTrue(docs.size() == 3);
        assertTrue(getClearURIPath(uri1).exists());
        filesToDelete.add(getClearURIPath(uri1));
    }

    @Test
    @Order(102)
    void undoNthPutByURI() throws IOException {
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
        store.put(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
        store.undo();
    }

//    @Test
//    @Order(103)
//    void

    private File getClearURIPath(URI uri) {
        //Get the path, clean it, and add .json
        String path = uri.getPath();
        String host = uri.getHost();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String cleanPath = path.replace("/", File.separator);
        cleanPath += ".json";
        File file = new File(baseDir, host + File.separator + cleanPath);
        return file;
    }

    private File getClearURIPathFromOtherDir(URI uri) {
        //Get the path, clean it, and add .json
        String path = uri.getPath();
        String host = uri.getHost();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String cleanPath = path.replace("/", File.separator);
        cleanPath += ".json";
        File file = new File(otherDir, host + File.separator + cleanPath);
        return file;
    }
}