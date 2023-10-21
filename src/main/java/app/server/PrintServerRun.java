package app.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

    public class PrintServerRun {

        private static final Logger logger = Logger.getLogger(PrintServer.class.getName());

        public static void main(String args[]){
            try{
                /* System.setProperty("javax.net.ssl.keyStore", ConfigManager.getInstance().getParameter("keyStore") );
                System.setProperty("javax.net.ssl.keyStorePassword", "keystore");

                SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
                SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory(); */

                IPrintServer printServer = new PrintServer();
                UnicastRemoteObject.unexportObject(printServer, true);
                IPrintServer stub = (IPrintServer) UnicastRemoteObject.exportObject(printServer, 0);

                LocateRegistry.createRegistry(5000);
                Naming.rebind("rmi://localhost:5000/PrintServer", stub);

                }   catch (Exception e) {
                    logger.severe("Server exception: " + e.toString());
                    e.printStackTrace();
                }
        }
    }