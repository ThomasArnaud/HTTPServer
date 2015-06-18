package arar.http.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public enum HTTPError
{
    BadRequest(400, "Bad Request"),
    NotFound(404, "Not Found"),
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented");
    
    protected int errorCode;
    
    protected String errorMessage;

    HTTPError(int errorCode, String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public int getErrorCode()
    {
        return this.errorCode;
    }
    
    public String getErrorMessage()
    {
        return this.errorMessage;
    }
    
    public byte[] getResponse()
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter;
        BufferedInputStream inputStream;
        
        // Manipulateur de tableau d'octets
        dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
        
        try
        {
            // Ecriture de la réponse
            dataWriter.writeBytes("HTTP/1.1 ");
            dataWriter.writeBytes(Integer.toString(this.errorCode));
            dataWriter.writeBytes(" ");
            dataWriter.writeBytes(this.errorMessage);
            
            return dataStream.toByteArray();
        }
        catch(IOException e)
        {
        }
        
        return null;
    }
}
