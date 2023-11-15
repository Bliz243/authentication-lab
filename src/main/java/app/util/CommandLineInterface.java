package app.util;

import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import app.server.interfaces.IPrintServer;

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
    private String tokenToUse = null;

    private void print(String message) {
        System.out.println(message);
    }

    public CommandLineInterface(IPrintServer printServer) {

        // Print server commands
        commandsMap.put("start", (token, args) -> {
            print(printServer.start(token));
        });
        commandsMap.put("stop", (token, args) -> {
            print(printServer.stop(token));
        });
        commandsMap.put("restart", (token, args) -> {
            try {
                print(printServer.restart(token));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("print", (token, args) -> {
            print(printServer.print(args[0], args[1], token));
        });
        commandsMap.put("authenticate", (token, args) -> {
            String result = printServer.authenticateUser(args[0], args[1]);
            String[] parts = result.split("-");
            setToken(parts[0]);
            result = parts[1];
            System.out.println(result);
        });
        commandsMap.put("createUser", (token, args) -> {
            try {
                print(printServer.createUser(args[0], args[1], token));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("updatePassword", (token, args) -> {
            try {
                print(printServer.createUser(args[0], args[1], token));
                String result = printServer.updatePassword(args[0], args[1], token);
                System.out.println(result);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        commandsMap.put("topQueue", (token, args) -> {
            print(printServer.topQueue(args[0], Integer.parseInt(args[1]), token));
        });
        commandsMap.put("queue", (token, args) -> {
            print(printServer.queue(args[0], token));
        });
        commandsMap.put("addToQueue", (token, args) -> {
            print(printServer.addToQueue(args[0], args[1], token));
        });
        commandsMap.put("status", (token, args) -> {
            print(printServer.status(args[0], token));
        });
        commandsMap.put("readConfig", (token, args) -> {
            print(printServer.readConfig(args[0], token));
        });
        commandsMap.put("setConfig", (token, args) -> {
            print(printServer.setConfig(args[0], args[1], token));
        });
        commandsMap.put("logout", (token, args) -> {
            print(printServer.logout(token));
            tokenToUse = null;
        });
        commandsMap.put("help", (token, args) -> {
            print(printServer.printCommands(token));
        });
        commandsMap.put("updateUserRole", (token, args) -> {
            print(printServer.updateUserPermissions(args[0], args[1], token));
        });
        commandsMap.put("addUserCommand", (token, args) -> {
            print(printServer.addUserToCommand(args[0], args[1], token));
        });
        commandsMap.put("removeUserCommand", (token, args) -> {
            print(printServer.removeUserFromCommand(args[0], args[1], token));
        });

    }

    public void executeCommand(String command, String... args) {
        Command action = commandsMap.get(command);
        if (action != null) {
            try {
                action.execute(tokenToUse, args);
            } catch (RemoteException e) {
                System.err.println("Error executing command: " + Color.red(e.getMessage()) + "\n");
            }
        } else {
            System.out.println(Color.yellow("Unknown command.\n"));
        }
    }

    public String getToken() {
        return tokenToUse;
    }

    public void setToken(String newToken) {
        tokenToUse = newToken;
    }

}