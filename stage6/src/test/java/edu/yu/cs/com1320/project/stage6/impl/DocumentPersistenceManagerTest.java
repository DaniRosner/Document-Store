package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentPersistenceManagerTest {
    File myDir = new File(System.getProperty("user.dir"));
    DocumentPersistenceManager manager = new DocumentPersistenceManager(myDir);
    URI documentURI = URI.create("http://A/hellp/document24");
    byte[] binaryData = new byte[] { 1, 2, 3, 4, 5 };
    String binaryDataString = "as";
    String docTxt = "This is a test dwawdbfoiagehowgspof lsfoiw hoiqho ho3hr opi hqoifhwoihro q[3r hqoFH 3OI;qg";
    Document document = new DocumentImpl(documentURI, docTxt, null);
    Document binaryDocument = new DocumentImpl(documentURI, binaryData);
    Document binaryDocument2 = new DocumentImpl(documentURI, binaryDataString.getBytes());
    URI testURI = URI.create("A");

    @Test
    void serialize() throws IOException {
        manager.serialize(documentURI, document);
    }

    @Test
    void deserialize() throws IOException {
        manager.serialize(documentURI, document);
        Document doc = manager.deserialize(documentURI);
        assertEquals("This is a test dwawdbfoiagehowgspof lsfoiw hoiqho ho3hr opi hqoifhwoihro q[3r hqoFH 3OI;qg", doc.getDocumentTxt());
    }

    @Test
    void serializeBinaryString() throws IOException {
        manager.serialize(documentURI, binaryDocument2);
        byte[] data = binaryDocument2.getDocumentBinaryData();
        Document doc = manager.deserialize(documentURI);
        assertArrayEquals(data, doc.getDocumentBinaryData());
    }


    @Test
    void delete() throws IOException {
        manager.serialize(documentURI, document);
        manager.delete(documentURI);
    }

    @Test
    void deleteOnDocNotOnDisk() throws IOException {
        assertFalse(manager.delete(testURI));
    }
}