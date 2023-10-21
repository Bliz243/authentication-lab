package app.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import app.server.IPrintServer;
import app.util.CommandLineInterface;
import app.util.ConfigManager;

public class PrintClient {

    private static final Logger logger = Logger.getLogger(PrintClient.class.getName());

    public static void main(String[] args) throws RemoteException {
        try {
            // IPrintServer printServer =(IPrintServer)Naming.lookup("rmi://localhost:6000/PrintServer");
            System.setProperty("javax.net.ssl.trustStore", ConfigManager.getInstance().getParameter("clientTrust"));
            System.setProperty("javax.net.ssl.trustStorePassword", "keystore");


            IPrintServer printServer = (IPrintServer) Naming.lookup("rmi://localhost:5000/PrintServer");
            logger.info("PrintServer is ready and waiting for client connections...");


            // Create CLI and start it
            CommandLineInterface cli = new CommandLineInterface(printServer);
            Scanner scanner = new Scanner(System.in);

            cli.executeCommand("help", "");
            while (true) {
                System.out.println("Enter command:");
                String fullCommand = scanner.nextLine();
                if ("exit".equalsIgnoreCase(fullCommand)) {
                    break;
                }
                String[] parts = fullCommand.split(" ");
                String command = parts[0];
                String[] commandArgs = new String[0];
                if (parts.length > 1) {
                    commandArgs = fullCommand.substring(command.length()).trim().split(" ");
                }
                cli.executeCommand(command, commandArgs);
            }

            scanner.close();

        } catch (Exception e) {
            logger.severe("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
