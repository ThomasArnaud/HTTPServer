package arar.http.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

/**
 *
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class Logger
{
    protected File errorLog;
    
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");;
            
    public Logger(File documentRoot, String errorLog)
    {
        this.errorLog = new File(documentRoot, errorLog);
        
        if(this.errorLog.exists() && this.errorLog.isDirectory())
            throw new RuntimeException("errorLog is invalid.");
    }
            
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


