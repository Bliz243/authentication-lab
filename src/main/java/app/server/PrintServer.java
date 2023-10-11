package app.server;

import app.log.AppLogger;
import app.util.ConfigManager;
import org.apache.logging.log4j.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrintServer extends UnicastRemoteObject implements PrintService {

    private static final Logger logger = AppLogger.getLogger(PrintServer.class);

    public PrintServer() throws RemoteException {
        super();
    }

    @Override
    public void print(String filename, String printer) throws RemoteException {
        logger.info("Print request: {} on printer: {}", filename, printer);
    }

    @Override
    public String queue(String printer) throws RemoteException {
        System.out.println("Queue request for printer: " + printer);
        return "Queue for " + printer;
    }

    @Override
    public void topQueue(String printer, int job) throws RemoteException {
        System.out.println("Move job: " + job + " to top of queue on printer: " + printer);
    }

    @Override
    public void start() throws RemoteException {
        System.out.println("Print server started.");
    }

    @Override
    public void stop() throws RemoteException {
        System.out.println("Print server stopped.");
    }

    @Override
    public void restart() throws RemoteException {
        System.out.println("Print server restarted.");
    }

    @Override
    public String status(String printer) throws RemoteException {
        System.out.println("Status request for printer: " + printer);
        return "Status for " + printer;
    }

   @Override
    public String readConfig(String parameter) throws RemoteException {
        String value = ConfigManager.getConfigValue(parameter);
        logger.info("Read config for parameter: {} with value: {}", parameter, value);
        return value;
    }

    @Override
    public void setConfig(String parameter, String value) throws RemoteException {
        ConfigManager.setConfigValue(parameter, value);
        logger.info("Set config for parameter: {} to value: {}", parameter, value);
    }

}
