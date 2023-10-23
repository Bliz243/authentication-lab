package app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface IPrintServer extends Remote {

    // Basic methods for the printserver
    String print(String filename, String printer) throws RemoteException;
    String queue(String printer) throws RemoteException;
    String topQueue(String printer, int job) throws RemoteException;
    String addToQueue(String filename, String printer) throws RemoteException;
    String start() throws RemoteException;
    String stop() throws RemoteException;
    String restart() throws RemoteException, InterruptedException;
    String status(String printer) throws RemoteException;
    String readConfig(String parameter) throws RemoteException;
    String setConfig(String parameter, String value) throws RemoteException;
    String printCommands() throws RemoteException;

    // Extra methods for controlling printserver flow
    String authenticateUser(String user, String password) throws RemoteException;
    String createUser(String newUser, String password) throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
    String updatePassword(String user, String newPassowrd) throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
    String logout() throws RemoteException;
}
