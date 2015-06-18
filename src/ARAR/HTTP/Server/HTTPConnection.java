package ARAR.HTTP.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class HTTPConnection extends Thread
{
    protected HTTPServer server;
    
    protected Socket socket;
    
    protected BufferedOutputStream socketWriter;
    
    protected BufferedInputStream socketReader;
    
    public HTTPConnection(HTTPServer server, Socket socket)
    {
        this.server = server;
        this.socket = socket;
        try 
        {
            this.socketWriter = new BufferedOutputStream(this.socket.getOutputStream());
            this.socketReader = new BufferedInputStream(this.socket.getInputStream());
        } 
        catch (IOException e) 
        {
            
        }
    }
    
    @Override
    public void run()
    {
        try 
        {
            //read the request
            // voir TFTP byte array stream String request = this.socketReader.();
            //analyse the request
            String request = this.readRequest();
            String requestType = request.substring(0, request.indexOf(" ")).toUpperCase();
            
            switch(requestType)
            {
                case "GET":
                    URL uri = new URL(request.substring(4, request.lastIndexOf(" HTTP/")));
                    File requestedRessource = new File(this.server.getDocumentRoot(),uri.getPath());
                    
                    if(requestedRessource.exists())
                    {
                        if(requestedRessource.isFile())
                        {
                            this.socketWriter.write(writeGETResponse(requestedRessource));
                        }
                    }
                    else this.socketWriter.write(HTTPError.NotFound.getResponse());
                break;
                    
                default:
                    this.socketWriter.write(HTTPError.NotImplemented.getResponse());
            }
            
            this.socket.close();
        } 
        catch (IOException ex) 
        {
            this.server.getLogger().log("Une erreur est survenue : " + ex.getMessage());
        }
    }
    
    /**
     * 
     * @param requestedResource
     * @return 
     */
    protected byte[] writeGETResponse(File requestedResource)
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter;
        BufferedInputStream inputStream;
        
        // Manipulateur de tableau d'octets
        dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        
        try
        {
            // Ouverture du fichier demandé
            inputStream = new BufferedInputStream(new FileInputStream(requestedResource));
            
            // Ecriture de l'en-tête
            dataWriter.writeBytes("HTTP/1.1 200 OK\r\n");
            dataWriter.writeBytes("Content-Length: " + requestedResource.length() + "\r\n");
            dataWriter.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromStream(inputStream));
            dataWriter.writeBytes("\r\n");
            
            // Ecriture du contenu de la ressource
            while(inputStream.available() > 0)
                dataWriter.writeByte(inputStream.read());
            
            // Fermeture de la ressource
            inputStream.close();
            
            return dataStream.toByteArray();
        }
        catch(FileNotFoundException e)
        {
            this.server.getLogger().log("Requested resource \"" + requestedResource.getAbsolutePath() + "\" cannot be found.");
        }
        catch(IOException e)
        {
            this.server.getLogger().log("Couldn't create GET response: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 
     * @return 
     */
    protected String readRequest()
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter;
        
        // Manipulateur de tableau d'octets
        dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        
        try
        {
            // Attente de la requête
            while(this.socketReader.available() == 0);
            
            // Lecture du contenu de la requête
            while(this.socketReader.available() > 0)
                dataWriter.writeByte(this.socketReader.read());
            
            return new String(dataStream.toByteArray());
        }
        catch(IOException e)
        {
            this.server.getLogger().log("Couldn't read request : " + e.getMessage());
        }
        
        return null;
    }
}
