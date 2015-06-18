package arar.http.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class Logger
{
    /**
     * Log date format.
     */
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss] ");;
    
    /**
     * Reference to the log file.
     */
    protected File errorLog;
    
    /**
     * Creates a new logger.
     * 
     * @param serverRoot Server root containing logs and config files.
     * @param errorLog Name of the error log file.
     */
    public Logger(File serverRoot, String errorLog)
    {
        this.errorLog = new File(serverRoot, errorLog);
        
        if(this.errorLog.exists() && this.errorLog.isDirectory())
            throw new RuntimeException("errorLog is invalid.");
    }
    
    /**
     * Appends a message to the error log file.
     * 
     * @param message Message to append.
     */
    public void log(String message)
    {
        try 
        {
            BufferedWriter errorLogWriter = new BufferedWriter(new FileWriter(this.errorLog));
            errorLogWriter.write(Logger.dateFormat.format(Calendar.getInstance().getTime()));
            errorLogWriter.write(message);
            errorLogWriter.close();
        }
        catch (IOException ex)
        {
            System.err.println("Cannot write into log : " + ex.getMessage());
        }
    }
}