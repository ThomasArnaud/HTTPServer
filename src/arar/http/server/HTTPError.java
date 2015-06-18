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
    Forbidden(403, "Forbidden"),
    NotFound(404, "Not Found"),
    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented");
    
    protected byte[] encodedError;

    /**
     * Creates an HTTP error.
     * 
     * @param errorCode Error code.
     * @param errorMessage Error message.
     */
    HTTPError(int errorCode, String errorMessage)
    {
        try
        {
            ByteArrayOutputStream dataStream;
            DataOutputStream dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
            
            // Encodage de la réponse avec une classe adaptée
            dataWriter.writeBytes("HTTP/1.1 ");
            dataWriter.writeBytes(Integer.toString(errorCode));
            dataWriter.writeBytes(" ");
            dataWriter.writeBytes(errorMessage);
            dataWriter.writeBytes("\r\n");
            
            this.encodedError = dataStream.toByteArray();
        }
        catch(IOException e)
        {
            // Encodage de la réponse via une chaîne de caractères
            this.encodedError = ("HTTP/1.1" + errorCode + " " + errorMessage + "\r\n").getBytes();
        }
    }
    
    /**
     * Gets an array of bytes containing the error response.
     * 
     * @return Error response as an array of bytes.
     */
    public byte[] toByteArray()
    {
        return this.encodedError;
    }
}
