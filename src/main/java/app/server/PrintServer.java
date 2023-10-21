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
    public String printCommands() throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable commands:\n");
        if (!hasAuth) {
            sb.append("authenticate <username> <password>: Authenticate user");
            sb.append("help: To see available commands");
            String helpCommands = sb.toString();
            return helpCommands;
        }
        sb.append("start: Starts the print server.\n");
        sb.append("stop: Stops the print server.\n");
        sb.append("restart: Restarts the print server.\n");
        sb.append("print <filename> <printer>: Prints the file.\n");
        sb.append("queue <printer>: Shows print queue. \n");
        sb.append("topQueue <printer> <job>: Moves job to top of queue.\n");
        sb.append("status <printer>: Shows printer status. \n");
        sb.append("readConfig <parameter>: Reads configuration.\n");
        sb.append("setConfig <paramter> <value>: Sets configuration.\n");
        sb.append("createUser <username> <password>: Creates a new user\n");
        sb.append("updateUser <username> <password>: Update user password\n");

        String helpCommands = sb.toString();
        logger.info("These are the commands available: \n\n" + helpCommands);
        return helpCommands;
    }

    @Override
    public String authenticateUser(String user, String password) throws RemoteException {
        int triesForLogin = 0;

        if (!hasAuth && triesForLogin == 3){
            return "Sorry bitch, i think you're trying to DDOS our PrintServer \n\n You're getting IP banned.... Sit down";
        }

        if (autheticator.authenticate(user, password)) {
            hasAuth = true;
            logger.info("Login succesful for user: \n" + user);
            return "Login succesful" + "\nWelcome  " + user + "\n\n" + printCommands();
        } else {
            triesForLogin++;
            return "Login failed...";
        }
    }

    @Override
    public void createUser(String newUser, String password) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        passwordStorage.createNewUser(newUser, password);
        logger.info("User created: " + newUser);
    }

    @Override
    public void updatePassword(String user, String password) throws RemoteException {
        if (!hasAuth) {
            throw new RemoteException("Unauthorized");
        }
        passwordStorage.updateExistingPassword(user, password);
        logger.info("Password updated for user: " + user);
    }


    @Override
    public void logout() throws RemoteException {
        hasAuth = false;
    };

}
