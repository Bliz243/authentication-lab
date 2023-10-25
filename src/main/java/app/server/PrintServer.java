package app.server;

import app.auth.Authenticator;
import app.auth.PasswordStorage;
import app.auth.PasswordVerifier;
import app.auth.TokenService;
import app.auth.TokenStorage;
import app.util.ConfigManager;

import java.util.LinkedList;
import java.util.logging.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {

    private int triesForLogin = 0;
    private boolean running = false;
    private String unauthorizedMsg = "Unauthorized, invalid session";
    private String serverNotRunningMsg = "Server not running...";

    private static final Logger logger = Logger.getLogger(PrintServer.class.getName());
    private TokenService tokenService = new TokenService();
    private static final PasswordStorage passwordStorage = new PasswordStorage();
    private static final PasswordVerifier passwordVerifier = new PasswordVerifier(passwordStorage);
    private static final Authenticator autheticator = new Authenticator(passwordVerifier);
    private LinkedList<PrintJob> printQueue = new LinkedList<>();
    private int nextJobId = 1;

    public PrintServer() throws RemoteException {
        super();
    }

    @Override
    public String print(String filename, String printer, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        logger.info("Print request: " + filename + " on printer: " + printer);
        return "Printing " + filename + " on printer: " + printer;
    }

    @Override
    public String queue(String printer, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        StringBuilder queueStatus = new StringBuilder();
        queueStatus.append("Queue for printer ").append(printer).append(":\n");

        for (PrintJob job : printQueue) {
            if (job.getPrinter().equals(printer)) {
                queueStatus.append(job).append("\n");
            }
        }

        logger.info("Queue request for printer: " + printer);
        return queueStatus.toString();
    }

    @Override
    public String topQueue(String printer, int jobId, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        PrintJob jobToMove = null;
        for (PrintJob job : printQueue) {
            if (job.getId() == jobId && job.getPrinter().equals(printer)) {
                jobToMove = job;
                break;
            }
        }

        if (jobToMove != null) {
            printQueue.remove(jobToMove);
            printQueue.addFirst(jobToMove);
            logger.info("Move job: " + jobId + " to top of queue on printer: " + printer);
            return "Moving job: " + jobId + " to top of queue on printer: " + printer;
        } else {
            return "Job not found";
        }
    }

    public String addToQueue(String filename, String printer, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        int newJobId = nextJobId++;
        PrintJob newJob = new PrintJob(newJobId, filename, printer);
        printQueue.addFirst(newJob);
        logger.info("Added job: " + newJobId + " to queue on printer: " + printer);
        return "Added job: " + newJobId + " to queue on printer: " + printer;
    }

    @Override
    public String start(String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (running)
            return "Server already running...";

        running = true;
        logger.info("Print server started.");
        return "Print server started." + "\n" + printCommands(token);
    }

    @Override
    public String stop(String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        running = false;

        logger.info("Print server stopped.");
        return "Print server stopped";
    }

    @Override
    public String restart(String token) throws RemoteException, InterruptedException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        // bruh🤪🤣
        running = false;
        logger.info("Restaring server...");
        wait(10000);
        running = true;
        logger.info("Print server restarted");

        return "Print server restarted";
    }

    @Override
    public String status(String printer, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        System.out.println("Status request for printer: " + printer);
        return "Status for " + printer;
    }

    @Override
    public String readConfig(String parameter, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        String value = ConfigManager.getInstance().getParameter(parameter); // adjusted line
        logger.info("Read config for parameter: " + parameter + " with value: " + value);
        return value;
    }

    @Override
    public String setConfig(String parameter, String value, String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;
        ConfigManager.getInstance().setParameter(parameter, value); // adjusted line
        logger.info("Set config for parameter: " + parameter + " to value: " + value);
        return "Set config for parameter: " + parameter + " to value: " + value;
    }

    // TODO Update the available command list
    @Override
    public String printCommands(String token) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable commands:\n");
        if (!tokenService.validateToken(token)) {
            sb.append("authenticate <username> <password>: Authenticate user");
            sb.append("help: To see available commands");
            return sb.toString();
        }
        if (!running) {
            sb.append("start: Starts the print server.\n");
            return sb.toString();
        }
        sb.append("stop: Stops the print server.\n");
        sb.append("restart: Restarts the print server.\n");
        sb.append("print <filename> <printer>: Prints the file.\n");
        sb.append("queue <printer>: Shows print queue. \n");
        sb.append("topQueue <printer> <job>: Moves job to top of queue.\n");
        sb.append("addToQueue <filename> <printer>: Adds to printer queue.\n");
        sb.append("status <printer>: Shows printer status. \n");
        sb.append("readConfig <parameter>: Reads configuration.\n");
        sb.append("setConfig <paramter> <value>: Sets configuration.\n");
        sb.append("createUser <username> <password>: Creates a new user\n");
        sb.append("updatePassword <username> <password>: Update user password\n");
        sb.append("logout: Logs current user out\n");

        logger.info("Returned commands");
        return sb.toString();
    }

    @Override
    public String authenticateUser(String user, String password) throws RemoteException {

        if (autheticator.authenticate(user, password)) {
            String token = tokenService.generateToken(user);
            TokenStorage.getInstance().storeToken(user, token);
            logger.info("Login succesful for user: \n" + user + "\n" + token);
            triesForLogin = 0;
            return "Login succesful" + "\nWelcome  " + user + "\n" + printCommands(token) + " " + token;
        } else {
            triesForLogin++;
            if (triesForLogin == 3) {
                triesForLogin = 0;
                return "Sorry bitch, i think you're trying to DDOS our PrintServer \n\n You're getting IP banned.... Sit down";
            }
            return "Login failed...";
        }
    }

    @Override
    public String createUser(String newUser, String password, String token)
            throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        passwordStorage.createNewUser(newUser, password);
        logger.info("User created: " + newUser);
        return "User created: " + newUser;
    }

    @Override
    public String updatePassword(String user, String password, String token)
            throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!tokenService.validateToken(token))
            return unauthorizedMsg;

        if (!running)
            return serverNotRunningMsg;

        passwordStorage.updateExistingPassword(user, password);
        logger.info("Password updated for user: " + user);
        return "Password updated for user:" + user;
    }

    @Override
    public String logout(String token) throws RemoteException {
        if (token == null)
            return "Not logged in";
        else
            return "Logging out";
    };
}
