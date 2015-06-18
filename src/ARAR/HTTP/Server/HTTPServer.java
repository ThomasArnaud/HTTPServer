package arar.http.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

/**
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
     * Server's logger
     */
    protected Logger logger;
    
    /**
     * Creates new HTTP server with default config.
     * 
     * @param port
     * @param serverRoot
     * @param documentRoot 
     */
    public HTTPServer(int port, String serverRoot, String documentRoot)
    {
        this(port, serverRoot, documentRoot, "error.log");
    }
    
    /**
     * Creates new HTTP server with custom config.
     * 
     * @param port Port to listen to.
     * @param serverRoot Config files and logs directory.
     * @param documentRoot Web directory.
     * @param errorLog Error log's name.
     */
    public HTTPServer(int port, String serverRoot, String documentRoot, String errorLog)
    {
        this.serverRoot = new File(serverRoot);
        this.documentRoot = new File(documentRoot);
        this.logger = new Logger(this.serverRoot, errorLog);
        
        if(!this.serverRoot.exists() || !this.serverRoot.isDirectory())
            throw new RuntimeException("Server root is invalid.");
        
        if(!this.documentRoot.exists() || !this.documentRoot.isDirectory())
            throw new RuntimeException("Document root is invalid.");
        
        try 
        { 
            this.socket = new ServerSocket(port);
        } 
        catch(IOException e) 
        {
            throw new RuntimeException("Port " + port + " is already taken.", e);
        }
    }
    
    /**
     * Launches and runs a server
     */
    public void run()
    {
        while(true)
        {
            try 
            {
                HTTPConnection connection = new HTTPConnection(this, this.socket.accept());
                connection.start();
            } 
            catch(IOException e) 
            {
                this.logger.log("Cannot accept client :" + e.getMessage());
            }
        }
    }
    
    /**
     * Gets the web directory root.
     * 
     * @return Reference to the web directory root.
     */
    public File getDocumentRoot()
    {
        return this.documentRoot;
    }
    
    /**
     * 
     * @return 
     */
    public Logger getLogger()
    {
        return this.logger;
    }
}
