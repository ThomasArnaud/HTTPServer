/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package arar.http.client;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sydney
 */
public class ClientHttp {
    
    private String pageURL;
    private Socket socketClient;
    private int port;
    private InetAddress host;
    private String Header;
    private String HtmlPage;
    private String file;

    public ClientHttp(InetAddress host,int port) throws IOException
    {
        this.socketClient = new Socket (host,port);
        this.port = port;
    }
    
    public String constructGet (String fileName) {
        this.file = fileName;
        String getRequest = "GET ";
        getRequest+="http:/";
        getRequest+=socketClient.getInetAddress().toString();
        getRequest+="/";
        getRequest+=file;
        getRequest+=" HTTP/1.1";
        getRequest+="\r\n";
        return getRequest;
    }
    
    public void sendGet()
    {
        try {
               // Variables
            ByteArrayOutputStream dataStream;
            DataOutputStream dataWriter;
            int readByte;
            dataWriter = new DataOutputStream(dataStream = new ByteArrayOutputStream());
            dataWriter.writeBytes(this.constructGet(file));
            //envoie du get
            System.out.println("-> " + new String(dataStream.toByteArray()).trim());
            socketClient.getOutputStream().write(dataStream.toByteArray());
            socketClient.getOutputStream().flush();
                    
             } 
        catch (IOException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     
    //on lit la réponse et on écrit dans un fichier html
    public String read_Reponse () throws IOException {
        try {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socketClient.getInputStream()) 
                );
                    String line ;
                    StringBuilder sb = new StringBuilder();
                while((line = in.readLine()) != null){
                    sb.append(line);
                }
                            String response = sb.toString();
                            System.out.println(sb);
                            BufferedWriter output = null;
                            File fich= new File("index.html");
                            output = new BufferedWriter(new FileWriter(fich));
                            Header = response.substring(0, response.indexOf("\r\n\r\n"));
                            HtmlPage = response.substring(response.indexOf("\r\n\r\n"));
                            output.write(HtmlPage);
                            output.close();
                            in.close();
                            return sb.toString();
            } 
        catch (IOException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
                socketClient.close();
        }
        return null;
    }
    
    //ouvrir la réponse dans un navigateur web par défaut
    public void OpenHtmlPage () throws IOException, URISyntaxException
    {
        URI uri = new URI("index.html");
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(uri);
    }
    
    
    public void run ()
    {
        sendGet();
        try {
                this.read_Reponse(); // lecture de la réponse
                this.OpenHtmlPage(); // une fois que tout c'est bien passé on peut afficher dans le navigateur web
        } catch (IOException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getHeader() {
        return Header;
    }

    public void setHeader(String Header) {
        this.Header = Header;
    }

    public String getHtmlPage() {
        return HtmlPage;
    }

    public void setHtmlPage(String HtmlPage) {
        this.HtmlPage = HtmlPage;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            ClientHttp client = new ClientHttp(InetAddress.getByName("127.0.0.1"),10080);
            client.setFile("test.txt");
            client.run();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}