package com.eci;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class SimpleWebServerTest {
    private ExecutorService serverExecutor;

    @BeforeEach
    public void setUp() {
        SimpleWebServer.services.clear();
        SimpleWebServer.WEB_ROOT = "target/";
        // Optionally start the server if needed for integration tests
    }

    @Test
    public void testStaticFiles() {
        SimpleWebServer.staticfiles("classes/webroot");
        assertEquals("target/classes/webroot", SimpleWebServer.WEB_ROOT);
    }

    @Test
    public void testStaticFilesChangeLocation() {
        SimpleWebServer.staticfiles("classes/webroot");
        assertEquals("target/classes/webroot", SimpleWebServer.WEB_ROOT);

        SimpleWebServer.staticfiles("another/location");
        assertEquals("target/another/location", SimpleWebServer.WEB_ROOT);
    }

    @Test
    public void testStaticFilesEmptyPath() {
        SimpleWebServer.staticfiles("");
        assertEquals("target/", SimpleWebServer.WEB_ROOT);
    }
    
    @Test
    public void testStaticFilesAbsolutePath() {
        String absolutePath = "/absolute/path/to/webroot";
        SimpleWebServer.staticfiles(absolutePath);
        assertEquals("target/" + absolutePath, SimpleWebServer.WEB_ROOT);
    }
    
    @Test
    public void testStaticFilesPathWithSpecialCharacters() {
        SimpleWebServer.staticfiles("webroot@123");
        assertEquals("target/webroot@123", SimpleWebServer.WEB_ROOT);
    }
    
    @Test
    public void testGetService() {
        SimpleWebServer.get("/test", (req, res) -> "Test response");
        assertTrue(SimpleWebServer.services.containsKey("/test"));
    }

    @Test
    public void testNonExistentService() {
        assertFalse(SimpleWebServer.services.containsKey("/nonExistent"));
    }

    @Test
    public void testServiceResponse() {
        SimpleWebServer.get("/service", (req, res) -> "Service response");
        Request request = new Request("/service");
        String response = SimpleWebServer.services.get("/service").getValues(request, "");
        assertEquals("Service response", response);
    }

    @Test
    public void testStaticFilesSpecialCharacters() {
        SimpleWebServer.staticfiles("webroot@123");
        assertEquals("target/webroot@123", SimpleWebServer.WEB_ROOT);
    }

    @Test
    public void testStaticFilesChange() {
        SimpleWebServer.staticfiles("initial/path");
        assertEquals("target/initial/path", SimpleWebServer.WEB_ROOT);

        SimpleWebServer.staticfiles("new/path");
        assertEquals("target/new/path", SimpleWebServer.WEB_ROOT);
    }

}


