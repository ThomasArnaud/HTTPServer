package arar.http.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public enum HTTPError
{
    BadRequest(400, "Bad Request"),
    NotFound(404, "Not Found"),
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented");
    
    /**
     * Holds the error code.
     */
    protected int errorCode;
    
    /**
     * Holds the error message.
     */
    protected String errorMessage;

    /**
     * Creates an HTTP error.
     * 
     * @param errorCode Error code.
     * @param errorMessage Error message.
     */
    HTTPError(int errorCode, String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets an array of bytes containing the error response.
     * 
     * @return Error response as an array of bytes.
     */
    public byte[] toByteArray()
    {
        ByteArrayOutputStream dataStream;
        DataOutputStream dataWriter;
        
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
