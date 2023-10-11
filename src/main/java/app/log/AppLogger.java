package app.log;

import java.util.logging.Logger;

public class AppLogger {

    public static Logger getLogger(String className) {
        return Logger.getLogger(className);
    }
}
