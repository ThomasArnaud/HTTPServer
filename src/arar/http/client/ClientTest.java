package arar.http.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public abstract class ClientTest
{
    public static void main(String[] args)
    {
        try
        {
            // Ouverture du socket
            Socket s = new Socket(InetAddress.getByName("127.0.0.1"), 10080);
            
            // Variables
            ByteArrayOutputStream dataStream;
            DataOutputStream dataWriter;
            int readByte;
            
            // Ecriture et envoi de la requête
            dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
            dataWriter.writeBytes("GET http://localhost/Test.txt HTTP/1.1\r\n");
            System.out.println("-> " + new String(dataStream.toByteArray()).trim());
            s.getOutputStream().write(dataStream.toByteArray());
            s.getOutputStream().flush();
            
            // Attente et affichage de la réponse
            dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
            
            do
            {
                dataWriter.writeByte(s.getInputStream().read());
            }
            while(s.getInputStream().available() > 0);
            
            System.out.println("<- " + new String(dataStream.toByteArray()).trim());
            
            // Fermeture du socket
            s.close();
        }
        catch(IOException e)
        {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
