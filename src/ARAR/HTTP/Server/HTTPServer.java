package ARAR.HTTP.Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class HTTPServer
{
    /**
     * Server's socket
     */
    protected ServerSocket socket;
    
    /**
     * Server's conf and logs root
     */
    protected File serverRoot;
    
    /**
     * Server's web root
     */
    protected File documentRoot;
    
    /**
     * Server's something
     */
    protected Set<String> directoryIndex;
    
    /**
     * Server's logger
     */
    protected Logger logger;
    
    /**
     * Creates new HTTP server with default config
     * @param port
     * @param serverRoot
     * @param documentRoot 
     */
    public HTTPServer(int port, String serverRoot, String documentRoot)
    {
        this(port, serverRoot, documentRoot, new HashSet<String>(Arrays.asList("index.html","index.htm")), "error.log");
    }
    
    /**
     * Creates new HTTP server with custom config
     * @param port
     * @param serverRoot
     * @param documentRoot
     * @param directoryIndex
     * @param errorLog 
     */
    public HTTPServer(int port, String serverRoot, String documentRoot, Set<String> directoryIndex, String errorLog)
    {
        this.serverRoot = new File(serverRoot);
        this.documentRoot = new File(documentRoot);
        this.directoryIndex = directoryIndex;
        //this.logger = new Logger(this.documentRoot, errorLog);
        
        if(!this.serverRoot.exists() || !this.serverRoot.isDirectory())
            throw new RuntimeException("Server root is invalid.");
        
        if(!this.documentRoot.exists() || !this.documentRoot.isDirectory())
            throw new RuntimeException("Document root is invalid.");
        
        try 
        { 
            this.socket = new ServerSocket(port);
        } 
        catch (IOException e) 
        {
            throw new RuntimeException("Port " + port + " is already taken.", e);
        }
    }
    
    /**
     * Launches et runs a server
     */
    public void run()
    {
        while(true)
        {
            try 
            {
                Socket clientSocket = this.socket.accept();
                HTTPConnection connection = new HTTPConnection(this, clientSocket);
                connection.start();
            } 
            catch (IOException e) 
            {
                /*try 
                {
                    BufferedWriter errorLogWriter = new BufferedWriter(new FileWriter(this.errorLog));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
                    errorLogWriter.write(dateFormat.format(Calendar.getInstance().getTime()));
                    errorLogWriter.write("Cannot accept client: " + e.getMessage());
                    errorLogWriter.close();
                } 
                catch (IOException ex) 
                {
                    System.err.println(ex.getMessage());
                }*/
            }
        }
    }
    
    public File getDocumentRoot()
    {
        return this.documentRoot;
    }
    
    public Set<String> getDirectoryIndex()
    {
        return this.directoryIndex;
    }
}
