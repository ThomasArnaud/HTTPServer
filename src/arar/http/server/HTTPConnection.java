package arar.http.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class HTTPConnection extends Thread
{
    /**
     * Reference to the server.
     */
    protected HTTPServer server;
    
    /**
     * Reference to the client socket.
     */
    protected Socket socket;
    
    /**
     * Output stream used to write to the client.
     */
    protected BufferedOutputStream socketWriter;
    
    /**
     * Input stream used to read from the client.
     */
    protected BufferedInputStream socketReader;
    
    /**
     * Creates a new connection in a separate thread.
     * 
     * @param server Reference to the server.
     * @param socket Reference to the new connected socket.
     */
    public HTTPConnection(HTTPServer server, Socket socket)
    {
        this.server = server;
        this.socket = socket;
        
        try 
        {
            this.socketWriter = new BufferedOutputStream(this.socket.getOutputStream());
            this.socketReader = new BufferedInputStream(this.socket.getInputStream());
        } 
        catch(IOException e) 
        {
            this.server.getLogger().log("Couldn't get a stream: " + e.getMessage());
        }
    }
    
    /**
     * Reads the request and sends the response.
     */
    @Override
    public void run()
    {
        try
        {
            // Lecture de la requête
            String request = this.readRequest();
            
            if(request != null && request.length() > 0)
            {
                // Extraction de la méthode
                String method = request.substring(0, request.indexOf(" ")).toUpperCase();

                switch(method)
                {
                    case "GET":
                        // Extraction du fichier à partir de l'URL
                        URL uri = new URL(request.substring(4, request.lastIndexOf(" HTTP/1.")));
                        File requestedRessource = new File(this.server.getDocumentRoot(), uri.getPath());
                        
                        // Vérification sur la ressource demandée
                        if(requestedRessource.exists())
                            if(requestedRessource.isFile())
                                this.sendResponse(this.encodeResponse(requestedRessource));
                            else
                                this.sendResponse(HTTPError.Forbidden.toByteArray());
                        else
                            this.sendResponse(HTTPError.NotFound.toByteArray());
                    break;

                    default:
                        // Toutes les autres méthodes ne sont pas implémentées
                        this.sendResponse(HTTPError.NotImplemented.toByteArray());
                }
            }
            else
            {
                // La requête est vide
                this.sendResponse(HTTPError.BadRequest.toByteArray());
            }
            
            this.socketWriter.flush();
        }
        catch(MalformedURLException e)
        {
            this.sendResponse(HTTPError.BadRequest.toByteArray());
            this.server.getLogger().log("Couldn't parse URL: " + e.getMessage());
        }
        catch(IOException e) 
        {
            this.server.getLogger().log("An error happened: " + e.getMessage());
        }
        finally
        {
            try
            {
                this.socket.close();
            }
            catch(IOException e)
            {
                this.server.getLogger().log("Cannot close socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Sends the response to the client.
     * 
     * @param encodedResponse Response encoded as an array of bytes.
     */
    protected void sendResponse(byte[] encodedResponse)
    {
        if(this.server.isDebug())
        {
            String response = new String(encodedResponse);
            System.out.println("-> " + this.socket.getInetAddress() + ":" + this.socket.getPort() + " " + response.substring(0, response.indexOf("\r\n")));
        }
        
        try
        {
            this.socketWriter.write(encodedResponse);
            this.socketWriter.flush();
        }
        catch(IOException e)
        {
            this.server.getLogger().log("Couldn't send response: " + e.getMessage());
        }
    }
    
    /**
     * Reads the request sent by the client.
     * 
     * @return Decoded request or <code>null</code> if an error happened.
     */
    protected String readRequest()
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        
        try
        {
            do
            {
                dataWriter.writeByte(this.socketReader.read());
            }
            while(this.socketReader.available() > 0);
            
            if(this.server.isDebug())
                System.out.println("<- " + this.socket.getInetAddress() + ":" + this.socket.getPort() + " " + new String(dataStream.toByteArray()).trim());
            
            return new String(dataStream.toByteArray());
        }
        catch(IOException e)
        {
            this.server.getLogger().log("Couldn't read request : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Encodes a 200 OK response with the contents of the requested resource.
     * 
     * @param requestedResource Reference to the requested resource.
     * @return Encoded response as an array of bytes or <code>null</code> if an error happened.
     */
    protected byte[] encodeResponse(File requestedResource)
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        BufferedInputStream inputStream;
        int readByte;
        
        try
        {
            // Ouverture du fichier demandé
            inputStream = new BufferedInputStream(new FileInputStream(requestedResource));
            
            // Ecriture de l'en-tête
            dataWriter.writeBytes("HTTP/1.1 200 OK\r\n");
            dataWriter.writeBytes("Content-Length: " + requestedResource.length() + "\r\n");
            dataWriter.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(requestedResource.getName()) + "\r\n");
            dataWriter.writeBytes("\r\n");
            
            // Ecriture du contenu de la ressource
            while((readByte = inputStream.read()) != -1)
                dataWriter.writeByte(readByte);
            
            // Fermeture de la ressource
            inputStream.close();
            
            return dataStream.toByteArray();
        }
        catch(FileNotFoundException e)
        {
            this.server.getLogger().log("Requested resource \"" + requestedResource.getAbsolutePath() + "\" cannot be found.");
            return HTTPError.NotFound.toByteArray();
        }
        catch(IOException e)
        {
            this.server.getLogger().log("Couldn't create GET response: " + e.getMessage());
        }
        
        return null;
    }
}
