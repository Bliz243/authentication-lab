package app.util;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import app.server.IPrintServer;

public class CommandLineInterface {

    @FunctionalInterface
    interface Command {
        void execute(String... args) throws RemoteException;
    }

    @FunctionalInterface
    interface CommandWithReturn {
        String execute(String... args) throws RemoteException;
    }

    private final Map<String, Command> commandsMap = new HashMap<>();

    public CommandLineInterface(IPrintServer printServer) {

        // Print server commands
        commandsMap.put("start", args -> printServer.start());
        commandsMap.put("stop", args -> printServer.stop());
        commandsMap.put("restart", args -> {
            try {
                printServer.restart();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("print", args -> printServer.print(args[0], args[1]));
        commandsMap.put("authenticate", args -> {
            String result = printServer.authenticateUser(args[0], args[1]);
            System.out.println(result);
        });
        commandsMap.put("createUser", args -> {
            String result;
            try {
                result = printServer.createUser(args[0], args[1]);
            System.out.println(result);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("updatePassword", args -> {
            String result;
            try {
                result = printServer.updatePassword(args[0], args[1]);
                System.out.println(result);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("topQueue", args -> {
            String result = printServer.topQueue(args[0], Integer.parseInt(args[1]));
            System.out.println(result);
        });
        commandsMap.put("queue", args -> {
            String result = printServer.queue(args[0]);
            System.out.println(result);
        });
        commandsMap.put("addToQueue", args -> {
            String result = printServer.addToQueue(args[0], args[1]);
            System.out.println(result);
        });
        commandsMap.put("status", args -> {
            String result = printServer.status(args[0]);
            System.out.println(result);
        });
        commandsMap.put("readConfig", args -> {
            String result = printServer.readConfig(args[0]);
            System.out.println(result);
        });
        commandsMap.put("setConfig", args -> printServer.setConfig(args[0], args[1]));
        commandsMap.put("logout", args -> {
            String result = printServer.logout();
            System.out.println(result);
        });
        commandsMap.put("help", args -> {
            String result = printServer.printCommands();
            System.out.println(result);
        });
        // User commands

    }

    public void executeCommand(String command, String... args) {
        Command action = commandsMap.get(command);
        if (action != null) {
            try {
                action.execute(args);
            } catch (RemoteException e) {
                System.err.println("Error executing command: " + e.getMessage());
            }
        } else {
            System.out.println("Unknown command.");
        }
    }

}