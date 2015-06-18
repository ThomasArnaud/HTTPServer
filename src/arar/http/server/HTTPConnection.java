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
                // Tester si la requête est bien formée
                String requestType = request.substring(0, request.indexOf(" ")).toUpperCase();

                switch(requestType)
                {
                    case "GET":
                        URL uri = new URL(request.substring(4, request.lastIndexOf(" HTTP/1.")));
                        File requestedRessource = new File(this.server.getDocumentRoot(), uri.getPath());

                        if(requestedRessource.exists())
                            if(requestedRessource.isFile())
                                this.socketWriter.write(this.writeGETResponse(requestedRessource));
                            else
                                this.socketWriter.write(HTTPError.Forbidden.toByteArray());
                        else
                            this.socketWriter.write(HTTPError.NotFound.toByteArray());
                    break;

                    default:
                        this.socketWriter.write(HTTPError.NotImplemented.toByteArray());
                }
            }
            else
            {
                this.socketWriter.write(HTTPError.BadRequest.toByteArray());
            }
            
            this.socketWriter.flush();
        }
        catch(MalformedURLException e)
        {
            try
            {
                this.socketWriter.write(HTTPError.BadRequest.toByteArray());
                this.socketWriter.flush();
            }
            catch(IOException ex)
            {
                this.server.getLogger().log("An error happened: " + ex.getMessage());
            }
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
     * 
     * @param requestedResource
     * @return 
     */
    protected byte[] writeGETResponse(File requestedResource)
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter;
        BufferedInputStream inputStream;
        int readByte;
        
        // Manipulateur de tableau d'octets
        dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        
        try
        {
            // Ouverture du fichier demandé
            inputStream = new BufferedInputStream(new FileInputStream(requestedResource));
            
            // Ecriture de l'en-tête
            dataWriter.writeBytes("HTTP/1.1 200 OK\r\n");
            dataWriter.writeBytes("Content-Length: " + requestedResource.length() + "\r\n");
            dataWriter.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromStream(inputStream) + "\r\n");
            dataWriter.writeBytes("\r\n");
            
            // Ecriture du contenu de la ressource
            while((readByte = inputStream.read()) != -1)
                dataWriter.writeByte(readByte);
            
            // Fermeture de la ressource
            inputStream.close();
            
            if(this.server.isDebug())
            {
                String request = new String(dataStream.toByteArray());
                System.out.println(request.substring(0, request.indexOf("\r\n\r\n")));
            }
            
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
            // Lecture du contenu de la requête
            do
            {
                dataWriter.writeByte(this.socketReader.read());
            }
            while(this.socketReader.available() > 0);
            
            if(this.server.isDebug())
                System.out.println(this.socket.getInetAddress() + ":" + this.socket.getPort() + " " + new String(dataStream.toByteArray()).trim());
            
            return new String(dataStream.toByteArray());
        }
        catch(IOException e)
        {
            this.server.getLogger().log("Couldn't read request : " + e.getMessage());
        }
        
        return null;
    }
}
