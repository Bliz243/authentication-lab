package app.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
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

public class PrintServerRun {

    private static final Logger logger = Logger.getLogger(PrintServer.class.getName());

    public static void main(String args[]) {
        try {
            /*
             * System.setProperty("javax.net.ssl.keyStore",
             * ConfigManager.getInstance().getParameter("keyStore") );
             * System.setProperty("javax.net.ssl.keyStorePassword", "keystore");
             * 
             * SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
             * SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();
             */

            IEncryptionService encryptionService = new EncryptionService();
            ITokenService tokenService = new TokenService();
            IPasswordService passwordService = new PasswordService(encryptionService);
            IAuthenticationService authenticationService = new AuthenticationService(passwordService);

            IPrintServer printServer = new PrintServer(passwordService, tokenService,
                    authenticationService);
            UnicastRemoteObject.unexportObject(printServer, true);
            IPrintServer stub = (IPrintServer) UnicastRemoteObject.exportObject(printServer, 0);

            LocateRegistry.createRegistry(5000);
            Naming.rebind("rmi://localhost:5000/PrintServer", stub);

        } catch (Exception e) {
            logger.severe("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}