package app.server;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;

class PrintServerTest {

    private PrintServer printServer;

    @BeforeEach
    void setUp() throws Exception {
        printServer = new PrintServer();
    }

    @Test
    void print() {
        // Assume a successful print request
        assertDoesNotThrow(() -> printServer.print("file.txt", "printer1"));
    }

    @Test
    void queue() throws RemoteException {
        String response = printServer.queue("printer1");
        assertEquals("Queue for printer1", response);
    }

    @Test
    void topQueue() {
        // Assume a successful request
        assertDoesNotThrow(() -> printServer.topQueue("printer1", 1));
    }

    @Test
    void start() {
        // Assume a successful request
        assertDoesNotThrow(() -> printServer.start());
    }

    @Test
    void stop() {
        // Assume a successful request
        assertDoesNotThrow(() -> printServer.stop());
    }

    @Test
    void restart() {
        // Assume a successful request
        assertDoesNotThrow(() -> printServer.restart());
    }

    @Test
    void status() throws RemoteException {
        String response = printServer.status("printer1");
        assertEquals("Status for printer1", response);
    }

    @Test
    void readConfig() throws RemoteException {
        String response = printServer.readConfig("parameter1");
        assertEquals("Value for parameter1", response);
    }

    @Test
    void setConfig() {
        // Assume a successful request
        assertDoesNotThrow(() -> printServer.setConfig("parameter1", "value1"));
    }
}