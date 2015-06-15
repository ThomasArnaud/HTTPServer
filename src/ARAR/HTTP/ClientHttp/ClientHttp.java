/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1310563
 */
public class ClientHttp {
    
    private String pageURL;
    private Socket socketClient;
    private int port;
    private InetAddress host;
    private String Header;
    private String HtmlPage;

    public ClientHttp(InetAddress host) throws IOException
    {
        this.socketClient = new Socket (host,port);
    }
    
    public String constructGet(String url) {
        String getRequest = "GET";
        getRequest+=url;
        getRequest+="HTTP/1.1";
        return getRequest;
    }
    
    public void sendGet()
    {
        try {
                    PrintWriter out = new PrintWriter(
                         new BufferedWriter(
                            new OutputStreamWriter(socketClient.getOutputStream())), 
                         true); 
                    
                    //envoie du GET
                    out.write(constructGet(pageURL));
                    out.flush();
                    
                    //attente de la réponse
                    
             } 
        catch (IOException ex) {
            Logger.getLogger(ClientHttp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     public void sendGet(String url)
    {
        this.pageURL = url;
        try {  
                    PrintWriter out = new PrintWriter(
                         new BufferedWriter(
                            new OutputStreamWriter(socketClient.getOutputStream())), 
                         true); 
                    
                    //envoie du GET
                    out.write(constructGet(pageURL));
                    out.flush();
                    
                    //attente de la réponse
                    
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
                            FileWriter fstream;                
                            fstream = new FileWriter("MyHtml.html");
                            String response = sb.toString();
                            Header = response.substring(0, response.indexOf("\r\n\r\n"));
                            HtmlPage = response.substring(response.indexOf("\r\n\r\n"));
                            BufferedWriter out = new BufferedWriter(fstream);
                            out.write(HtmlPage); 
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
        URI uri = new URI("MyHtml.html");
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(uri);
    }
    
    public void run ()
    {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
    }
    
}
