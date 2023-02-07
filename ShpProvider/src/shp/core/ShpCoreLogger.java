package shp.core;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ShpCoreLogger
{
    
    /* ------------------------------------------------------------------------ */
    /* CLASS MEMBERS                                                            */ 
    /* ------------------------------------------------------------------------ */
    
    /**
     * The logger object.
     * */
    private static Logger logger;
    
    /**
     * The prefix string, that is placed before every line of the log message.
     * */
    private static final String PREFIX_STRING = "    ";
    
    /**
     * Denotes, if logging is enabled, or not.
     * */
    private static boolean isLoggingEnabled = true;
    
    
    /* ------------------------------------------------------------------------ */
    /* PUBLIC FUNCTIONS                                                         */ 
    /* ------------------------------------------------------------------------ */
    
    /**
     * Disables the logging.
     * */
    public static void disableLogging()
    {
        isLoggingEnabled = false;
    }
    
    /**
     * Checks whether logging is enabled, or not.
     * 
     * @return true, if logging is enabled.
     * */
    public static boolean isLoggingEnabled()
    {
        return isLoggingEnabled;
    }
    
    /**
     * Gets the logger instance for logging.
     * 
     * @return The logger.
     * 
     * @throws IOException
     * */
    public static Logger getLogger()
    {
        if(logger == null)
        {
            logger = Logger.getLogger("OsmCoreLogger");
            
            if(isLoggingEnabled)
            {
                FileHandler fileHandler;
                try
                {
                    fileHandler = new FileHandler("log/shp_core_log.txt");
                    
                    fileHandler.setFormatter(new Formatter()
                    {
                        public String format(LogRecord record)
                        {
                            Date date = new Date();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            String dateString = "" + calendar.get(Calendar.YEAR)         + "."
                                                   + calendar.get(Calendar.MONTH) + 1    + "."
                                                   + calendar.get(Calendar.DAY_OF_MONTH) + " "
                                                   + calendar.get(Calendar.HOUR)         + ":"
                                                   + calendar.get(Calendar.MINUTE)       + ":"
                                                   + calendar.get(Calendar.SECOND)       + ":"
                                                   + calendar.get(Calendar.MILLISECOND);
                            
                            /* 
                             * We want to place PREFIX_STRING before
                             * each row of the log message. 
                             */
                            String logMessage = record.getMessage();
                             
                            /* Cut newlines from the end of the message. */
                            logMessage = logMessage.replaceAll("\\n+$", "");
                            
                            /* Replace multiple newlines with one. */
                            logMessage = logMessage.replaceAll("\\n+", "\n");
                            
                            /* Add prefix string before each line. */
                            logMessage = logMessage.replaceAll("\\n", "\n" + PREFIX_STRING);
                            
                            /* Add the prefix before the first line. */
                            logMessage = PREFIX_STRING + logMessage;
                            
                            return dateString                   + " "    +
                                   record.getLevel()            + ": "   +
                                   record.getSourceClassName()  + "."    +
                                   record.getSourceMethodName() + "(): " + "\n" +
                                   logMessage                   + "\n\n";
                        }
                    });
                    
                    logger.addHandler(fileHandler);
                    logger.setUseParentHandlers(false);
                }
                catch(SecurityException e)
                {
                    System.err.println("Could not log to file due to SecurityException!");
                }
                catch(IOException e)
                {
                    System.err.println("Could not log to file due to IOException!");
                }
            }
            else
            {
                logger.setLevel(Level.OFF);
            }
        }
        
        return logger;
    }
    
    /**
     * Converts a byte array into a hex string.
     * 
     * @param array    The array to dump.
     * 
     * @return The hex string representation of the byte array.
     * */
    public static String toHexString(byte[] array)
    {
        StringBuilder result = new StringBuilder(array.length * 2);
        
        int numOfBytesAppended = 0;
        for(byte b: array)
        {
            result.append(String.format("0x%02X ", b));
            numOfBytesAppended++;
            
            if(numOfBytesAppended % 16 == 0)
            {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
    
     
    /* ------------------------------------------------------------------------ */
    /* PRIVATE FUNCTIONS                                                        */ 
    /* ------------------------------------------------------------------------ */
    
    /**
     * This is a singleton class. Instantiating is controlled through the getLogger()
     * method.
     * */
    private ShpCoreLogger()
    {
    }
}
