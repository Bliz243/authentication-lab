package app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface IPrintServer extends Remote {

    // Basic methods for the printserver
    String print(String filename, String printer, String token) throws RemoteException;
    String queue(String printer, String token) throws RemoteException;
    String topQueue(String printer, int job, String token) throws RemoteException;
    String addToQueue(String filename, String printer, String token) throws RemoteException;
    String start(String token) throws RemoteException;
    String stop(String token) throws RemoteException;
    String restart(String token) throws RemoteException, InterruptedException;
    String status(String printer, String token) throws RemoteException;
    String readConfig(String parameter, String token) throws RemoteException;
    String setConfig(String parameter, String value, String token) throws RemoteException;
    String printCommands(String token) throws RemoteException;

    // Extra methods for controlling printserver flow
    String authenticateUser(String user, String password) throws RemoteException;
    String createUser(String newUser, String password, String token) throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
    String updatePassword(String user, String newPassowrd, String token) throws RemoteException, NoSuchAlgorithmException, InvalidKeySpecException;
    String logout(String token) throws RemoteException;

}
