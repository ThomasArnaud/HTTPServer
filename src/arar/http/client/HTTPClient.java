package arar.http.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public class HTTPClient
{
    /**
     * Nothing happened.
     */
    public static final int SUCCESS = 0;
    
    /**
     * Local file already exists.
     */
    public static final int ERROR_FILE_ALREADY_EXISTS = -1;
    
    /**
     * Error using a socket.
     */
    public static final int ERROR_SOCKET = -2;
    
    /**
     * Error writing file.
     */
    public static final int ERROR_IO = -3;
    
    /**
     * Performs a GET request.
     * 
     * @param remoteFile Path to the remote file from the server's root.
     * @param localFile Path to the local file.
     * @param serverAddress Server's address.
     * @param serverPort Server's port.
     * @return 
     */
    public static int get(String remoteFile, String localFile, InetAddress serverAddress, int serverPort)
    {
        File file = new File(localFile);
        
        // Le fichier existe-t-il déjà ?
        if(file.exists())
            return HTTPClient.ERROR_FILE_ALREADY_EXISTS;
        
        // Variables pour la communication
        Socket socket = null;
        
        try
        {
            // Création du socket
            socket = new Socket(serverAddress, serverPort);
            
            // Envoi de la requête
            socket.getOutputStream().write(HTTPClient.encodeGet(remoteFile, serverAddress, serverPort));
            
            // Attente et lecture de la réponse
            InputStream input = socket.getInputStream();
            DataOutputStream headerWriter = new DataOutputStream(new ByteArrayOutputStream());
            StringBuilder responseHeader = new StringBuilder();
            int readByte;
            
            // Lecture de l'en-tête
            do
            {
                // Lecture d'un octet
                headerWriter.write(readByte = input.read());
                responseHeader.append((char) readByte);
            }
            while(input.available() > 0 && responseHeader.toString().lastIndexOf("\r\n\r\n") == -1);
            
            if(input.available() > 0)
            {
                // Il reste encores données à lire c'est qu'il n'y a pas eu d'erreur côté serveur
                // On peut les écrire dans le fichier
                BufferedOutputStream fileOutput = null;
                
                try
                {
                    // Ouverture du fichier
                    fileOutput = new BufferedOutputStream(new FileOutputStream(file));
                    
                    do
                    {
                        fileOutput.write(input.read());
                    }
                    while(input.available() > 0);
                }
                catch(FileNotFoundException e)
                {
                    return HTTPClient.ERROR_IO;
                }
                catch(IOException e)
                {
                    return HTTPClient.ERROR_IO;
                }
                finally
                {
                    try
                    {
                        // Fermeture du fichier
                        if(fileOutput != null)
                            fileOutput.close();
                    }
                    catch(IOException e)
                    {
                        return HTTPClient.ERROR_IO;
                    }
                }
            }
            else
            {
                // Il n'y a plus rien à lire, on extrait le code de l'erreur
                String header = responseHeader.toString();
                int boundaryIndex;
                
                return Integer.parseInt(header.substring(boundaryIndex = header.indexOf(" ") + 1, header.indexOf(" ", boundaryIndex))); 
            }
        }
        catch(IOException e)
        {
            return HTTPClient.ERROR_SOCKET;
        }
        finally
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
                }
                catch(IOException e)
                {
                    return HTTPClient.ERROR_SOCKET;
                }
            }
        }
        
        return HTTPClient.SUCCESS;
    }
    
    /**
     * Encodes a GET request into a byte array.
     * 
     * @param remoteFile Path to the remote file from the server's root.
     * @param serverAddress Server's address.
     * @param serverPort Server's port.
     * @return 
     */
    protected static byte[] encodeGet(String remoteFile, InetAddress serverAddress, int serverPort)
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        
        try
        {
            dataWriter.writeBytes("GET http://");
            dataWriter.writeBytes(serverAddress.getHostAddress());
            dataWriter.writeBytes("/");
            dataWriter.writeBytes(remoteFile.trim());
            dataWriter.writeBytes(" HTTP/1.1\r\n");
            
            return dataStream.toByteArray();
        }
        catch(IOException e)
        {
            return null;
        }
    }
    
    /**
     * Performs a HEAD request.
     */
    public static void head()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    /**
     * Performs a POST request.
     */
    public static void post()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    /**
     * Performs a PUT request.
     */
    public static void put()
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
