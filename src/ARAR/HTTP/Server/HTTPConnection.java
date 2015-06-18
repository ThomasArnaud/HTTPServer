package ARAR.HTTP.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
                            BufferedInputStream intput = new BufferedInputStream(new FileInputStream(requestedRessource));
                            StringBuilder responseBuilder = new StringBuilder();
                            responseBuilder
                                .append("HTTP/1.1 200 OK\r\n")
                                .append("Content-Length: " + requestedRessource.length() + "\r\n")
                                .append("Content-Type: " + URLConnection.guessContentTypeFromStream(intput) + "\r\n")
                                .append("\r\n")
                            ;
                        }
                    }
                    else this.socketWriter.write("404 Not Found\r\n");
                break;
                    
                default:
                    this.socketWriter.write("HTTP/1.1 501 Not Implemented\r\n");
            }
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
}
