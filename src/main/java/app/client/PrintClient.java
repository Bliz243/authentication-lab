package app.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import app.server.PrintServer;
import app.util.CommandLineInterface;

public class PrintClient {

    private static final Logger logger = Logger.getLogger(PrintClient.class.getName());

    public static void main(String[] args) throws RemoteException {
        try {
            PrintServer printServer = new PrintServer();

            LocateRegistry.createRegistry(1099);

            Naming.rebind("rmi://localhost:1099/PrintServer", printServer);

            logger.info("PrintServer is ready and waiting for client connections...");

            // Create CLI and start it
            CommandLineInterface cli = new CommandLineInterface(printServer);
            Scanner scanner = new Scanner(System.in);

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
