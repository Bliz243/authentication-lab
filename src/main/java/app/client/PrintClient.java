package app.client;

import app.log.AppLogger;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import app.server.PrintServer;
import org.apache.logging.log4j.Logger;

public class PrintClient {

    private static final Logger logger = AppLogger.getLogger(PrintClient.class);
    public static void main(String[] args) throws RemoteException {
        try {
            PrintServer printServer = new PrintServer();

            LocateRegistry.createRegistry(1099);

            Naming.rebind("rmi://localhost:1099/PrintServer", printServer);

            logger.info("PrintServer is ready and waiting for client connections...");

        } catch (Exception e) {
            logger.error("Server exception: {}", e.toString(), e);
        }
    }
}
