package app.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import app.server.PrintServer;

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
    private final PrintServer server;

    public CommandLineInterface(PrintServer server) {
        this.server = server;

        commandsMap.put("start", args -> server.start());
        commandsMap.put("stop", args -> server.stop());
        commandsMap.put("restart", args -> server.restart());
        commandsMap.put("print", args -> server.print(args[0], args[1])); // expects 2 arguments
        commandsMap.put("topQueue", args -> server.topQueue(args[0], Integer.parseInt(args[1]))); // expects 2 arguments

        // Commands with return values
        commandsMap.put("queue", args -> {
            String result = server.queue(args[0]); // expects 1 argument
            System.out.println(result);
        });
        commandsMap.put("status", args -> {
            String result = server.status(args[0]); // expects 1 argument
            System.out.println(result);
        });
        commandsMap.put("readConfig", args -> {
            String result = server.readConfig(args[0]); // expects 1 argument
            System.out.println(result);
        });

        // Commands with arguments and return values
        commandsMap.put("setConfig", args -> server.setConfig(args[0], args[1])); // expects 2 arguments

        commandsMap.put("help", args -> server.printCommands());

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