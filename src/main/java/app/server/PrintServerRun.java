package app.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.logging.Logger;

import app.auth.AuthenticationService;
import app.auth.EncryptionService;
import app.auth.PasswordService;
import app.auth.TokenService;
import app.auth.interfaces.IAuthenticationService;
import app.auth.interfaces.IEncryptionService;
import app.auth.interfaces.IPasswordService;
import app.auth.interfaces.ITokenService;
import app.server.interfaces.IPrintServer;
import app.util.Color;

public class PrintServerRun {

    private static final Logger logger = Logger.getLogger(PrintServer.class.getName());

    public static void main(String args[]) {
        try {

            IEncryptionService encryptionService = new EncryptionService();
            ITokenService tokenService = new TokenService();
            IPasswordService passwordService = new PasswordService(encryptionService);
            IAuthenticationService authenticationService = new AuthenticationService(passwordService);

            logger.info("Choose " + Color.blue("ACL") + " or " + Color.blue("RBAC") + ":");
            Scanner in = new Scanner(System.in);

            boolean isRBAC;
            while (true) {
                String input = in.nextLine();
                if (input.equalsIgnoreCase("rbac")) {
                    isRBAC = true;
                    break;
                } else if (input.equalsIgnoreCase("acl")) {
                    isRBAC = false;
                    break;
                }
            }
            ;

            IPrintServer printServer = new PrintServer(passwordService, tokenService,
                    authenticationService, isRBAC);
            UnicastRemoteObject.unexportObject(printServer, true);
            IPrintServer stub = (IPrintServer) UnicastRemoteObject.exportObject(printServer, 0);

            LocateRegistry.createRegistry(5000);
            Naming.rebind("rmi://localhost:5000/PrintServer", stub);
            logger.info(Color.green("Started RMI server"));

        } catch (Exception e) {
            logger.severe(Color.red("Server exception)") + ":" + e.toString());
            e.printStackTrace();
        }
    }
}