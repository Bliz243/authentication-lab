package app.client;

import java.net.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Logger;

import app.server.interfaces.IPrintServer;
import app.util.CommandLineInterface;

public class PrintClient {

    private static final Logger logger = Logger.getLogger(PrintClient.class.getName());

    public static void main(String[] args) throws RemoteException {
        try {

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
                try {
                    cli.executeCommand(command, commandArgs);
                } catch (IndexOutOfBoundsException e) {
                    logger.info("You need to input arguments for this command.");
                } catch (NumberFormatException e) {
                    logger.info("Invalid job");
                }
            }

            scanner.close();

        } catch (RemoteException e) {
            logger.info("RMI server not running");
        } catch (Exception e) {
            logger.severe("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
