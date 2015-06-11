package ARAR.HTTP.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class HTTPConnection extends Thread
{
    protected HTTPServer server;
    
    protected Socket socket;
    
    protected Writer socketWriter;
    
    protected Reader socketReader;
    
    public HTTPConnection(HTTPServer server, Socket socket)
    {
        this.server = server;
        this.socket = socket;
        try 
        {
            this.socketWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.socketReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } 
        catch (IOException e) 
        {
            
        }
    }
    
    @Override
    public void run()
    {
       //lecture de la requête 
        
       //analyse de la requête
        
       //écriture de la réponse
    }
}
