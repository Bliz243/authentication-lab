package app.server;

import app.auth.Authenticator;
import app.auth.PasswordStorage;
import app.auth.PasswordVerifier;
import app.log.AppLogger;
import app.util.ConfigManager;
import java.util.logging.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {

    private static final Logger logger = Logger.getLogger(PrintServer.class.getName());
    private boolean hasAuth = false;
    private static final PasswordStorage passwordStorage = new PasswordStorage();
    private static final PasswordVerifier passwordVerifier = new PasswordVerifier(passwordStorage);
    private static final Authenticator autheticator = new Authenticator(passwordVerifier);

    public PrintServer() throws RemoteException {
        super();
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        logger.info("Print request: " + filename + " on printer: " + printer);
    }

    @Override
    public String queue(String printer) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        System.out.println("Queue request for printer: " + printer);
        return "Queue for " + printer;
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        System.out.println("Move job: " + job + " to top of queue on printer: " + printer);
    }

    @Override
    public void start() throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        System.out.println("Print server started.");
    }

    @Override
    public void stop() throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        System.out.println("Print server stopped.");
    }

    @Override
    public void restart() throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        System.out.println("Print server restarted.");
    }

    @Override
    public String status(String printer) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        System.out.println("Status request for printer: " + printer);
        return "Status for " + printer;
    }

    @Override
    public String readConfig(String parameter) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        String value = ConfigManager.getInstance().getParameter(parameter); // adjusted line
        logger.info("Read config for parameter: " + parameter + " with value: " + value);
        return value;
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        ConfigManager.getInstance().setParameter(parameter, value); // adjusted line
        logger.info("Set config for parameter: " + parameter + " to value: " + value);
    }

    @Override
    public void printCommands() throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:\n");
        sb.append("start: Starts the print server.\n");
        sb.append("stop: Stops the print server.\n");
        sb.append("restart: Restarts the print server.\n");
        sb.append("print: Prints the file. Args: filename, printer\n");
        sb.append("topQueue: Moves job to top of queue. Args: printer, job\n");
        sb.append("queue: Shows print queue. Arg: printer\n");
        sb.append("status: Shows printer status. Arg: printer\n");
        sb.append("readConfig: Reads configuration. Arg: parameter\n");
        sb.append("setConfig: Sets configuration. Args: parameter, value\n");
        logger.info(sb.toString());
    }

    @Override
    public void authenticateUser(String user, String password) throws RemoteException {
        int triesForLogin = 0;
        if (autheticator.authenticate(user, password)) {
            hasAuth = true;
            logger.info("Login succesful" + "\nWelcome  " + user);

        }
        if (triesForLogin == 3){
            logger.info("Sorry bitch, i think you're trying to DDOS our PrintServer \n\n You're getting IP banned.... Sit down");
        }
        triesForLogin++;
        logger.info("Login failed...");

    }

    @Override
    public void createUser(String newUser, String password) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }

        passwordStorage.updatePassword(newUser, password);
        logger.info("Created User: " + newUser);

    };

    @Override
    public void updatePassword(String user, String password) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        passwordStorage.updatePassword(user, password);
    };


    @Override
    public void logout() throws RemoteException {
        hasAuth = false;
    };

}
