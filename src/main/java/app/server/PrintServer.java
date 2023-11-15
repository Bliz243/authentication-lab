package app.server;

import app.auth.interfaces.IAuthenticationService;
import app.auth.interfaces.IPasswordService;
import app.auth.interfaces.ITokenService;
import app.server.interfaces.IPrintServer;
import app.util.ConfigManager;

import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Logger;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PrintServer extends UnicastRemoteObject implements IPrintServer {

    private static final Logger logger = Logger.getLogger(PrintServer.class.getName());
    private int triesForLogin = 0;
    private boolean running = false;
    private boolean isRBAC;
    private String invalidSessionMsg = "Unauthorized, invalid session.\n";
    private String unauthorizedMsg = "Unauthorized, you don't have access to this command.\n";
    private String serverNotRunningMsg = "Server not running.\n";

    private String successMsg = "200";
    private transient ITokenService tokenService;
    private transient IPasswordService passwordService;
    private transient IAuthenticationService authenticationService;
    private transient LinkedList<PrintJob> printQueue = new LinkedList<>();
    private int nextJobId = 1;

    public PrintServer(IPasswordService passwordService, ITokenService tokenService,
            IAuthenticationService authenticationService, boolean isRBAC) throws IOException {
        super();
        this.tokenService = tokenService;
        this.passwordService = passwordService;
        this.authenticationService = authenticationService;
        this.isRBAC = isRBAC;
    }

    @Override
    public String print(String filename, String printer, String token) throws RemoteException {
        String msg = validateExecution("print", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        logger.info("Print request: " + filename + " on printer: " + printer + "\n");
        return "Printing " + filename + " on printer: " + printer + "\n";
    }

    @Override
    public String queue(String printer, String token) throws RemoteException {
        String msg = validateExecution("queue", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

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
        String msg = validateExecution("topqueue", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

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
            return "Moving job: " + jobId + " to top of queue on printer: " + printer + "\n";
        } else {
            return "Job not found\n";
        }
    }

    public String addToQueue(String filename, String printer, String token) throws RemoteException {
        String msg = validateExecution("addtoqueue", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        int newJobId = nextJobId++;
        PrintJob newJob = new PrintJob(newJobId, filename, printer);
        printQueue.addFirst(newJob);
        logger.info("Added job: " + newJobId + " to queue on printer: " + printer);
        return "Added job: " + newJobId + " to queue on printer: " + printer + "\n";
    }

    @Override
    public String start(String token) throws RemoteException {
        if (!tokenService.validateToken(token))
            return invalidSessionMsg;
        if (isRBAC) {
            if (!authenticationService.hasRBACPermission(tokenService.getUsername(token), "start"))
                return unauthorizedMsg;
        } else {
            if (!authenticationService.hasACLPermission(tokenService.getUsername(token), "start"))
                return unauthorizedMsg;
        }

        if (running)
            return "Server already running...\n";

        running = true;
        logger.info("Print server started.");
        return "Print server started." + "\n";
    }

    @Override
    public String stop(String token) throws RemoteException {
        String msg = validateExecution("stop", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        running = false;

        logger.info("Print server stopped.");
        return "Print server stopped\n";
    }

    @Override
    public String restart(String token) throws RemoteException, InterruptedException {
        String msg = validateExecution("restart", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        logger.info("Restaring server...");
        running = false;

        // bruh🤪🤣
        for (int i = 0; i < 100000000; i++) {
            i++;
        }

        running = true;
        logger.info("Print server restarted");

        return "Print server restarted\n";
    }

    @Override
    public String status(String printer, String token) throws RemoteException {
        String msg = validateExecution("status", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        logger.info("Status request for printer: " + printer);
        return "Status for " + printer + "\n";
    }

    @Override
    public String readConfig(String parameter, String token) throws RemoteException {
        String msg = validateExecution("readconfig", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        String value = ConfigManager.getInstance().getParameter(parameter); // adjusted line
        logger.info("Read config for parameter: " + parameter + " with value: " + value);
        return value + "\n";
    }

    @Override
    public String setConfig(String parameter, String value, String token) throws RemoteException {
        String msg = validateExecution("queue", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        ConfigManager.getInstance().setParameter(parameter, value); // adjusted line
        logger.info("Set config for parameter: " + parameter + " to value: " + value);
        return "Set config for parameter: " + parameter + " to value: " + value + "\n";
    }

    @Override
    public String printCommands(String token) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable commands:\n");
        if (!tokenService.validateToken(token)) {
            sb.append("authenticate <username> <password>: Authenticate user\n");
            sb.append("help: To see available commands\n");
            return sb.toString();
        }

        logger.info("Returned commands");
        if (isRBAC) {
            return authenticationService.getRBACAvailableCommands(tokenService.getUsername(token));

        } else {
            return authenticationService.getACLAvailableCommands(tokenService.getUsername(token));
        }
    }

    @Override
    public String authenticateUser(String user, String password) throws RemoteException {
        if (authenticationService.authenticate(user, password)) {
            String token = tokenService.generateToken(user);
            tokenService.storeToken(user, token);
            logger.info("Login succesful for user: \n" + user + "\n" + token);
            triesForLogin = 0;
            return "Login succesful" + "\nWelcome  " + user + "\n" + printCommands(token) + " " + token;
        } else {
            triesForLogin++;
            if (triesForLogin == 3) {
                triesForLogin = 0;
                return "Sorry bitch, i think you're trying to DDOS our PrintServer \n\n You're getting IP banned.... Sit down\n";
            }
            return "Login failed...\n";
        }
    }

    @Override
    public String createUser(String newUser, String password, String token)
            throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
        String msg = validateExecution("createuser", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        passwordService.createNewUser(newUser, password);
        try {
            if (isRBAC) {
                authenticationService.setUserRole(newUser, "user");
            } else {
                authenticationService.addUserToCommand(newUser, "print");
                authenticationService.addUserToCommand(newUser, "queue");
                authenticationService.addUserToCommand(newUser, "status");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("User created: " + newUser);
        return "User created: " + newUser + "\n";
    }

    @Override
    public String updatePassword(String user, String password, String token)
            throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException {
        String msg = validateExecution("updatepassword", token);
        if (!Objects.equals(msg, successMsg))
            return msg;

        passwordService.updateExistingPassword(user, password);
        logger.info("Password updated for user: " + user);
        return "Password updated for user:" + user + "\n";
    }

    @Override
    public String updateUserPermissions(String user, String role, String token) throws RemoteException {
        try {

            if (!isRBAC) {
                return "RCAB is not enabled\n";
            }
            String msg = validateExecution("queue", token);
            if (!Objects.equals(msg, successMsg))
                return msg;

            authenticationService.setUserRole(user, role);

            logger.info("Permission updated for user: " + user + " to role: " + role);
            return "Permission updated for user: " + user + " to role: " + role + "\n";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    public String logout(String token) throws RemoteException {
        if (token == null) {
            return "Not logged in\n";
        } else {
            tokenService.removeToken(tokenService.getUsername(token));
            return "Logging out\n";
        }
    };

    private String validateExecution(String operation, String token) {
        if (!tokenService.validateToken(token))
            return invalidSessionMsg;
        if (isRBAC) {
            if (!authenticationService.hasRBACPermission(tokenService.getUsername(token), operation))
                return unauthorizedMsg;
        } else {
            if (!authenticationService.hasACLPermission(tokenService.getUsername(token), operation))
                return unauthorizedMsg;
        }

        if (!running)
            return serverNotRunningMsg;

        return successMsg;
    }

    @Override
    public String addUserToCommand(String user, String command, String token) throws RemoteException {
        try {
            if (isRBAC) {
                return "ACL is not enabled\n";
            }
            String msg = validateExecution("queue", token);
            if (!Objects.equals(msg, successMsg))
                return msg;

            authenticationService.addUserToCommand(user, command);

            logger.info("Permission updated for user: " + user + " added command: " + command);
            return "Permission updated for user: " + user + " added command: " + command + "\n";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    public String removeUserFromCommand(String user, String command, String token) throws RemoteException {
        try {
            if (isRBAC) {
                return "ACL is not enabled\n";
            }
            String msg = validateExecution("queue", token);
            if (!Objects.equals(msg, successMsg))
                return msg;

            authenticationService.removeUserFromCommand(user, command);

            logger.info("Permission updated for user: " + user + " removed command: " + command + "\n");
            return "Permission updated for user: " + user + " removed command: " + command + "\n";
        } catch (IOException e) {
            return e.getMessage();
        }
    }

}
