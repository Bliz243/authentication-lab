package app.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Logger;

import app.server.PrintServer;

public class PrintClient {

    private static final Logger logger = Logger.getLogger(PrintClient.class.getName());

    public static void main(String[] args) throws RemoteException {
        try {
            PrintServer printServer = new PrintServer();

            LocateRegistry.createRegistry(1099);

            Naming.rebind("rmi://localhost:1099/PrintServer", printServer);

            logger.info("PrintServer is ready and waiting for client connections...");

        } catch (Exception e) {
            logger.severe("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
