package Util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SystemLogger {
    
    private static final Logger logger = Logger.getLogger("LuxefitSystemLogger");

    static {
        try {
            
            Files.createDirectories(Paths.get("logs"));
            
            FileHandler fh = new FileHandler("logs/luxefit_system.log", true); 

            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);

            logger.setUseParentHandlers(false);

            logger.setLevel(Level.ALL); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    
}
