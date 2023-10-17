package app.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import app.server.IPrintServer;
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

    public CommandLineInterface(IPrintServer printServer) {

        // Print server commands
        commandsMap.put("start", args -> printServer.start());
        commandsMap.put("stop", args -> printServer.stop());
        commandsMap.put("restart", args -> printServer.restart());
        commandsMap.put("print", args -> printServer.print(args[0], args[1]));
        commandsMap.put("topQueue", args -> printServer.topQueue(args[0], Integer.parseInt(args[1])));
        commandsMap.put("queue", args -> {
            String result = printServer.queue(args[0]);
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
        commandsMap.put("help", args -> printServer.printCommands());


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