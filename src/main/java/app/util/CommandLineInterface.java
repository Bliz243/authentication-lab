package app.util;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import app.client.PrintClient;
import app.server.IPrintServer;

public class CommandLineInterface {

    @FunctionalInterface
    interface Command {
        void execute(String token, String... args) throws RemoteException;
    }

    @FunctionalInterface
    interface CommandWithReturn {
        String execute(String token, String... args) throws RemoteException;
    }

    private final Map<String, Command> commandsMap = new HashMap<>();

    public CommandLineInterface(IPrintServer printServer) {

        // Print server commands
        commandsMap.put("start", (token, args) -> printServer.start(token));
        commandsMap.put("stop", (token, args) -> printServer.stop(token));
        commandsMap.put("restart", (token, args) -> {
            try {
                printServer.restart(token);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("print", (token, args) -> printServer.print(args[0], args[1], token));
        commandsMap.put("authenticate", (token, args) -> {
            String result = printServer.authenticateUser(args[0], args[1]);
            if(result.startsWith("Login succesful")) {
                String[] parts = result.split(" ");
                PrintClient.setToken(parts[parts.length - 1]);
                result = result.substring(0, result.lastIndexOf(" "));
            }
            System.out.println(result);
        });
        commandsMap.put("createUser", (token, args) -> {
            String result;
            try {

                result = printServer.createUser(args[0], args[1], token);
            System.out.println(result);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("updatePassword", (token, args) -> {
            String result;
            try {
                result = printServer.updatePassword(args[0], args[1], token);
                System.out.println(result);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("topQueue", (token, args) -> {
            String result = printServer.topQueue(args[0], Integer.parseInt(args[1]), token);
            System.out.println(result);
        });
        commandsMap.put("queue", (token, args) -> {
            String result = printServer.queue(args[0], token);
            System.out.println(result);
        });
        commandsMap.put("addToQueue", (token, args) -> {
            String result = printServer.addToQueue(args[0], args[1], token);
            System.out.println(result);
        });
        commandsMap.put("status", (token, args) -> {
            String result = printServer.status(args[0], token);
            System.out.println(result);
        });
        commandsMap.put("readConfig", (token, args) -> {
            String result = printServer.readConfig(args[0], token);
            System.out.println(result);
        });
        commandsMap.put("setConfig", (token, args) -> printServer.setConfig(args[0], args[1], token));
        commandsMap.put("logout", (token, args) -> {
            String result = printServer.logout(token);
            System.out.println(result);
        });
        commandsMap.put("help", (token, args) -> {
            String result = printServer.printCommands(token);
            System.out.println(result);
        });
        // User commands

    }

    public void executeCommand(String command, String token, String... args) {
        Command action = commandsMap.get(command);
        if (action != null) {
            try {
                action.execute(token, args);
            } catch (RemoteException e) {
                System.err.println("Error executing command: " + e.getMessage());
            }
        } else {
            System.out.println("Unknown command.");
        }
    }

}